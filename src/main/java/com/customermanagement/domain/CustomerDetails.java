package com.customermanagement.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema()
public class CustomerDetails {

    private long id;
    private String customerName;
    private String contactName;
    private String city;
    private String country;
}
