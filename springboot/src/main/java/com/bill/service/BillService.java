package com.bill.service;

import com.bill.entity.Account;
import com.bill.entity.BillCategory;
import com.bill.entity.BillDetail;
import com.bill.entity.BillRecord;
import com.bill.entity.CategoryStatistic;
import com.bill.mapper.AccountMapper;
import com.bill.mapper.BillCategoryMapper;
import com.bill.mapper.BillRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillCategoryMapper billCategoryMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private BillRecordMapper billRecordMapper;

    public List<BillCategory> listCategories() {
        return billCategoryMapper.findAllActive();
    }

    public BillCategory getCategoryById(String cateId) {
        return billCategoryMapper.selectById(cateId);
    }

    public void createCategory(BillCategory category) {
        if (category == null || category.getCate_id() == null || category.getCate_id().isBlank()) {
            throw new RuntimeException("分类编码不能为空");
        }
        if (category.getClass_name() == null || category.getClass_name().isBlank()) {
            throw new RuntimeException("分类名称不能为空");
        }
        if (category.getType() == null) {
            throw new RuntimeException("分类类型不能为空");
        }
        category.setIs_deleted(0);
        category.setLevel(category.getLevel() == null ? 2 : category.getLevel());
        billCategoryMapper.insert(category);
    }

    public void updateCategory(BillCategory category) {
        if (category == null || category.getCate_id() == null || category.getCate_id().isBlank()) {
            throw new RuntimeException("分类编码不能为空");
        }
        billCategoryMapper.update(category);
    }

    public void deleteCategory(String cateId) {
        if (cateId == null || cateId.isBlank()) {
            throw new RuntimeException("分类编码不能为空");
        }
        billCategoryMapper.softDelete(cateId);
    }

    public List<Account> listAccounts(Integer userId) {
        if (userId == null) {
            userId = 1;
        }
        return accountMapper.findByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createBillRecord(BillRecord record) {
        if (record == null) {
            throw new RuntimeException("账单信息不能为空");
        }
        if (record.getOut_account_id() == null || record.getIn_account_id() == null) {
            throw new RuntimeException("请选择收支账户");
        }
        if (record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("账单金额必须大于0");
        }

        Account outAccount = accountMapper.selectById(record.getOut_account_id());
        Account inAccount = accountMapper.selectById(record.getIn_account_id());
        if (outAccount == null || inAccount == null) {
            throw new RuntimeException("账单账户不存在");
        }
        if (record.getOut_account_id().equals(record.getIn_account_id())) {
            throw new RuntimeException("转入与转出账户不能相同");
        }

        BigDecimal amount = record.getAmount();
        BigDecimal outBalance = outAccount.getBalance() == null ? BigDecimal.ZERO : outAccount.getBalance().subtract(amount);
        BigDecimal inBalance = inAccount.getBalance() == null ? BigDecimal.ZERO : inAccount.getBalance().add(amount);

        accountMapper.updateBalance(outAccount.getId(), outBalance);
        accountMapper.updateBalance(inAccount.getId(), inBalance);

        if (record.getUser_id() == null) {
            record.setUser_id(1);
        }
        if (record.getCurrency() == null || record.getCurrency().isBlank()) {
            record.setCurrency("CNY");
        }
        billRecordMapper.insert(record);
    }

    public List<BillDetail> listBillDetails(Integer userId, Integer payType, Integer accountId, String cateId, String keyword, String startTime, String endTime) {
        if (userId == null) {
            userId = 1;
        }
        return billRecordMapper.queryBillDetails(userId, payType, accountId, cateId, keyword, startTime, endTime);
    }

    public List<CategoryStatistic> statisticsByCategory(Integer userId, Integer payType, String startTime, String endTime) {
        if (userId == null) {
            userId = 1;
        }
        return billRecordMapper.categoryStatistics(userId, payType, startTime, endTime);
    }
}
