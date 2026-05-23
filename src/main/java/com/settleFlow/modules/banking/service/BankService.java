package com.settleFlow.modules.banking.service;

import com.settleFlow.modules.banking.dto.request.CreateBankRequest;
import com.settleFlow.modules.banking.dto.request.UpdateBankRequest;
import com.settleFlow.modules.banking.dto.response.BankResponse;
import com.settleFlow.modules.banking.model.Bank;
import com.settleFlow.modules.banking.repository.BankRepository;
import com.settleFlow.modules.shared.exception.BusinessException;
import com.settleFlow.modules.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    public List<BankResponse> listActiveBanks() {
        return bankRepository.findAllByActiveTrue()
                .stream()
                .map(BankResponse::from)
                .toList();
    }

    public List<BankResponse> listAllBanks() {
        return bankRepository.findAll()
                .stream()
                .map(BankResponse::from)
                .toList();
    }

    public BankResponse getById(Long id) {
        return BankResponse.from(findActiveOrThrow(id));
    }

    @Transactional
    public BankResponse create(CreateBankRequest request) {
        if (bankRepository.existsByName(request.getName())) {
            throw BusinessException.conflict("A bank with this name already exists");
        }

        validateInstallmentRange(request.getMinInstallmentMonths(),
                request.getMaxInstallmentMonths());

        Bank bank = Bank.builder()
                .name(request.getName())
                .defaultCreditLimit(request.getDefaultCreditLimit())
                .interestRate(request.getInterestRate())
                .minInstallmentMonths(request.getMinInstallmentMonths())
                .maxInstallmentMonths(request.getMaxInstallmentMonths())
                .active(true)
                .build();

        return BankResponse.from(bankRepository.save(bank));
    }

    @Transactional
    public BankResponse update(Long id, UpdateBankRequest request) {
        Bank bank = findOrThrow(id);

        if (request.getDefaultCreditLimit() != null)
            bank.setDefaultCreditLimit(request.getDefaultCreditLimit());

        if (request.getInterestRate() != null)
            bank.setInterestRate(request.getInterestRate());

        if (request.getMinInstallmentMonths() != null)
            bank.setMinInstallmentMonths(request.getMinInstallmentMonths());

        if (request.getMaxInstallmentMonths() != null)
            bank.setMaxInstallmentMonths(request.getMaxInstallmentMonths());

        if (request.getActive() != null)
            bank.setActive(request.getActive());

        validateInstallmentRange(bank.getMinInstallmentMonths(),
                bank.getMaxInstallmentMonths());

        return BankResponse.from(bankRepository.save(bank));
    }

    @Transactional
    public void deactivate(Long id) {
        Bank bank = findOrThrow(id);
        bank.setActive(false);
        bankRepository.save(bank);
    }

    // Internal use by other services — returns the entity, not a DTO
    public Bank findActiveOrThrow(Long id) {
        return bankRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new NotFoundException("Bank not found or inactive: " + id));
    }

    public Bank findOrThrow(Long id) {
        return bankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bank not found: " + id));
    }

    private void validateInstallmentRange(int min, int max) {
        if (min > max) {
            throw BusinessException.badRequest(
                    "Min installment months cannot exceed max installment months");
        }
    }
}