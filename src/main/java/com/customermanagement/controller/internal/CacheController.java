package com.customermanagement.controller.internal;

import com.customermanagement.service.CustomerCacheServiceWithPostConstruct;
import com.hazelcast.core.HazelcastInstance;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
public class CacheController implements ErrorController {

    private static final String ERROR_URL = "${server.error.path:${error.path:/error}}";

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private CustomerCacheServiceWithPostConstruct service;

    @PostMapping("/cache/refresh")
    @Operation(hidden = true)
    public void refreshCacheValues(){
        service.initialize(null); //call method to refresh cache
    }

    @DeleteMapping("/cache/{cacheName}")
    @Operation(hidden = true)
    public ResponseEntity cachePurge(@PathVariable("cacheName") String cacheName){
        try {
            Objects.requireNonNull(hazelcastInstance.getMap(cacheName)).clear();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e){
            log.error("{}", ExceptionUtils.getRootCauseMessage(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    @Override
    public String getErrorPath() {
        return ERROR_URL;
    }
}
