package dev.jordanjoseph.backend.infra;

import dev.jordanjoseph.backend.dto.email.EmailContent;
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

}
