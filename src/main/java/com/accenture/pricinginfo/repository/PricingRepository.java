package com.accenture.pricinginfo.repository;

import com.accenture.pricinginfo.dto.PricingForProductResponse;
import com.accenture.pricinginfo.model.entity.Pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, String> {

    Optional<Pricing> findByProductId(String productId);
}
