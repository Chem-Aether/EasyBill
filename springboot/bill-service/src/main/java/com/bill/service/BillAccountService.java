package com.bill.service;

import com.bill.entity.Account;
import com.bill.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BillAccountService {
    @Autowired
    private AccountMapper accountMapper;

    public List<Account> listAccounts(Integer userId) {
        if (userId == null) {
            userId = 1;
        }
        return accountMapper.findByUserId(userId);
    }
}
