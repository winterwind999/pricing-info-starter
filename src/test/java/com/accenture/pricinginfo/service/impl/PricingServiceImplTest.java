package com.accenture.pricinginfo.service.impl;

import com.accenture.pricinginfo.dto.PricingForProductResponse;
import com.accenture.pricinginfo.dto.ValidatePricingResponse;
import com.accenture.pricinginfo.enums.TermDuration;
import com.accenture.pricinginfo.exception.BadRequestException;
import com.accenture.pricinginfo.model.entity.Pricing;
import com.accenture.pricinginfo.repository.PricingRepository;
import com.accenture.pricinginfo.repository.productMappingApi.ProductRepository;
import com.accenture.pricinginfo.repository.productMappingApi.dto.ProductIdResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PricingServiceImplTest {

    @Mock
    private PricingRepository pricingRepository;
    @Mock
    private ProductRepository productRepository;

    private PricingServiceImpl pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingServiceImpl(pricingRepository, productRepository);
    }

    @Nested
    @DisplayName("Unit Tests for getPricing")
    class GetPricingTest {

        @DisplayName("Retrieving the pricing for a valid product code and a valid product ID.")
        @Test
        void testGetPricing_ValidProductCodeAndValidProductId() {
            String productCode = "123456";
            String productId = "1234567";

            ProductIdResponse mockProductIdResponse = new ProductIdResponse(productId);
            Pricing mockPricing = Pricing.builder(productId, 1, "YEAR")
                    .interestRate(new BigDecimal("0.20"))
                    .minDepositAmount(new BigDecimal("1000.00"))
                    .maxDepositAmount(new BigDecimal("100000.00"))
                    .build();

            Mockito.when(productRepository.getProductId(productCode)).thenReturn(mockProductIdResponse);
            Mockito.when(pricingRepository.findByProductId(productId)).thenReturn(Optional.of(mockPricing));

            PricingForProductResponse response = pricingService.getPricing(productCode);

            Assertions.assertEquals(new BigDecimal("0.20"), response.getInterestRate());
            Assertions.assertEquals(new BigDecimal("1000.00"), response.getMinDepositAmount());
            Assertions.assertEquals(new BigDecimal("100000.00"), response.getMaxDepositAmount());
            Assertions.assertEquals(TermDuration.ONE_YEAR, response.getMinAllowedTerm());
        }

        @DisplayName("Retrieving the pricing for a valid product code but an invalid product ID should result in an error response.")
        @Test
        void testGetPricing_ValidProductCodeButInvalidProductId() {
            String productCode = "12345";
            String productId = "null";

            ProductIdResponse mockProductIdResponse = new ProductIdResponse(productId);

            Mockito.when(productRepository.getProductId(productCode)).thenReturn(mockProductIdResponse);
            Mockito.when(pricingRepository.findByProductId(productId)).thenReturn(Optional.empty());

            BadRequestException badRequestException = Assertions.assertThrows(
                    BadRequestException.class,
                    () -> pricingService.getPricing(productCode));

            Assertions.assertEquals(400, badRequestException.getErrorId());
            Assertions.assertEquals("Pricing for product code " + productCode + " not found.", badRequestException.getErrorMessage());
            Assertions.assertEquals(Map.of(), badRequestException.getErrorDetails());
        }

        @DisplayName("Attempting to retrieve pricing for an invalid product code should return an error response.")
        @Test
        void testGetPricing_InvalidProductCode() {
            String productCode = "12345";

            ProductIdResponse mockProductIdResponse = new ProductIdResponse(null);

            Mockito.when(productRepository.getProductId(productCode)).thenReturn(mockProductIdResponse);

            BadRequestException badRequestException = Assertions.assertThrows(
                    BadRequestException.class,
                    () -> pricingService.getPricing(productCode));

            Assertions.assertEquals(400, badRequestException.getErrorId());
            Assertions.assertEquals("Product code " + productCode + " not found.", badRequestException.getErrorMessage());
            Assertions.assertEquals(Map.of(), badRequestException.getErrorDetails());
        }
    }

    @Nested
    @DisplayName("Unit Tests for validatePricing")
    class ValidatePricingTest {

        @DisplayName("Validate a pricing with correct details should return a ValidatePricingResponse.")
        @Test
        void testValidatePricing_CorrectDetails() {
            String productId = "1234567";
            BigDecimal interestRate = new BigDecimal("0.2");
            int minAllowedTerm = 1;
            String minAllowedTermType = "YEAR";
            String term = minAllowedTerm + "_" + minAllowedTermType;

            Pricing mockPricing = Pricing.builder(productId, minAllowedTerm, minAllowedTermType)
                    .interestRate(interestRate)
                    .minDepositAmount(new BigDecimal("1000.00"))
                    .maxDepositAmount(new BigDecimal("100000.00"))
                    .build();

            Mockito.when(pricingRepository.findByProductId(productId)).thenReturn(Optional.of(mockPricing));

            ValidatePricingResponse response = pricingService.validatePricing(productId, interestRate, term);

            Assertions.assertEquals("", response.getNotValidReason());
            Assertions.assertTrue(response.isValid());
        }

        @DisplayName("Validate a pricing with an invalid product id should return a BadRequestException.")
        @Test
        void testValidatePricing_InvalidProductId() {
            String productId = "123456";
            BigDecimal interestRate = new BigDecimal("0.2");
            int minAllowedTerm = 1;
            String minAllowedTermType = "YEAR";
            String term = minAllowedTerm + "_" + minAllowedTermType;

            Mockito.when(pricingRepository.findByProductId(productId)).thenReturn(Optional.empty());

            BadRequestException badRequestException = Assertions.assertThrows(
                    BadRequestException.class,
                    () -> pricingService.validatePricing(productId, interestRate, term));

            Assertions.assertEquals(400, badRequestException.getErrorId());
            Assertions.assertEquals("Product id " + productId + " not found.", badRequestException.getErrorMessage());
            Assertions.assertEquals(Map.of(), badRequestException.getErrorDetails());
        }

        @DisplayName("Validate a pricing with a valid product id, but with an invalid interest rate.")
        @Test
        void testValidatePricing_ValidProductIdButInvalidInterestRate() {
            String productId = "1234567";
            BigDecimal testInterestRate = new BigDecimal("0.3");
            BigDecimal dbInterestRate = new BigDecimal("0.2");
            int minAllowedTerm = 1;
            String minAllowedTermType = "YEAR";
            String term = minAllowedTerm + "_" + minAllowedTermType;

            Pricing mockPricing = Pricing.builder(productId, minAllowedTerm, minAllowedTermType)
                    .interestRate(dbInterestRate)
                    .minDepositAmount(new BigDecimal("1000.00"))
                    .maxDepositAmount(new BigDecimal("100000.00"))
                    .build();

            Mockito.when(pricingRepository.findByProductId(productId)).thenReturn(Optional.of(mockPricing));

            ValidatePricingResponse response = pricingService.validatePricing(productId, testInterestRate, term);

            Assertions.assertEquals("The interest rate is incorrect.", response.getNotValidReason());
            Assertions.assertFalse(response.isValid());
        }

        @DisplayName("Validate a pricing with a valid product id, but with an invalid term.")
        @Test
        void testValidatePricing_ValidProductIdButInvalidTerm() {
            String productId = "1234567";
            BigDecimal interestRate = new BigDecimal("0.2");
            int minAllowedTerm = 1;
            String testMinAllowedTermType = "MONTH";
            String dbMinAllowedTermType = "YEAR";
            String term = minAllowedTerm + "_" + testMinAllowedTermType;

            Pricing mockPricing = Pricing.builder(productId, minAllowedTerm, dbMinAllowedTermType)
                    .interestRate(interestRate)
                    .minDepositAmount(new BigDecimal("1000.00"))
                    .maxDepositAmount(new BigDecimal("100000.00"))
                    .build();

            Mockito.when(pricingRepository.findByProductId(productId)).thenReturn(Optional.of(mockPricing));

            ValidatePricingResponse response = pricingService.validatePricing(productId, interestRate, term);

            Assertions.assertEquals("The term is incorrect.", response.getNotValidReason());
            Assertions.assertFalse(response.isValid());
        }
    }
}
