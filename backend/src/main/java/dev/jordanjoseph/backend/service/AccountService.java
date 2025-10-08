package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.account.AccountView;

import dev.jordanjoseph.backend.model.Account;
import dev.jordanjoseph.backend.model.User;
import dev.jordanjoseph.backend.repository.AccountRepository;
import dev.jordanjoseph.backend.util.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountValidator accountValidator;

    @Transactional
    public AccountView myAccount() {
        Account account = accountValidator.getCurrentUserAccount();
        return new AccountView(account.getId(), account.getBalance());
    }

    @Transactional
    public void createForUser(User user, String type) {
        //if type is null or blank, default to checking, set to uppercase to prevent case-sensitivity issues
        Account.Type t = (type == null || type.isBlank() ? Account.Type.CHECKING : Account.Type.valueOf(type.toUpperCase()));
        Account account = new Account();
        account.setUser(user);
        account.setType(t);
        accountRepository.save(account);
    }
}
