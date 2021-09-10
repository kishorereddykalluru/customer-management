package com.customermanagement.aspect;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.StringJoiner;

/**
 * Aspect to use PerfProfiler annotation and print performance stats in ms on a public method.
 */
@Aspect
@Component
public class PerfProfilerAspect {

    private static final String TRACE = "trace";
    private static final String DEBUG = "debug";

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    private void repositories(){
        //Pointcut definition to match Spring Repository class join points
    }

    @Pointcut("execution(@com.customermanagement.aspect.PerfProfiler * *(..)) && @annotation(perfProfiler)")
    private void profilerAnn(PerfProfiler perfProfiler){
        //Pointcut definition to match Profiler annotated join points
    }

    @Around("repositories()")
    public Object logExecutionTimeForDatabaseCalls(ProceedingJoinPoint pjp) throws Throwable {
        StringJoiner tagNameJoiner= new StringJoiner(".");
        tagNameJoiner.add("perfProfiler");

        String[] fullyQualifiedClassArr = StringUtils.split(pjp.getSignature().getDeclaringTypeName(), ".");
        if(ArrayUtils.isNotEmpty(fullyQualifiedClassArr)){
            tagNameJoiner.add(fullyQualifiedClassArr[fullyQualifiedClassArr.length-1])
                    .add(pjp.getSignature().getName());
            return executeStopWatch(tagNameJoiner.toString(), pjp, TRACE);
        }
        return pjp.proceed();
    }

    @Around("profilerAnn(perfProfiler)")
    public Object logExecutionTimeForProfilerAnnotation(ProceedingJoinPoint pjp, PerfProfiler perfProfiler) throws Throwable {
        String tagName = perfProfiler.tag();
        if(StringUtils.isEmpty(tagName)){
            StringJoiner tagNameJoiner = new StringJoiner(".");
            tagNameJoiner.add("perfProfiler");

            String[] fullyQualifiedClassArr = StringUtils.split(pjp.getSignature().getDeclaringTypeName(), ".");
            if(ArrayUtils.isNotEmpty(fullyQualifiedClassArr)){
                tagNameJoiner.add(fullyQualifiedClassArr[fullyQualifiedClassArr.length-1])
                        .add(pjp.getSignature().getName());
                tagName = tagNameJoiner.toString();
            }
        }

        return executeStopWatch(tagName, pjp, DEBUG);
    }

    private Object executeStopWatch(String tag, ProceedingJoinPoint pjp, String logLevel) throws Throwable {
        tag = StringUtils.defaultString(tag, "dummy");

        StopWatch stopWatch = new StopWatch(tag);

        stopWatch.start();
        try {
            return pjp.proceed();
        } finally {
            stopWatch.stop();
            Logger logger = LoggerFactory.getLogger(tag);
            if(logLevel.equalsIgnoreCase(DEBUG) || logLevel.equalsIgnoreCase(TRACE)){
                logger.debug("{} ms", stopWatch.getTotalTimeMillis());
            }
        }

    }
}
