package com.accenture.pricinginfo.service.impl;

import com.accenture.pricinginfo.dto.PricingForProductResponse;
import com.accenture.pricinginfo.dto.PricingInitializationResponse;
import com.accenture.pricinginfo.dto.ValidatePricingResponse;
import com.accenture.pricinginfo.enums.TermDuration;
import com.accenture.pricinginfo.exception.BadRequestException;
import com.accenture.pricinginfo.model.entity.Pricing;
import com.accenture.pricinginfo.repository.productMappingApi.ProductRepository;
import com.accenture.pricinginfo.repository.PricingRepository;
import com.accenture.pricinginfo.repository.productMappingApi.dto.ProductIdResponse;
import com.accenture.pricinginfo.service.PricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PricingServiceImpl implements PricingService {

    private final PricingRepository pricingRepository;
    private final ProductRepository productRepository;

    public PricingServiceImpl(
            PricingRepository pricingRepository,
            ProductRepository productRepository
    ) {
        this.pricingRepository = pricingRepository;
        this.productRepository = productRepository;
    }

    @Override
    public PricingInitializationResponse initializeDatabase() {
        Pricing pricing1 = buildPricing("1234567", 1, "YEAR");
        Pricing pricing2 = buildPricing("12345678", 1, "YEAR");
        Pricing pricing3 = buildPricing("123457", 1, "YEAR");
        Pricing pricing4 = buildPricing("1234573", 6, "MONTHS");
        Pricing pricing5 = buildPricing("1234572", 9, "MONTHS");
        Pricing pricing6 = buildPricing("12345671", 2, "YEARS");

        List<Pricing> pricings = pricingRepository.saveAll(
                List.of(
                        pricing1,
                        pricing2,
                        pricing3,
                        pricing4,
                        pricing5,
                        pricing6
                )
        );

        return new PricingInitializationResponse(
                pricings
        );
    }

    @Override
    public PricingForProductResponse getPricing(String productCode) {
        ProductIdResponse productId = productRepository.getProductId(productCode);

        if (productId == null || productId.getProductId() == null) {
            throw new BadRequestException(400, "Product code " + productCode + " not found.", Map.of());
        }

        Pricing pricing = pricingRepository.findByProductId(productId.getProductId())
                 .orElseThrow(() -> new BadRequestException(400, "Pricing for product code " + productCode + " not found.", Map.of()));

        String termString = pricing.getMinAllowedTerm() + "_" + pricing.getMinAllowedTermType();

        TermDuration termDuration = TermDuration.fromValue(termString);

        return new PricingForProductResponse(pricing.getInterestRate(), pricing.getMinDepositAmount(), pricing.getMaxDepositAmount(), termDuration);
    }

    @Override
    public ValidatePricingResponse validatePricing(String productId, BigDecimal interestRate, String term) {
        Pricing pricing = pricingRepository.findByProductId(productId)
                .orElseThrow(() -> new BadRequestException(400, "Product id " + productId + " not found.", Map.of()));

        if (pricing.getInterestRate().compareTo(interestRate) != 0) {
            return new ValidatePricingResponse(false, "The interest rate is incorrect.");
        }

        try {
            TermDuration.fromValue(term);
        } catch (IllegalArgumentException e) {
            return new ValidatePricingResponse(false, "The term is incorrect.");
        }

        String termString = pricing.getMinAllowedTerm() + "_" + pricing.getMinAllowedTermType();

        if (!termString.equals(term)) {
            return new ValidatePricingResponse(false, "The term is incorrect.");
        }

        return new ValidatePricingResponse(true, "");
    }

    private Pricing buildPricing(
            String productId,
            int minAllowedTerm,
            String minAllowedTermType
    ) {
        return Pricing
                .builder(productId, minAllowedTerm, minAllowedTermType)
                .build();
    }
}
