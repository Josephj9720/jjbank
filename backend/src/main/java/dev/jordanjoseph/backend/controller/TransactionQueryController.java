package dev.jordanjoseph.backend.controller;

import dev.jordanjoseph.backend.dto.transactionhistory.TransactionView;
import dev.jordanjoseph.backend.model.Transaction;
import dev.jordanjoseph.backend.service.TransactionQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
public class TransactionQueryController {

    @Autowired
    private TransactionQueryService transactionQueryService;

    @Autowired
    private PagedResourcesAssembler<TransactionView> assembler;

    @GetMapping("/accounts/{accountId}/transactions")
    public PagedModel<EntityModel<TransactionView>> getAccountTransactions(
            @PathVariable UUID accountId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        Transaction.Type t = (type == null || type.isBlank()) ? null : Transaction.Type.valueOf(type);

        Page<TransactionView> page = transactionQueryService.listForAccount(accountId, t, from, to, pageable);
        return assembler.toModel(page);
    }

    @GetMapping("/transactions/{transaction_id}")
    public ResponseEntity<TransactionView> getTransaction(@PathVariable UUID transactionId) {

        return null; //implement later
    }
}
