package com.maids.libms.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.maids.libms.controller.*.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        logger.info("Starting execution of {}.{}", className, methodName);
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            
            logger.info("{}.{} executed successfully in {} ms", 
                className, methodName, stopWatch.getTotalTimeMillis());
            
            return result;
        } catch (Exception e) {
            logger.error("Exception in {}.{}: {}", 
                className, methodName, e.getMessage(), e);
            throw e;
        }
    }

    @AfterReturning(
        pointcut = "execution(* com.maids.libms.controller.BookController.*(..)) || " +
                  "execution(* com.maids.libms.controller.BorrowController.*(..))",
        returning = "result"
    )
    public void logSuccessfulOperations(Object result) {
        logger.info("Operation completed successfully. Result: {}", result);
    }

    @AfterThrowing(
        pointcut = "execution(* com.maids.libms.controller.*.*(..))",
        throwing = "exception"
    )
    public void logException(Exception exception) {
        logger.error("Operation failed with exception: {}", exception.getMessage(), exception);
    }
} 