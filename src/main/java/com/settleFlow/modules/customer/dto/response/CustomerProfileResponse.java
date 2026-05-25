package com.settleFlow.modules.customer.dto.response;

import com.settleFlow.modules.banking.dto.response.CustomerCreditResponse;
import com.settleFlow.modules.customer.model.Customer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class CustomerProfileResponse {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String address;
    private String city;
    private String country;
    private Boolean active;
    private LocalDateTime createdAt;

   private List<CustomerCreditResponse> credits;

    public static CustomerProfileResponse from(Customer c) {
        return CustomerProfileResponse.builder()
                .id(c.getId())
                .userId(c.getUserId())
                .firstName(c.getFirstName())
                .lastName(c.getLastName())
                .fullName(c.getFullName())
                .phone(c.getPhone())
                .address(c.getAddress())
                .city(c.getCity())
                .country(c.getCountry())
                .active(c.getActive())
                .createdAt(c.getCreatedAt())
                .build();
    }

    public static CustomerProfileResponse from(Customer c,
                                               List<CustomerCreditResponse> credits) {
        CustomerProfileResponse response = from(c);
        return CustomerProfileResponse.builder()
                .id(response.getId())
                .userId(response.getUserId())
                .firstName(response.getFirstName())
                .lastName(response.getLastName())
                .fullName(response.getFullName())
                .phone(response.getPhone())
                .address(response.getAddress())
                .city(response.getCity())
                .country(response.getCountry())
                .active(response.getActive())
                .createdAt(response.getCreatedAt())
                .credits(credits)
                .build();
    }
}