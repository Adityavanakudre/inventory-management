package com.inventory.Repositry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.inventory.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	 // Query to find all non-deleted products
    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    List<Product> findAllActiveProducts();

    // Query to find all soft-deleted products
    @Query("SELECT p FROM Product p WHERE p.deleted = true")
    List<Product> findAllDeletedProducts();
}
