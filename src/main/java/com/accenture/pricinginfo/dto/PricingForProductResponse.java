package com.accenture.pricinginfo.dto;

import com.accenture.pricinginfo.enums.TermDuration;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public class PricingForProductResponse {

    // pattern: ^(?!0(\.0*)?$)([0]\[1-9]\d{0,15})\.\d{1,16}$
    @Digits(integer = 15, fraction = 16)
    @DecimalMin(value = "0.0", inclusive = false)
    BigDecimal interestRate;

    // pattern: ^(?!0?0\.00$)([0]\[1-9]\d{0,15})\.\d{2}$
    @Digits(integer = 15, fraction = 2)
    @DecimalMin(value = "0.01")
    BigDecimal minDepositAmount;

    // pattern: ^(?!0?0\.00$)([0]\[1-9]\d{0,15})\.\d{2}$
    @Digits(integer = 15, fraction = 2)
    @DecimalMin(value = "0.01")
    BigDecimal maxDepositAmount;

    // enum: "3_MONTHS", "6_MONTHS", "9_MONTHS", "1_YEAR", "2_YEARS", "3_YEARS"
    TermDuration minAllowedTerm;

    public PricingForProductResponse(BigDecimal interestRate, BigDecimal minDepositAmount, BigDecimal maxDepositAmount, TermDuration minAllowedTerm) {
        this.interestRate = interestRate;
        this.minDepositAmount = minDepositAmount;
        this.maxDepositAmount = maxDepositAmount;
        this.minAllowedTerm = minAllowedTerm;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getMinDepositAmount() {
        return minDepositAmount;
    }

    public void setMinDepositAmount(BigDecimal minDepositAmount) {
        this.minDepositAmount = minDepositAmount;
    }

    public BigDecimal getMaxDepositAmount() {
        return maxDepositAmount;
    }

    public void setMaxDepositAmount(BigDecimal maxDepositAmount) {
        this.maxDepositAmount = maxDepositAmount;
    }

    public TermDuration getMinAllowedTerm() {
        return minAllowedTerm;
    }

    public void setMinAllowedTerm(TermDuration minAllowedTerm) {
        this.minAllowedTerm = minAllowedTerm;
    }
}
