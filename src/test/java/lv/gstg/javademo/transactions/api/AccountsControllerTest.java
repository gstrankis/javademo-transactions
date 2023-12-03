package lv.gstg.javademo.transactions.api;

import lombok.SneakyThrows;
import lv.gstg.javademo.transactions.config.WebSecurityConfig;
import lv.gstg.javademo.transactions.core.AccountsService;
import lv.gstg.javademo.transactions.core.dto.AccountDetails;
import lv.gstg.javademo.transactions.core.dto.TransactionDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountsController.class)
@Import(WebSecurityConfig.class)
@ActiveProfiles("test")
public class AccountsControllerTest {

    @MockBean
    AccountsService accountsService;

    @Autowired
    MockMvc mvc;

    @Test
    @SneakyThrows
    void getAccounts() {
        when(accountsService.findAllAccounts())
                .thenReturn(List.of(new AccountDetails()));
        mvc.perform(MockMvcRequestBuilders.get("/accounts").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void getAccountsByClientId() {
        when(accountsService.findAccountsByClientId(123L))
                .thenReturn(List.of(new AccountDetails()));
        mvc.perform(MockMvcRequestBuilders.get("/accounts?clientId=123").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void getAccountHistory() {
        var td = new TransactionDetails();
        td.setId(1L);
        td.setAccountId(1234L);
        when(accountsService.findAccountHistory(1234L, 1, 2))
                .thenReturn(List.of(td));
        mvc.perform(MockMvcRequestBuilders.get("/accounts/1234/history?offset=1&limit=2").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]").isNotEmpty())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].accountId").value(1234));
    }

}
