package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.model.DebitCard;
import dev.jordanjoseph.backend.model.User;
import dev.jordanjoseph.backend.repository.DebitCardRepository;
import dev.jordanjoseph.backend.util.CardDetailsGenerator;
import dev.jordanjoseph.backend.util.CardDisplay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DebitCardService {

    @Autowired
    private DebitCardRepository debitCards;

    @Value("${jjb.card.expiry-years}")
    private int cardExpiryYears;

    @Transactional
    public void createForUser(User user) {
        CardDetailsGenerator generator = new CardDetailsGenerator();
        CardDisplay display = new CardDisplay();
        DebitCard debitCard = new DebitCard();
        debitCard.setOwner(user);
        debitCard.setNumber(generator.generateCardNumber());
        debitCard.setExpiry(generator.generateCardExpiryFromNow(cardExpiryYears));
        debitCard.setBrand("JJB Card");
        debitCard.setLast4(display.getLast4FromNumber(debitCard.getNumber()));
        debitCards.save(debitCard);
    }

}
