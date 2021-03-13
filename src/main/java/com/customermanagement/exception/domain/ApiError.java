package com.customermanagement.exception.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError implements Serializable {
    private static final long serialVersionUID = 2043577371392984463L;
    private int code;
    private String message;
}
