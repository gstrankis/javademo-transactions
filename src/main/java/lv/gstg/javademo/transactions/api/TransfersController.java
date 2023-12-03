package lv.gstg.javademo.transactions.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lv.gstg.javademo.transactions.core.AccountsService;
import lv.gstg.javademo.transactions.core.dto.TransferFundsRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
@Validated
public class TransfersController {
    final AccountsService accountsService;

    /**
     * Transfer funds between two accounts indicated by identifiers
     *
     * @param request
     * @return
     */
    @PostMapping
    @Operation(summary = "Transfer funds between two accounts")
    public Map<String, Object> transferFunds(@RequestBody @Valid TransferFundsRequest request) {
        var transferId = accountsService.transferFunds(request);
        return Map.of("transferId", transferId);
    }

}
