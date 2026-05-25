package com.settleFlow.modules.customer.service;

import com.settleFlow.modules.banking.service.CreditService;
import com.settleFlow.modules.customer.dto.request.CreateCustomerProfileRequest;
import com.settleFlow.modules.customer.dto.request.UpdateCustomerProfileRequest;
import com.settleFlow.modules.customer.dto.response.CustomerProfileResponse;
import com.settleFlow.modules.customer.model.Customer;
import com.settleFlow.modules.customer.repository.CustomerRepository;
import com.settleFlow.modules.shared.exception.BusinessException;
import com.settleFlow.modules.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CreditService creditService;   // banking domain — via service, not repo

    @Transactional
    public CustomerProfileResponse createProfile(Long userId,
                                                 CreateCustomerProfileRequest request) {
        if (customerRepository.existsByUserId(userId)) {
            throw BusinessException.conflict("Customer profile already exists");
        }

        Customer customer = Customer.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .active(true)
                .build();

        return CustomerProfileResponse.from(customerRepository.save(customer));
    }

    public CustomerProfileResponse getMyProfile(Long userId) {
        Customer customer = findByUserIdOrThrow(userId);

        var credits = creditService.getCustomerCredits(customer.getId());

        return CustomerProfileResponse.from(customer, credits);
    }

    @Transactional
    public CustomerProfileResponse updateProfile(Long userId,
                                                 UpdateCustomerProfileRequest request) {
        Customer customer = findByUserIdOrThrow(userId);

        if (request.getFirstName() != null) customer.setFirstName(request.getFirstName());
        if (request.getLastName()  != null) customer.setLastName(request.getLastName());
        if (request.getPhone()     != null) customer.setPhone(request.getPhone());
        if (request.getAddress()   != null) customer.setAddress(request.getAddress());
        if (request.getCity()      != null) customer.setCity(request.getCity());
        if (request.getCountry()   != null) customer.setCountry(request.getCountry());

        return CustomerProfileResponse.from(customerRepository.save(customer));
    }

   public Customer findByUserIdOrThrow(Long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        "Customer profile not found. Please complete your profile first."));
    }

    public Customer findByIdOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Customer not found: " + id));
    }

    @Transactional
    public void deactivate(Long customerId) {
        Customer customer = findByIdOrThrow(customerId);
        customer.setActive(false);
        customerRepository.save(customer);
    }
}