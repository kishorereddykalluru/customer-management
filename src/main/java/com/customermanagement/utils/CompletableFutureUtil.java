package com.customermanagement.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class CompletableFutureUtil {

    public String method1(){
        log.info("one");
        String join = null;
        for(int i = 0; i < 5; i++)
            join = String.join(",", "method-1-"+i);
        return join;
    }

    public String method2(){
        log.info("two");
        String join = null;
        for(int i = 0; i < 5; i++)
            join =  String.join(",", "method-2-"+i);
        return join;
    }

    public String method3(){
        log.info("three");
        String join = null;
        for(int i = 0; i < 5; i++)
            join =  String.join(",", "method-3-"+i);
        return join;
    }


    public String method4(){
        log.info("four");
        String join = null;
        for(int i = 0; i < 5; i++)
            join =  String.join(",", "method-4-"+i);
        return join;
    }
}
