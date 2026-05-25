package com.settleFlow.modules.merchant.service;

import com.settleFlow.modules.merchant.dto.request.RegisterMerchantRequest;
import com.settleFlow.modules.merchant.dto.request.UpdateMerchantRequest;
import com.settleFlow.modules.merchant.dto.response.MerchantResponse;
import com.settleFlow.modules.merchant.enums.MerchantStatus;
import com.settleFlow.modules.merchant.model.Merchant;
import com.settleFlow.modules.merchant.repository.MerchantRepository;
import com.settleFlow.modules.shared.exception.BusinessException;
import com.settleFlow.modules.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;

    @Transactional
    public MerchantResponse register(Long userId, RegisterMerchantRequest request) {
        if (merchantRepository.existsByUserId(userId)) {
            throw BusinessException.conflict("Merchant profile already exists for this user");
        }
        if (merchantRepository.existsByStoreName(request.getStoreName())) {
            throw BusinessException.conflict("Store name already taken");
        }

        Merchant merchant = Merchant.builder()
                .userId(userId)
                .storeName(request.getStoreName())
                .description(request.getDescription())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .logoUrl(request.getLogoUrl())
                .status(MerchantStatus.PENDING)
                .build();

        return MerchantResponse.from(merchantRepository.save(merchant));
    }

    @Transactional
    public MerchantResponse update(Long userId, UpdateMerchantRequest request) {
        Merchant merchant = findByUserIdOrThrow(userId);

        if (request.getStoreName() != null) {
            if (!request.getStoreName().equals(merchant.getStoreName())
                    && merchantRepository.existsByStoreName(request.getStoreName())) {
                throw BusinessException.conflict("Store name already taken");
            }
            merchant.setStoreName(request.getStoreName());
        }
        if (request.getDescription() != null)  merchant.setDescription(request.getDescription());
        if (request.getContactEmail() != null)  merchant.setContactEmail(request.getContactEmail());
        if (request.getContactPhone() != null)  merchant.setContactPhone(request.getContactPhone());
        if (request.getLogoUrl() != null)       merchant.setLogoUrl(request.getLogoUrl());

        return MerchantResponse.from(merchantRepository.save(merchant));
    }

    public MerchantResponse getMyProfile(Long userId) {
        return MerchantResponse.from(findByUserIdOrThrow(userId));
    }

    public MerchantResponse getById(Long id) {
        Merchant merchant = merchantRepository
                .findByIdAndStatus(id, MerchantStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Merchant not found: " + id));
        return MerchantResponse.from(merchant);
    }

    // Admin use
    public List<MerchantResponse> listAll() {
        return merchantRepository.findAll()
                .stream().map(MerchantResponse::from).toList();
    }

    @Transactional
    public MerchantResponse updateStatus(Long id, MerchantStatus newStatus) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Merchant not found: " + id));
        merchant.setStatus(newStatus);
        return MerchantResponse.from(merchantRepository.save(merchant));
    }

    // Internal — returns entity for use within the merchant domain
    public Merchant findByUserIdOrThrow(Long userId) {
        return merchantRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        "Merchant profile not found for user: " + userId));
    }

    public Merchant findActiveByIdOrThrow(Long id) {
        return merchantRepository.findByIdAndStatus(id, MerchantStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Merchant not found: " + id));
    }
}