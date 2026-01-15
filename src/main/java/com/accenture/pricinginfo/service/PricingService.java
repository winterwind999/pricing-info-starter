package com.accenture.pricinginfo.service;

import com.accenture.pricinginfo.dto.PricingForProductResponse;
import com.accenture.pricinginfo.dto.PricingInitializationResponse;
import com.accenture.pricinginfo.dto.ValidatePricingResponse;
import com.accenture.pricinginfo.enums.TermDuration;

import java.io.IOException;
import java.math.BigDecimal;

public interface PricingService {

    PricingInitializationResponse initializeDatabase();

    PricingForProductResponse getPricing(String productCode);

    ValidatePricingResponse validatePricing(String productId, BigDecimal interestRate, String term);
}
