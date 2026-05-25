package com.settleFlow.modules.customer.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateCustomerProfileRequest {

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String city;
    private String country;
}