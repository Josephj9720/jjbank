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
            String reference) {

        Context context = new Context();
        context.setVariable("recipient", recipient);
        context.setVariable("date", date);
        context.setVariable("amount", amount);
        context.setVariable("sender", sender);
        context.setVariable("reference", reference);

        String htmlContent = templateEngine.process("recipient_funds_deposited", context); //resolver1
        String textContent = templateEngine.process("text_recipient_funds_deposited", context); //resolver2

        return new EmailContent(htmlContent, textContent);
    }

    public EmailContent recipientFundsPending(
            String recipient,
            String date,
            String amount,
            String sender,
            String reference,
            String link) {

        Context context = new Context();
        context.setVariable("recipient", recipient);
        context.setVariable("date", date);
        context.setVariable("amount", amount);
        context.setVariable("sender", sender);
        context.setVariable("reference", reference);
        context.setVariable("link", link);

        String htmlContent = templateEngine.process("recipient_funds_pending", context);
        String textContent = templateEngine.process("text_recipient_funds_pending", context);

        return new EmailContent(htmlContent, textContent);
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

        return new EmailContent(htmlContent, textContent);
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

        return new EmailContent(htmlContent, textContent);
    }

    public EmailContent senderTransferDeposited(
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

        String htmlContent = templateEngine.process("sender_transfer_deposited", context);
        String textContent = templateEngine.process("text_sender_transfer_deposited", context);

        return new EmailContent(htmlContent, textContent);
    }

    public EmailContent senderTransferCancelled(
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

        String htmlContent = templateEngine.process("sender_transfer_cancelled", context);
        String textContent = templateEngine.process("text_sender_transfer_cancelled", context);

        return new EmailContent(htmlContent, textContent);
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

        return new EmailContent(htmlContent, textContent);
    }

}
