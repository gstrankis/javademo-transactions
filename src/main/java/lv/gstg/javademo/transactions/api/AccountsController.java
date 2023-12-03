package lv.gstg.javademo.transactions.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.gstg.javademo.transactions.core.AccountsService;
import lv.gstg.javademo.transactions.core.dto.AccountDetails;
import lv.gstg.javademo.transactions.core.dto.TransactionDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountsController {
    final AccountsService accountsService;

    /**
     * List of accounts
     *
     * @param clientId - limits list of returned accounts by given clientId, if provided
     * @return
     */
    @GetMapping("")
    @Operation(summary = "List of all accounts")
    public List<AccountDetails> getAccounts(@RequestParam(required = false, name = "clientId") Long clientId) {
        if (clientId == null)
            return accountsService.findAllAccounts();
        else
            return accountsService.findAccountsByClientId(clientId);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Account details")
    public AccountDetails getAccountsById(@PathVariable("accountId") Long accountId) {
        return accountsService.findAccountById(accountId);
    }

    /**
     * Given an account identifier, return transaction history (last transactions come first)
     * and support result paging using “offset” and “limit” parameters
     *
     * @param accountId
     * @param offset    row offset, 0 for no offset
     * @param limit     row limit per request, 0 for unlimited
     * @return
     */
    @GetMapping("/{accountId}/history")
    @Operation(summary = "Account history")
    public List<TransactionDetails> getAccountsHistory(@PathVariable("accountId") Long accountId,
                                                       @RequestParam(defaultValue = "0", name = "offset") int offset,
                                                       @RequestParam(defaultValue = "0", name = "limit") int limit) {
        log.info("getAccountsHistory {} {} {}", accountId, offset, limit);
        return accountsService.findAccountHistory(accountId, offset, limit);
    }

}
