package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.email.EmailContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailTemplateService {

    private final SpringTemplateEngine templateEngine;

    @Autowired
    public EmailTemplateService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public EmailContent recipientFundsDeposited(
            String recipient,
            String date,
            String amount,
            String sender,
            String reference,
            String message) {

        Context context = new Context();
        context.setVariable("recipient", recipient);
        context.setVariable("date", date);
        context.setVariable("amount", amount);
        context.setVariable("sender", sender);
        context.setVariable("reference", reference);
        context.setVariable("message", message);

        String htmlContent = templateEngine.process("recipient_funds_deposited", context); //resolver1
        String textContent = templateEngine.process("text_recipient_funds_deposited", context); //resolver2

        String subject = "JJBank External Transfer: Your funds from " + sender + " have been deposited in your account.";

        return new EmailContent(subject, htmlContent, textContent);
    }

    public EmailContent recipientFundsPending(
            String recipient,
            String date,
            String amount,
            String sender,
            String reference,
            String message,
            String link) {

        Context context = new Context();
        context.setVariable("recipient", recipient);
        context.setVariable("date", date);
        context.setVariable("amount", amount);
        context.setVariable("sender", sender);
        context.setVariable("reference", reference);
        context.setVariable("message", message);
        context.setVariable("link", link);

        String htmlContent = templateEngine.process("recipient_funds_pending", context);
        String textContent = templateEngine.process("text_recipient_funds_pending", context);

        String subject = "JJBank External Transfer: You have received " + amount + "$J from " + sender;

        return new EmailContent(subject, htmlContent, textContent);
    }

    public EmailContent recipientFundsPendingReminder(
            String recipient,
            String date,
            String expiry,
            String amount,
            String sender,
            String reference,
            String message,
            String link) {

        Context context = new Context();
        context.setVariable("recipient", recipient);
        context.setVariable("date", date);
        context.setVariable("expiry", expiry);
        context.setVariable("amount", amount);
        context.setVariable("sender", sender);
        context.setVariable("reference", reference);
        context.setVariable("message", message);
        context.setVariable("link", link);

        String htmlContent = templateEngine.process("recipient_funds_pending_reminder", context);
        String textContent = templateEngine.process("text_recipient_funds_pending_reminder", context);

        String subject = "JJBank External Transfer - Reminder: You have received " + amount + "$J from " + sender;

        return new EmailContent(subject, htmlContent, textContent);
    }

    public EmailContent recipientTransferCancelled(
            String recipient,
            String date,
            String amount,
            String sender,
            String reference){

        Context context = new Context();
        context.setVariable("recipient", recipient);
        context.setVariable("date", date);
        context.setVariable("amount", amount);
        context.setVariable("sender", sender);
        context.setVariable("reference", reference);

        String htmlContent = templateEngine.process("recipient_transfer_cancelled", context);
        String textContent = templateEngine.process("text_recipient_transfer_cancelled", context);

        String subject = "JJBank External Transfer: Your transfer from " + sender + " has been cancelled.";

        return new EmailContent(subject, htmlContent, textContent);
    }

    public EmailContent recipientTransferExpired(
            String recipient,
            String date,
            String amount,
            String sender,
            String reference) {

        Context context = new Context();
        context.setVariable("recipient", recipient);
        context.setVariable("date", date);
        context.setVariable("amount", amount);
        context.setVariable("sender", sender);
        context.setVariable("reference", reference);

        String htmlContent = templateEngine.process("recipient_transfer_expired", context);
        String textContent = templateEngine.process("text_recipient_transfer_expired", context);

        String subject = "JJBank External Transfer: Your transfer from " + sender + " has expired.";

        return new EmailContent(subject, htmlContent, textContent);
    }

    public EmailContent senderTransferDeposited(
            String sender,
            String date,
            String amount,
            String recipient,
            String reference,
            String message) {

        Context context = new Context();
        context.setVariable("sender", sender);
        context.setVariable("date", date);
        context.setVariable("amount", amount);
        context.setVariable("recipient", recipient);
        context.setVariable("reference", reference);
        context.setVariable("message", message);


        String htmlContent = templateEngine.process("sender_transfer_deposited", context);
        String textContent = templateEngine.process("text_sender_transfer_deposited", context);

        String subject = "JJBank External Transfer: Your transfer to " + recipient + " has been successfully deposited.";

        return new EmailContent(subject, htmlContent, textContent);
    }

    public EmailContent senderTransferDeclined(
            String sender,
            String date,
            String amount,
            String recipient,
            String reference) {

        Context context = new Context();
        context.setVariable("sender", sender);
        context.setVariable("date", date);
        context.setVariable("amount", amount);
        context.setVariable("recipient", recipient);
        context.setVariable("reference", reference);

        String htmlContent = templateEngine.process("sender_transfer_declined", context);
        String textContent = templateEngine.process("text_sender_transfer_declined", context);

        String subject = "JJBank External Transfer: " + recipient + " has declined your transfer.";

        return new EmailContent(subject, htmlContent, textContent);
    }

    public EmailContent senderTransferExpired(
            String sender,
            String date,
            String amount,
            String recipient,
            String reference) {

        Context context = new Context();
        context.setVariable("sender", sender);
        context.setVariable("date", date);
        context.setVariable("amount", amount);
        context.setVariable("recipient", recipient);
        context.setVariable("reference", reference);

        String htmlContent = templateEngine.process("sender_transfer_expired", context);
        String textContent = templateEngine.process("text_sender_transfer_expired", context);

        String subject = "JJBank External Transfer: Your transfer to " + recipient + " has expired.";

        return new EmailContent(subject, htmlContent, textContent);
    }

}
