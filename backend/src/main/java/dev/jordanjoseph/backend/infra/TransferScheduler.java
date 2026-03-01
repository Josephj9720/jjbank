package dev.jordanjoseph.backend.infra;

import dev.jordanjoseph.backend.service.ExternalTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TransferScheduler {

    private final ExternalTransferService externalTransferService;

    @Autowired
    public TransferScheduler(ExternalTransferService externalTransferService) {
        this.externalTransferService = externalTransferService;
    }

    @Scheduled(fixedRate = 60000)
    public void processExpiredTransfers() {
        externalTransferService.expirePendingTransfers();
    }

    @Scheduled(fixedRate = 60000)
    public void sendTransferReminder() {
        //create method in transaction service for it
        //use transaction repo too
    }
}
