package lv.gstg.javademo.transactions.api;

import lombok.SneakyThrows;
import lv.gstg.javademo.transactions.config.WebSecurityConfig;
import lv.gstg.javademo.transactions.core.AccountsService;
import lv.gstg.javademo.transactions.core.dto.TransferFundsRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransfersController.class)
@Import(WebSecurityConfig.class)
@ActiveProfiles("test")
public class TransfersControllerTest {

    @MockBean
    AccountsService accountsService;

    @Autowired
    MockMvc mvc;

    @Test
    @SneakyThrows
    void transferFunds() {
        Object data = TransferFundsRequest.builder()
                .sourceAccountId(1L)
                .targetAccountId(3L)
                .amount(new BigDecimal("123.12"))
                .currency("USD")
                .build();
        when(accountsService.transferFunds(any())).thenReturn(123L);
        mvc.perform(MockMvcRequestBuilders.post("/transfers") //.with(csrf())
                        .content(new ObjectMapper().writeValueAsString(data))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("transferId").isNotEmpty());
    }

}
