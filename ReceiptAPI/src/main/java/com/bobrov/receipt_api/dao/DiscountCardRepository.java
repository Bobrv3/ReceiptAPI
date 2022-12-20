package com.bobrov.receipt_api.dao;

import com.bobrov.receipt_api.model.DiscountCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountCardRepository extends JpaRepository<DiscountCard, Long> {
}
