package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.email.EmailContent;
import dev.jordanjoseph.backend.infra.EmailSender;
import dev.jordanjoseph.backend.model.ExternalTransfer;
import dev.jordanjoseph.backend.repository.ExternalTransferRepository;
import dev.jordanjoseph.backend.repository.TransferTokenRepository;
import dev.jordanjoseph.backend.util.InstantToDateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ExternalTransferService {

    private ExternalTransferRepository externalTransferRepository;

    private TransferTokenRepository transferTokenRepository;

    private EmailSender emailSender;

    private EmailTemplateService emailTemplateService;

    private InstantToDateConverter instantToDateConverter;

    @Autowired
    public ExternalTransferService(
            ExternalTransferRepository externalTransferRepository,
            TransferTokenRepository transferTokenRepository,
            EmailSender emailSender,
            EmailTemplateService emailTemplateService) {

        this.externalTransferRepository = externalTransferRepository;
        this.transferTokenRepository = transferTokenRepository;
        this.emailSender = emailSender;
        this.emailTemplateService = emailTemplateService;
        this.instantToDateConverter = new InstantToDateConverter();
    }

    public void expirePendingTransfers() {
        externalTransferRepository.findByStatusAndExpiresAtBefore(ExternalTransfer.Status.PENDING, Instant.now())
                .forEach(externalTransfer -> {
                    externalTransfer.setStatus(ExternalTransfer.Status.EXPIRED);

                    //send message to user
                    EmailContent emailContent = switch (externalTransfer.getType()) {
                        case TRANSFER_IN -> expirePendingIncomingTransfer(externalTransfer);
                        case TRANSFER_OUT -> expirePendingOutgoingTransfer(externalTransfer);
                        default -> throw new IllegalStateException("Unexpected value: " + externalTransfer.getType());
                    };
                    emailSender.sendFromNoReply(
                            externalTransfer.getAccount().getUser().getEmail(),
                            "Transfer  Expired - " + externalTransfer.getReference(),
                            emailContent.textContent(),
                            emailContent.htmlContent()
                    );

                });
    }

    public void sendReminders() {
        externalTransferRepository.findByStatusAndReminderAtBeforeAndReminderSentFalse(ExternalTransfer.Status.PENDING, Instant.now())
                .forEach(externalTransfer -> {
                    //send message to recipient
                    externalTransfer.setReminderSent(true);
                });
    }

    private EmailContent expirePendingIncomingTransfer(ExternalTransfer incoming) {
        //invalidate transfer token
        transferTokenRepository.findByIncomingTransferId(incoming.getId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Transfer Token could not be found for external transfer id: " + incoming.getId()))
                .setInvalid(true);

        //get complementary outgoing transfer and sender/recipient names
        ExternalTransfer outgoing = getComplementaryTransfer(incoming.getReference(), incoming.getAccount().getId());
        String sender = outgoing.getAccount().getUser().getFullName();
        String recipient = incoming.getAccount().getUser().getFullName();

        //generate email templates
        return emailTemplateService.recipientTransferExpired(
                recipient,
                instantToDateConverter.toAbbreviatedFullDate(incoming.getCreatedAt()),
                incoming.getAmount().toPlainString(),
                sender,
                incoming.getReference()
        );
    }

    private EmailContent expirePendingOutgoingTransfer(ExternalTransfer outgoing) {
        //get complementary ingoing transfer and sender/recipient names
        ExternalTransfer incoming = getComplementaryTransfer(outgoing.getReference(), outgoing.getAccount().getId());
        String recipient = incoming.getAccount().getUser().getFullName();
        String sender = outgoing.getAccount().getUser().getFullName();

        //generate email templates
        return emailTemplateService.senderTransferExpired(
                sender,
                instantToDateConverter.toAbbreviatedFullDate(outgoing.getCreatedAt()),
                outgoing.getAmount().toPlainString(),
                recipient,
                outgoing.getReference()
        );
    }

    private ExternalTransfer getComplementaryTransfer(String reference, UUID accountId) {
        List<ExternalTransfer> transfers = externalTransferRepository
                .findByReferenceAndAccountIdNot(reference, accountId);

        for(ExternalTransfer transfer : transfers) {
            System.out.println(transfer.getType());
            System.out.println(transfer.getId());
        }

        if(transfers.isEmpty()) {
            throw new IllegalStateException("No complementary transfer found");
        }

        if(transfers.size() > 1) {
            throw new IllegalStateException("Expected one complementary transfer but found multiple");
        }

        return transfers.getFirst();
    }
}
