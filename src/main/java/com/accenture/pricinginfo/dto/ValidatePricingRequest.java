package com.accenture.pricinginfo.dto;

import com.accenture.pricinginfo.enums.TermDuration;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ValidatePricingRequest {

    private String productId;

    @NotNull
    @Digits(integer = 15, fraction = 16)
    @DecimalMin(value = "0.1")
    private BigDecimal interestRate;

    @NotBlank
    private String term;

    public ValidatePricingRequest(String productId, BigDecimal interestRate, String term) {
        this.productId = productId;
        this.interestRate = interestRate;
        this.term = term;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
