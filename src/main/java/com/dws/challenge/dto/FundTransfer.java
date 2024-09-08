package com.dws.challenge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundTransfer {
    @NotNull
    @NotEmpty
    private String debitAccountId;
    @NotNull
    @NotEmpty
    private String creditAccountId;
    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    private BigDecimal fundToBeTransferred;
}
