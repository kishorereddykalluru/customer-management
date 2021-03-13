package com.customermanagement.exception.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrors implements Serializable {
    private static final long serialVersionUID = -3104421728891908156L;
    private List<ApiError> apiErrors;
}
