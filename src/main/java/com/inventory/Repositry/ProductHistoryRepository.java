package com.inventory.Repositry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.model.ProductHistory;

public interface ProductHistoryRepository extends JpaRepository<ProductHistory,Long> {
	
	// Retrieve all history records for a specific product by productId
    List<ProductHistory> findAllByProductIdOrderByModifiedAtDesc(Long productId);

}
