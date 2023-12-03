package lv.gstg.javademo.transactions.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.gstg.javademo.transactions.core.dto.AccountDetails;
import lv.gstg.javademo.transactions.core.dto.TransactionDetails;
import lv.gstg.javademo.transactions.core.dto.TransferFundsRequest;
import lv.gstg.javademo.transactions.core.exceptions.BadRequestException;
import lv.gstg.javademo.transactions.model.Account;
import lv.gstg.javademo.transactions.model.Transaction;
import lv.gstg.javademo.transactions.model.Transfer;
import lv.gstg.javademo.transactions.model.TxType;
import lv.gstg.javademo.transactions.repositories.AccountsRepository;
import lv.gstg.javademo.transactions.repositories.OffsetBasedPageRequest;
import lv.gstg.javademo.transactions.repositories.TransactionsRepository;
import lv.gstg.javademo.transactions.repositories.TransfersRepository;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountsService {

    final AccountsRepository accountsRepository;
    final TransfersRepository transfersRepository;
    final TransactionsRepository transactionsRepository;
    final CurrencyConverter currencyConverter;
    final AccountMapper accountMapper;
    final TransactionMapper transactionMapper;


    @Transactional
    public List<AccountDetails> findAllAccounts() {
        return accountsRepository.findAll().stream()
                .map(accountMapper::toDetails)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AccountDetails> findAccountsByClientId(Long clientId) {
        return accountsRepository.findAllByClientId(clientId).stream()
                .map(accountMapper::toDetails)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TransactionDetails> findAccountHistory(Long accountId, int offset, int limit) {
        offset = offset >= 0 ? offset : 0;
        limit = limit > 0 ? limit : Integer.MAX_VALUE;
        return transactionsRepository.findAllByAccountIdPageable(accountId, OffsetBasedPageRequest.of(offset, limit))
                .stream()
                .map(transactionMapper::toDetails)
                .collect(Collectors.toList());
    }

    /**
     * Registers new transfer operation, corresponding transaction and updates account balance
     *
     * @param request request for a funds transfer
     * @return id of registered {@link Transfer}
     */
    //retry in case of deadlock while trying to lock accounts affected
    @Retryable(retryFor = CannotAcquireLockException.class)
    @Transactional
    public Long transferFunds(TransferFundsRequest request) {
        var accounts = accountsRepository.findAllByIdAndLockForUpdate(Set.of(request.getSourceAccountId(), request.getTargetAccountId()));
        var sourceAccount = accounts.stream().filter(a -> a.getId().equals(request.getSourceAccountId())).findAny()
                .orElseThrow(() -> badRequest("No such accountId " + request.getSourceAccountId()));
        var targetAccount = accounts.stream().filter(a -> a.getId().equals(request.getTargetAccountId())).findAny()
                .orElseThrow(() -> badRequest("No such accountId " + request.getTargetAccountId()));

        //validate
        if (!StringUtils.equals(targetAccount.getCurrency(), request.getCurrency()))
            throw badRequest("Target account currency " + targetAccount.getCurrency() + " doesn't match transfer currency " + request.getCurrency());
        //TODO: check
        // sourceAccount!=targetAccount
        // request.getCurrency() is supported
        // request.getAmount()>0

        BigDecimal amount = request.getAmount();
        var targetCurrencyRate = currencyConverter.findRate(request.getCurrency(), targetAccount.getCurrency());
        var targetAmount = currencyConverter.convertUsingRate(amount, targetCurrencyRate);
        var targetBalanceAfter = targetAccount.getBalance().add(targetAmount);
        var sourceCurrencyRate = currencyConverter.findRate(request.getCurrency(), sourceAccount.getCurrency());
        var sourceAmount = currencyConverter.convertUsingRate(amount, sourceCurrencyRate);
        var sourceBalanceAfter = sourceAccount.getBalance().subtract(amount);
        if (sourceBalanceAfter.compareTo(BigDecimal.ZERO) < 0)
            throw badRequest("Not enough funds source.balance: " + sourceAccount.getBalance() + " to withdraw " + sourceAmount);

        var transfer = Transfer.builder()
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .targetCurrencyRate(targetCurrencyRate)
                .sourceCurrencyRate(sourceCurrencyRate)
                .build();
        var debitTx = createTransaction(transfer, sourceAccount, TxType.D, sourceAmount, sourceBalanceAfter);
        var creditTx = createTransaction(transfer, targetAccount, TxType.C, targetAmount, targetBalanceAfter);

        transfersRepository.saveAndFlush(transfer);
        transactionsRepository.saveAllAndFlush(Set.of(debitTx, creditTx));
        accountsRepository.saveAllAndFlush(Set.of(sourceAccount, targetAccount));

        return transfer.getId();
    }

    private Transaction createTransaction(Transfer tr, Account account, TxType txType, BigDecimal amount, BigDecimal balanceAfter) {
        var debitTx = Transaction.builder()
                .createdAt(tr.getCreatedAt())
                .account(account)
                .txType(txType)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .transfer(tr)
                .build();
        account.setBalance(balanceAfter);
        return debitTx;
    }

    private BadRequestException badRequest(String message) {
        return new BadRequestException(message);
    }
}

@Mapper
interface AccountMapper {
    AccountDetails toDetails(Account account);
}

@Mapper(imports = {ZoneOffset.class})
interface TransactionMapper {
    @Mapping(target = "createdAt", expression = "java(transaction.getCreatedAt().atZone(ZoneOffset.UTC))")
    TransactionDetails toDetails(Transaction transaction);
}
