package com.customermanagement.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema()
public class CustomerDetails implements Serializable {

    private static final long serialVersionUID = -8174621394448158907L;
    private long id;
    private String customerName;
    private String contactName;
    private String city;
    private String country;
}
