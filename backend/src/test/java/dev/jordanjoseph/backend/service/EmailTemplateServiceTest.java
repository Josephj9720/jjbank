package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.email.EmailContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailTemplateServiceTest {

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailTemplateService service;

    @Test
    void shouldGenerateRecipientFundsDepositedTemplateWithCorrectContentAndContext(){

        //define behaviour
        when(templateEngine.process(eq("recipient_funds_deposited"), any(Context.class)))
                .thenReturn("<html>HTML</html>");
        when(templateEngine.process(eq("text_recipient_funds_deposited"), any(Context.class)))
                .thenReturn("TEXT");

        String recipient = "Ryu";
        String date = "SUN, FEB 22, 2026";
        String amount = "$50 (J)";
        String sender = "Yellow Knife";
        String reference = "TX-123456";

        //act - call method
        EmailContent result = service.recipientFundsDeposited(recipient, date, amount, sender, reference);

        //assert DTO values
        assertNotNull(result, "EmailContent should not be null");
        assertEquals("<html>HTML</html>", result.htmlContent(), "HTML content mismatch");
        assertEquals("TEXT", result.textContent(), "TEXT content mismatch");

        //capture html Context arguments to inspect variables
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("recipient_funds_deposited"), contextCaptor.capture());
        Context htmlContext = contextCaptor.getValue();

        assertEquals(recipient, htmlContext.getVariable("recipient"));
        assertEquals(date, htmlContext.getVariable("date"));
        assertEquals(amount, htmlContext.getVariable("amount"));
        assertEquals(sender, htmlContext.getVariable("sender"));
        assertEquals(reference, htmlContext.getVariable("reference"));

        //capture text Context arguments to inspect variables
        ArgumentCaptor<Context> textContextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("text_recipient_funds_deposited"), textContextCaptor.capture());
        Context textContext = textContextCaptor.getValue();

        assertEquals(recipient, textContext.getVariable("recipient"));
        assertEquals(date, textContext.getVariable("date"));
        assertEquals(amount, textContext.getVariable("amount"));
        assertEquals(sender, textContext.getVariable("sender"));
        assertEquals(reference, textContext.getVariable("reference"));

        //verify that both templates were called exactly once
        verify(templateEngine, times(1)).process(eq("recipient_funds_deposited"), any(Context.class));
        verify(templateEngine, times(1)).process(eq("text_recipient_funds_deposited"), any(Context.class));

        //verify no extra interactions
        verifyNoMoreInteractions(templateEngine);
    }

    @Test
    void shouldGenerateRecipientFundsPendingTemplateWithCorrectContentAndContext(){

        //define behaviour
        when(templateEngine.process(eq("recipient_funds_pending"), any(Context.class)))
                .thenReturn("<html>HTML</html>");
        when(templateEngine.process(eq("text_recipient_funds_pending"), any(Context.class)))
                .thenReturn("TEXT");

        String recipient = "Ryu";
        String date = "SUN, FEB 22, 2026";
        String amount = "$50 (J)";
        String sender = "Yellow Knife";
        String reference = "TX-123456";
        String link = "www.jordanjoseph.dev";

        //act - call method
        EmailContent result = service.recipientFundsPending(recipient, date, amount, sender, reference, link);

        //assert DTO values
        assertNotNull(result, "EmailContent should not be null");
        assertEquals("<html>HTML</html>", result.htmlContent(), "HTML content mismatch");
        assertEquals("TEXT", result.textContent(), "TEXT content mismatch");

        //capture html Context arguments to inspect variables
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("recipient_funds_pending"), contextCaptor.capture());
        Context htmlContext = contextCaptor.getValue();

        assertEquals(recipient, htmlContext.getVariable("recipient"));
        assertEquals(date, htmlContext.getVariable("date"));
        assertEquals(amount, htmlContext.getVariable("amount"));
        assertEquals(sender, htmlContext.getVariable("sender"));
        assertEquals(reference, htmlContext.getVariable("reference"));
        assertEquals(link, htmlContext.getVariable("link"));

        //capture text Context arguments to inspect variables
        ArgumentCaptor<Context> textContextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("text_recipient_funds_pending"), textContextCaptor.capture());
        Context textContext = textContextCaptor.getValue();

        assertEquals(recipient, textContext.getVariable("recipient"));
        assertEquals(date, textContext.getVariable("date"));
        assertEquals(amount, textContext.getVariable("amount"));
        assertEquals(sender, textContext.getVariable("sender"));
        assertEquals(reference, textContext.getVariable("reference"));
        assertEquals(link, textContext.getVariable("link"));

        //verify that both templates were called exactly once
        verify(templateEngine, times(1)).process(eq("recipient_funds_pending"), any(Context.class));
        verify(templateEngine, times(1)).process(eq("text_recipient_funds_pending"), any(Context.class));

        //verify no extra interactions
        verifyNoMoreInteractions(templateEngine);
    }
    @Test
    void shouldGenerateRecipientTransferCancelledTemplateWithCorrectContentAndContext(){

        //define behaviour
        when(templateEngine.process(eq("recipient_transfer_cancelled"), any(Context.class)))
                .thenReturn("<html>HTML</html>");
        when(templateEngine.process(eq("text_recipient_transfer_cancelled"), any(Context.class)))
                .thenReturn("TEXT");

        String recipient = "Ryu";
        String date = "SUN, FEB 22, 2026";
        String amount = "$50 (J)";
        String sender = "Yellow Knife";
        String reference = "TX-123456";

        //act - call method
        EmailContent result = service.recipientTransferCancelled(recipient, date, amount, sender, reference);

        //assert DTO values
        assertNotNull(result, "EmailContent should not be null");
        assertEquals("<html>HTML</html>", result.htmlContent(), "HTML content mismatch");
        assertEquals("TEXT", result.textContent(), "TEXT content mismatch");

        //capture html Context arguments to inspect variables
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("recipient_transfer_cancelled"), contextCaptor.capture());
        Context htmlContext = contextCaptor.getValue();

        assertEquals(recipient, htmlContext.getVariable("recipient"));
        assertEquals(date, htmlContext.getVariable("date"));
        assertEquals(amount, htmlContext.getVariable("amount"));
        assertEquals(sender, htmlContext.getVariable("sender"));
        assertEquals(reference, htmlContext.getVariable("reference"));

        //capture text Context arguments to inspect variables
        ArgumentCaptor<Context> textContextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("text_recipient_transfer_cancelled"), textContextCaptor.capture());
        Context textContext = textContextCaptor.getValue();

        assertEquals(recipient, textContext.getVariable("recipient"));
        assertEquals(date, textContext.getVariable("date"));
        assertEquals(amount, textContext.getVariable("amount"));
        assertEquals(sender, textContext.getVariable("sender"));
        assertEquals(reference, textContext.getVariable("reference"));

        //verify that both templates were called exactly once
        verify(templateEngine, times(1)).process(eq("recipient_transfer_cancelled"), any(Context.class));
        verify(templateEngine, times(1)).process(eq("text_recipient_transfer_cancelled"), any(Context.class));

        //verify no extra interactions
        verifyNoMoreInteractions(templateEngine);
    }
    @Test
    void shouldGenerateRecipientTransferExpiredTemplateWithCorrectContentAndContext(){

        //define behaviour
        when(templateEngine.process(eq("recipient_transfer_expired"), any(Context.class)))
                .thenReturn("<html>HTML</html>");
        when(templateEngine.process(eq("text_recipient_transfer_expired"), any(Context.class)))
                .thenReturn("TEXT");

        String recipient = "Ryu";
        String date = "SUN, FEB 22, 2026";
        String amount = "$50 (J)";
        String sender = "Yellow Knife";
        String reference = "TX-123456";

        //act - call method
        EmailContent result = service.recipientTransferExpired(recipient, date, amount, sender, reference);

        //assert DTO values
        assertNotNull(result, "EmailContent should not be null");
        assertEquals("<html>HTML</html>", result.htmlContent(), "HTML content mismatch");
        assertEquals("TEXT", result.textContent(), "TEXT content mismatch");

        //capture html Context arguments to inspect variables
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("recipient_transfer_expired"), contextCaptor.capture());
        Context htmlContext = contextCaptor.getValue();

        assertEquals(recipient, htmlContext.getVariable("recipient"));
        assertEquals(date, htmlContext.getVariable("date"));
        assertEquals(amount, htmlContext.getVariable("amount"));
        assertEquals(sender, htmlContext.getVariable("sender"));
        assertEquals(reference, htmlContext.getVariable("reference"));

        //capture text Context arguments to inspect variables
        ArgumentCaptor<Context> textContextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("text_recipient_transfer_expired"), textContextCaptor.capture());
        Context textContext = textContextCaptor.getValue();

        assertEquals(recipient, textContext.getVariable("recipient"));
        assertEquals(date, textContext.getVariable("date"));
        assertEquals(amount, textContext.getVariable("amount"));
        assertEquals(sender, textContext.getVariable("sender"));
        assertEquals(reference, textContext.getVariable("reference"));

        //verify that both templates were called exactly once
        verify(templateEngine, times(1)).process(eq("recipient_transfer_expired"), any(Context.class));
        verify(templateEngine, times(1)).process(eq("text_recipient_transfer_expired"), any(Context.class));

        //verify no extra interactions
        verifyNoMoreInteractions(templateEngine);
    }
    @Test
    void shouldGenerateSenderTransferDepositedTemplateWithCorrectContentAndContext(){

        //define behaviour
        when(templateEngine.process(eq("sender_transfer_deposited"), any(Context.class)))
                .thenReturn("<html>HTML</html>");
        when(templateEngine.process(eq("text_sender_transfer_deposited"), any(Context.class)))
                .thenReturn("TEXT");

        String sender = "Yellow Knife";
        String date = "SUN, FEB 22, 2026";
        String amount = "$50 (J)";
        String recipient = "Ryu";
        String reference = "TX-123456";

        //act - call method
        EmailContent result = service.senderTransferDeposited(sender, date, amount, recipient, reference);

        //assert DTO values
        assertNotNull(result, "EmailContent should not be null");
        assertEquals("<html>HTML</html>", result.htmlContent(), "HTML content mismatch");
        assertEquals("TEXT", result.textContent(), "TEXT content mismatch");

        //capture html Context arguments to inspect variables
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("sender_transfer_deposited"), contextCaptor.capture());
        Context htmlContext = contextCaptor.getValue();

        assertEquals(sender, htmlContext.getVariable("sender"));
        assertEquals(date, htmlContext.getVariable("date"));
        assertEquals(amount, htmlContext.getVariable("amount"));
        assertEquals(recipient, htmlContext.getVariable("recipient"));
        assertEquals(reference, htmlContext.getVariable("reference"));

        //capture text Context arguments to inspect variables
        ArgumentCaptor<Context> textContextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("text_sender_transfer_deposited"), textContextCaptor.capture());
        Context textContext = textContextCaptor.getValue();

        assertEquals(sender, textContext.getVariable("sender"));
        assertEquals(date, textContext.getVariable("date"));
        assertEquals(amount, textContext.getVariable("amount"));
        assertEquals(recipient, textContext.getVariable("recipient"));
        assertEquals(reference, textContext.getVariable("reference"));

        //verify that both templates were called exactly once
        verify(templateEngine, times(1)).process(eq("sender_transfer_deposited"), any(Context.class));
        verify(templateEngine, times(1)).process(eq("text_sender_transfer_deposited"), any(Context.class));

        //verify no extra interactions
        verifyNoMoreInteractions(templateEngine);
    }
    @Test
    void shouldGenerateSenderTransferCancelledTemplateWithCorrectContentAndContext(){

        //define behaviour
        when(templateEngine.process(eq("sender_transfer_cancelled"), any(Context.class)))
                .thenReturn("<html>HTML</html>");
        when(templateEngine.process(eq("text_sender_transfer_cancelled"), any(Context.class)))
                .thenReturn("TEXT");

        String sender = "Yellow Knife";
        String date = "SUN, FEB 22, 2026";
        String amount = "$50 (J)";
        String recipient = "Ryu";
        String reference = "TX-123456";

        //act - call method
        EmailContent result = service.senderTransferCancelled(sender, date, amount, recipient, reference);

        //assert DTO values
        assertNotNull(result, "EmailContent should not be null");
        assertEquals("<html>HTML</html>", result.htmlContent(), "HTML content mismatch");
        assertEquals("TEXT", result.textContent(), "TEXT content mismatch");

        //capture html Context arguments to inspect variables
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("sender_transfer_cancelled"), contextCaptor.capture());
        Context htmlContext = contextCaptor.getValue();

        assertEquals(sender, htmlContext.getVariable("sender"));
        assertEquals(date, htmlContext.getVariable("date"));
        assertEquals(amount, htmlContext.getVariable("amount"));
        assertEquals(recipient, htmlContext.getVariable("recipient"));
        assertEquals(reference, htmlContext.getVariable("reference"));

        //capture text Context arguments to inspect variables
        ArgumentCaptor<Context> textContextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("text_sender_transfer_cancelled"), textContextCaptor.capture());
        Context textContext = textContextCaptor.getValue();

        assertEquals(sender, textContext.getVariable("sender"));
        assertEquals(date, textContext.getVariable("date"));
        assertEquals(amount, textContext.getVariable("amount"));
        assertEquals(recipient, textContext.getVariable("recipient"));
        assertEquals(reference, textContext.getVariable("reference"));

        //verify that both templates were called exactly once
        verify(templateEngine, times(1)).process(eq("sender_transfer_cancelled"), any(Context.class));
        verify(templateEngine, times(1)).process(eq("text_sender_transfer_cancelled"), any(Context.class));

        //verify no extra interactions
        verifyNoMoreInteractions(templateEngine);
    }
    @Test
    void shouldGenerateSenderTransferExpiredTemplateWithCorrectContentAndContext(){

        //define behaviour
        when(templateEngine.process(eq("sender_transfer_expired"), any(Context.class)))
                .thenReturn("<html>HTML</html>");
        when(templateEngine.process(eq("text_sender_transfer_expired"), any(Context.class)))
                .thenReturn("TEXT");

        String sender = "Yellow Knife";
        String date = "SUN, FEB 22, 2026";
        String amount = "$50 (J)";
        String recipient = "Ryu";
        String reference = "TX-123456";

        //act - call method
        EmailContent result = service.senderTransferExpired(sender, date, amount, recipient, reference);

        //assert DTO values
        assertNotNull(result, "EmailContent should not be null");
        assertEquals("<html>HTML</html>", result.htmlContent(), "HTML content mismatch");
        assertEquals("TEXT", result.textContent(), "TEXT content mismatch");

        //capture html Context arguments to inspect variables
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("sender_transfer_expired"), contextCaptor.capture());
        Context htmlContext = contextCaptor.getValue();

        assertEquals(sender, htmlContext.getVariable("sender"));
        assertEquals(date, htmlContext.getVariable("date"));
        assertEquals(amount, htmlContext.getVariable("amount"));
        assertEquals(recipient, htmlContext.getVariable("recipient"));
        assertEquals(reference, htmlContext.getVariable("reference"));

        //capture text Context arguments to inspect variables
        ArgumentCaptor<Context> textContextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("text_sender_transfer_expired"), textContextCaptor.capture());
        Context textContext = textContextCaptor.getValue();

        assertEquals(sender, textContext.getVariable("sender"));
        assertEquals(date, textContext.getVariable("date"));
        assertEquals(amount, textContext.getVariable("amount"));
        assertEquals(recipient, textContext.getVariable("recipient"));
        assertEquals(reference, textContext.getVariable("reference"));

        //verify that both templates were called exactly once
        verify(templateEngine, times(1)).process(eq("sender_transfer_expired"), any(Context.class));
        verify(templateEngine, times(1)).process(eq("text_sender_transfer_expired"), any(Context.class));

        //verify no extra interactions
        verifyNoMoreInteractions(templateEngine);
    }
}
