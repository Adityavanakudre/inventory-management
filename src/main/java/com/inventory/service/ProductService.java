package com.inventory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.DTO.ProductDTO;
import com.inventory.Repositry.ProductHistoryRepository;
import com.inventory.Repositry.ProductRepository;
import com.inventory.model.Product;
import com.inventory.model.ProductHistory;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductHistoryRepository productHistoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAllActiveProducts();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setDeleted(false); // Ensure deleted flag is false by default
        return productRepository.save(product);
    }

    public void updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Save the current state to ProductHistory before updating
        ProductHistory history = new ProductHistory();
        history.setProductId(existingProduct.getId());
        history.setName(existingProduct.getName());
        history.setDescription(existingProduct.getDescription());
        history.setPrice(existingProduct.getPrice());
        history.setQuantity(existingProduct.getQuantity());
        history.setModifiedAt(LocalDateTime.now());
        productHistoryRepository.save(history);

        // Update the product with new data
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setQuantity(productDTO.getQuantity());
        productRepository.save(existingProduct);
    }

    public void softDeleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setDeleted(true);
        productRepository.save(product);
    }

    public List<ProductHistory> getProductHistory(Long productId) {
        return productHistoryRepository.findAllByProductIdOrderByModifiedAtDesc(productId);
    }

    public void revertToLastModified(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Save the current state to ProductHistory before reverting
        ProductHistory currentState = new ProductHistory();
        currentState.setProductId(product.getId());
        currentState.setName(product.getName());
        currentState.setDescription(product.getDescription());
        currentState.setPrice(product.getPrice());
        currentState.setQuantity(product.getQuantity());
        currentState.setModifiedAt(LocalDateTime.now());
        productHistoryRepository.save(currentState);

        // Retrieve the most recent history entry
        ProductHistory lastModified = productHistoryRepository.findAllByProductIdOrderByModifiedAtDesc(productId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No history found for product"));

        // Update the product with data from the latest historical entry
        product.setName(lastModified.getName());
        product.setDescription(lastModified.getDescription());
        product.setPrice(lastModified.getPrice());
        product.setQuantity(lastModified.getQuantity());
        productRepository.save(product);
    }

    public void revertToSpecificVersion(Long productId, Long historyId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Save the current state to ProductHistory before reverting
        ProductHistory currentState = new ProductHistory();
        currentState.setProductId(product.getId());
        currentState.setName(product.getName());
        currentState.setDescription(product.getDescription());
        currentState.setPrice(product.getPrice());
        currentState.setQuantity(product.getQuantity());
        currentState.setModifiedAt(LocalDateTime.now());
        productHistoryRepository.save(currentState);

        // Retrieve the specific historical version by historyId
        ProductHistory specificVersion = productHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History record not found"));

        if (!specificVersion.getProductId().equals(productId)) {
            throw new RuntimeException("History record does not belong to the specified product");
        }

        // Update the product with data from the specific historical entry
        product.setName(specificVersion.getName());
        product.setDescription(specificVersion.getDescription());
        product.setPrice(specificVersion.getPrice());
        product.setQuantity(specificVersion.getQuantity());
        productRepository.save(product);
    }

    public List<Product> getAllDeletedProducts() {
        return productRepository.findAllDeletedProducts();
    }

    public void restoreDeletedProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getDeleted()) {
            throw new RuntimeException("Product is not deleted");
        }
        product.setDeleted(false);
        productRepository.save(product);
    }
}
