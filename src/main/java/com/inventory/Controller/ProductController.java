package com.inventory.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory.DTO.ProductDTO;
import com.inventory.model.Product;
import com.inventory.model.ProductHistory;
import com.inventory.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller", description = "API for managing product records with versioning and soft delete")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Create a new product", description = "Add a new product to the inventory with details like name, description, price, and quantity.")
    @PostMapping
    public Product createProduct(@RequestBody ProductDTO productDTO) {
        return productService.createProduct(productDTO);
    }

    @Operation(summary = "Get all active products", description = "Retrieve a list of all active products that are not marked as deleted.")
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(summary = "Get product by ID", description = "Retrieve the details of a specific product by its unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get product modification history", description = "Retrieve the complete modification history of a product by its ID.")
    @GetMapping("/{id}/history")
    public List<ProductHistory> getProductHistory(@PathVariable Long id) {
        return productService.getProductHistory(id);
    }

    @Operation(summary = "Get all deleted products", description = "Retrieve a list of all products that have been soft-deleted.")
    @GetMapping("/deleted")
    public List<Product> getAllDeletedProducts() {
        return productService.getAllDeletedProducts();
    }

    @Operation(summary = "Update a product", description = "Update an existing product's details.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        try {
            productService.updateProduct(id, productDTO);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Revert product to last modified version", description = "Revert a product to the most recent historical version.")
    @PutMapping("/{id}/revert")
    public ResponseEntity<Void> revertToLastModified(@PathVariable Long id) {
        try {
            productService.revertToLastModified(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Revert product to a specific historical version", description = "Revert a product to a specific version using the history ID.")
    @PutMapping("/{id}/revert/{historyId}")
    public ResponseEntity<Void> revertToSpecificVersion(@PathVariable Long id, @PathVariable Long historyId) {
        try {
            productService.revertToSpecificVersion(id, historyId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Restore a deleted product", description = "Restore a soft-deleted product by setting its deleted flag back to false.")
    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restoreDeletedProduct(@PathVariable Long id) {
        try {
            productService.restoreDeletedProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Soft delete a product", description = "Mark a product as deleted without removing it from the database.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteProduct(@PathVariable Long id) {
        try {
            productService.softDeleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
