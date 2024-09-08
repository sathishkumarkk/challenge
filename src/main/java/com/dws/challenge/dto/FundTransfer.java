package com.dws.challenge.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundTransfer {

    private String debitAccountId;
    private String creditAccountId;
    private BigDecimal fundToBeTransferred;
}
