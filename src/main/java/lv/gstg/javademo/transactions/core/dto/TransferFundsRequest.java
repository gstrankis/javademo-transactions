package lv.gstg.javademo.transactions.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferFundsRequest {

    @NotNull
    Long sourceAccountId;

    @NotNull
    Long targetAccountId;

    @NotNull
    BigDecimal amount;

    @NotBlank
    String currency;

}
