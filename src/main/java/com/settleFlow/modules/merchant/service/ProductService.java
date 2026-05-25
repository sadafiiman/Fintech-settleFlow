package com.settleFlow.modules.merchant.service;

import com.settleFlow.modules.merchant.dto.request.CreateProductRequest;
import com.settleFlow.modules.merchant.dto.request.UpdateProductRequest;
import com.settleFlow.modules.merchant.dto.response.ProductResponse;
import com.settleFlow.modules.merchant.enums.MerchantStatus;
import com.settleFlow.modules.merchant.enums.ProductStatus;
import com.settleFlow.modules.merchant.model.Merchant;
import com.settleFlow.modules.merchant.model.Product;
import com.settleFlow.modules.merchant.repository.ProductRepository;
import com.settleFlow.modules.shared.exception.BusinessException;
import com.settleFlow.modules.shared.exception.NotFoundException;
import com.settleFlow.modules.shared.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MerchantService merchantService;

    public PageResponse<ProductResponse> listMarketplace(int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        var result = category != null
                ? productRepository.findAllByStatusAndCategory(
                ProductStatus.ACTIVE, category, pageable)
                : productRepository.findAllByStatus(ProductStatus.ACTIVE, pageable);

        return PageResponse.from(result.map(ProductResponse::from));
    }

    public ProductResponse getById(Long id) {
        Product product = productRepository
                .findByIdAndStatus(id, ProductStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        return ProductResponse.from(product);
    }


    public List<ProductResponse> getMyProducts(Long userId) {
        Merchant merchant = merchantService.findByUserIdOrThrow(userId);
        return productRepository
                .findAllByMerchantIdAndStatusNot(merchant.getId(), ProductStatus.DELETED)
                .stream().map(ProductResponse::from).toList();
    }

    @Transactional
    public ProductResponse create(Long userId, CreateProductRequest request) {
        Merchant merchant = merchantService.findByUserIdOrThrow(userId);

        if (merchant.getStatus() != MerchantStatus.ACTIVE) {
            throw BusinessException.badRequest(
                    "Your store must be approved before listing products");
        }

        Product product = Product.builder()
                .merchant(merchant)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .status(request.getStock() > 0
                        ? ProductStatus.ACTIVE
                        : ProductStatus.OUT_OF_STOCK)
                .build();

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long userId, Long productId, UpdateProductRequest request) {
        Product product = findOwnedProductOrThrow(userId, productId);

        if (request.getName() != null)        product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null)       product.setPrice(request.getPrice());
        if (request.getImageUrl() != null)    product.setImageUrl(request.getImageUrl());
        if (request.getCategory() != null)    product.setCategory(request.getCategory());

        if (request.getStock() != null) {
            product.setStock(request.getStock());
            if (request.getStatus() == null) {
                product.setStatus(request.getStock() > 0
                        ? ProductStatus.ACTIVE
                        : ProductStatus.OUT_OF_STOCK);
            }
        }

        if (request.getStatus() != null) product.setStatus(request.getStatus());

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void delete(Long userId, Long productId) {
        Product product = findOwnedProductOrThrow(userId, productId);
        product.setStatus(ProductStatus.DELETED);  // soft delete
        productRepository.save(product);
    }

    @Transactional
    public void reserveStock(Long productId, int quantity) {
        int updated = productRepository.decrementStock(productId, quantity);
        if (updated == 0) {
            throw BusinessException.badRequest(
                    "Insufficient stock for product: " + productId);
        }
    }

    @Transactional
    public void releaseStock(Long productId, int quantity) {
        productRepository.incrementStock(productId, quantity);
    }

    public Product findActiveProductOrThrow(Long productId) {
        return productRepository.findByIdAndStatus(productId, ProductStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(
                        "Product not found or unavailable: " + productId));
    }


    private Product findOwnedProductOrThrow(Long userId, Long productId) {
        Merchant merchant = merchantService.findByUserIdOrThrow(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));

        if (!product.getMerchant().getId().equals(merchant.getId())) {
            throw BusinessException.badRequest("You do not own this product");
        }

        if (product.getStatus() == ProductStatus.DELETED) {
            throw new NotFoundException("Product not found: " + productId);
        }

        return product;
    }
}