package dev.jordanjoseph.backend.infra;

import dev.jordanjoseph.backend.dto.email.EmailContent;
import dev.jordanjoseph.backend.dto.transfer.event.TransferExpiredEvent;
import dev.jordanjoseph.backend.dto.transfer.event.TransferInitiatedEvent;
import dev.jordanjoseph.backend.service.EmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransferEventListener {

    private final EmailTemplateService emailTemplateService;
    private final EmailSender emailSender;

    @Autowired
    public TransferEventListener(EmailTemplateService emailTemplateService, EmailSender emailSender) {
        this.emailTemplateService = emailTemplateService;
        this.emailSender = emailSender;
    }

    @TransactionalEventListener
    public void onTransferInitiated(TransferInitiatedEvent event) {
        EmailContent emailContent = emailTemplateService.recipientFundsPending(
                event.recipientDisplayName(),
                event.date(),
                event.amount(),
                event.senderFullName(),
                event.sharedRef(),
                event.transferLink());

        emailSender.sendFromNoReply(
                event.recipientEmail(),
                emailContent.subject(),
                emailContent.textContent(),
                emailContent.htmlContent());
    }

    @TransactionalEventListener
    public void onTransferExpired(TransferExpiredEvent event) {
        switch (event.emailTo().toLowerCase()) {
            case "sender" -> {
                EmailContent emailContent = emailTemplateService.senderTransferExpired(
                        event.senderFullName(),
                        event.date(),
                        event.amount(),
                        event.recipientDisplayName(),
                        event.reference());

                emailSender.sendFromNoReply(
                        event.email(),
                        emailContent.subject(),
                        emailContent.textContent(),
                        emailContent.htmlContent());
            }
            case "recipient" -> {
                EmailContent emailContent = emailTemplateService.recipientTransferExpired(
                        event.recipientDisplayName(),
                        event.date(),
                        event.amount(),
                        event.senderFullName(),
                        event.reference());

                emailSender.sendFromNoReply(
                        event.email(),
                        emailContent.subject(),
                        emailContent.textContent(),
                        emailContent.htmlContent());
            }
            default -> throw new IllegalStateException("Expired Transfer Event - Unexpected value for 'emailTo': " + event.emailTo());

        }
    }

}
