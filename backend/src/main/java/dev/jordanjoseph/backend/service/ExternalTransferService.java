package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.transfer.event.TransferExpiredEvent;
import dev.jordanjoseph.backend.dto.transfer.event.TransferReminderDateReachedEvent;
import dev.jordanjoseph.backend.model.*;
import dev.jordanjoseph.backend.repository.ExternalTransferRepository;
import dev.jordanjoseph.backend.repository.TransferTokenRepository;
import dev.jordanjoseph.backend.util.HashUtil;
import dev.jordanjoseph.backend.util.InstantToDateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ExternalTransferService {

    private ExternalTransferRepository externalTransferRepository;

    private TransferTokenRepository transferTokenRepository;

    private ApplicationEventPublisher eventPublisher;

    private InstantToDateConverter instantToDateConverter;

    @Value("${jjb.frontend.base.url}")
    private String frontEndBaseUrl;

    @Autowired
    public ExternalTransferService(
            ExternalTransferRepository externalTransferRepository,
            TransferTokenRepository transferTokenRepository,
            ApplicationEventPublisher eventPublisher) {

        this.externalTransferRepository = externalTransferRepository;
        this.transferTokenRepository = transferTokenRepository;
        this.eventPublisher = eventPublisher;
        this.instantToDateConverter = new InstantToDateConverter();
    }

    @Transactional
    public void expirePendingTransfers() {
        externalTransferRepository.findByStatusAndTypeAndExpiresAtBefore(ExternalTransfer.Status.PENDING, Transaction.Type.TRANSFER_OUT,Instant.now())
                .forEach(outgoing -> {
                    ExternalTransfer incoming = getComplementaryTransfer(outgoing.getReference(), outgoing.getAccount().getId(), outgoing.getId());

                    //expire the external transfers
                    outgoing.setStatus(ExternalTransfer.Status.EXPIRED);
                    incoming.setStatus(ExternalTransfer.Status.EXPIRED);

                    //refund sender
                    Account senderAccount = outgoing.getAccount();
                    senderAccount.setBalance(senderAccount.getBalance().add(outgoing.getAmount()));

                    //invalidate

                    //get sender and recipient account IDs
                    UUID senderAccountId = outgoing.getAccount().getId();
                    UUID recipientAccountId = incoming.getAccount().getId();

                    //set up variable to hold recipient name and email
                    String recipientName;
                    String recipientEmail;

                    //get TransferToken
                    TransferToken token = transferTokenRepository.findByIncomingTransferId(incoming.getId())
                            .orElse(null);

                    if(token == null) {
                        //the recipient is a User of JJBank, no transfer token created for a requested transfer

                        //get recipient User
                        User recipient = incoming.getAccount().getUser();

                        //set recipient name and email
                        recipientName = recipient.getFullName();
                        recipientEmail = recipient.getEmail();

                    } else if(senderAccountId.equals(recipientAccountId)) {
                        //the recipient has not registered an account with JJBank, delete their record of the transaction
                        //only need the record for the sender who is a JJBank user
                        transferTokenRepository.delete(token);
                        externalTransferRepository.delete(incoming);

                        //set recipient name and email
                        recipientName = token.getRecipient().getDisplayName();
                        recipientEmail = token.getRecipient().getEmail();

                    } else {
                        //the recipient is a User of JJBank, keep the record, invalidate transfer token
                        token.setInvalid(true);

                        //get recipient User
                        User recipient = incoming.getAccount().getUser();

                        //set recipient name and email
                        recipientName = recipient.getFullName();
                        recipientEmail = recipient.getEmail();
                    }

                    //get sender User
                    User sender = outgoing.getAccount().getUser();

                    //retrieve necessary information for events
                    String expiryDate = instantToDateConverter.toShortWeekdayLongDate(outgoing.getExpiresAt());
                    String amount = outgoing.getAmount().toPlainString();
                    String sharedRef = outgoing.getReference();

                    //fire events
                    eventPublisher.publishEvent(new TransferExpiredEvent(
                            "recipient",
                            recipientEmail,
                            recipientName,
                            expiryDate,
                            amount,
                            sender.getFullName(),
                            sharedRef)
                    );

                    eventPublisher.publishEvent(new TransferExpiredEvent(
                            "sender",
                            sender.getEmail(),
                            recipientName,
                            expiryDate,
                            amount,
                            sender.getFullName(),
                            sharedRef)
                    );

                });
    }

    @Transactional
    public void sendReminders() {
        externalTransferRepository.findByStatusAndTypeAndReminderAtBeforeAndReminderSentFalse(ExternalTransfer.Status.PENDING, Transaction.Type.TRANSFER_OUT,Instant.now())
                .forEach(outgoing -> {
                    ExternalTransfer incoming = getComplementaryTransfer(outgoing.getReference(), outgoing.getAccount().getId(), outgoing.getId());

                    //get sender and recipient account IDs
                    UUID senderAccountId = outgoing.getAccount().getId();
                    UUID recipientAccountId = incoming.getAccount().getId();

                    //setup variable to hold recipient name and email
                    String recipientName;
                    String recipientEmail;

                    //get TransferToken and renew token hash
                    TransferToken token = transferTokenRepository.findByIncomingTransferId(incoming.getId())
                            .orElse(null);

                    //token is null if recipient requested transfer,
                    //only send reminders to recipients when sender has initiated the transfer
                    if(token != null) {

                        //set reminder sent to true
                        outgoing.setReminderSent(true);
                        incoming.setReminderSent(true);

                        HashUtil hashUtil = new HashUtil();
                        String newTransferTokenString = UUID.randomUUID().toString();
                        token.setTokenHash(hashUtil.sha256(newTransferTokenString));

                        if(senderAccountId.equals(recipientAccountId)) {
                            //recipient has not registered yet, use Contact info from TransferToken
                            recipientName = token.getRecipient().getDisplayName();
                            recipientEmail = token.getRecipient().getEmail();
                        } else {
                            //recipient is a User of JJBank, get recipient User and info
                            User recipient = incoming.getAccount().getUser();
                            recipientName = recipient.getFullName();
                            recipientEmail = recipient.getEmail();
                        }

                        //get Sender User
                        User sender = outgoing.getAccount().getUser();

                        //retrieve necessary info to fire event
                        String date = instantToDateConverter.toShortWeekdayLongDate(outgoing.getCreatedAt());
                        String expiry = instantToDateConverter.toLongDate(outgoing.getExpiresAt());
                        String amount = outgoing.getAmount().toPlainString();
                        String senderFullName = sender.getFullName();
                        String sharedRef = outgoing.getReference();
                        String message = outgoing.getMessage();

                        //create new transfer link with transfer token
                        String transferLinkPath = "/transfer/claim/" + newTransferTokenString;
                        String transferLink = frontEndBaseUrl + transferLinkPath;

                        //notify recipient
                        eventPublisher.publishEvent(new TransferReminderDateReachedEvent(
                                recipientEmail,
                                recipientName,
                                date,
                                expiry,
                                amount,
                                senderFullName,
                                sharedRef,
                                message,
                                transferLink)
                        );
                    }

                });
    }

    private ExternalTransfer getComplementaryTransfer(String reference, UUID accountId, UUID externalTransferId) {
        List<ExternalTransfer> transfers = externalTransferRepository
                .findByReferenceAndAccountIdNot(reference, accountId);

        for(ExternalTransfer transfer : transfers) {
            System.out.println(transfer.getType());
            System.out.println(transfer.getId());
        }

        if(transfers.isEmpty()) {
            //recipient might not have registered with JJBank so the accountId is same as sender's
            transfers = externalTransferRepository
                    .findByReferenceAndAccountIdAndIdNot(reference, accountId, externalTransferId);

            if(transfers.isEmpty()) {
                throw new IllegalStateException("No complementary transfer found");

            }
        }

        if(transfers.size() > 1) {
            throw new IllegalStateException("Expected one complementary transfer but found multiple");
        }

        return transfers.getFirst();
    }
}
