package org.javaproteam27.socialnetwork.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggerAspect {

    private final Logger logDebug = LoggerFactory.getLogger("DebugLogging");

    @Pointcut("@annotation(InfoLogger) || @within(InfoLogger)")
    public void infoLoggerMethod() {
        // Do nothing because is name of log method
    }

    @Pointcut("@annotation(DebugLogger) || @within(DebugLogger)")
    public void debugLoggerMethod() {
        // Do nothing because is name of log method
    }

    @Before("debugLoggerMethod()")
    public void beforeDebugLog(JoinPoint joinPoint) {
        String args = Arrays.toString(joinPoint.getArgs());
        logDebug.debug("Request = path: [{}], method: [{}], arguments: {} ",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), args);
    }

    @AfterReturning(pointcut = "debugLoggerMethod()", returning = "entity")
    public void afterDebugLog(JoinPoint joinPoint, Object entity) {
        logDebug.debug("Response = path: [{}], method: [{}], return: [{}] ",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), entity);
    }

    @AfterThrowing(pointcut = "debugLoggerMethod()", throwing = "e")
    public void afterThrowingDebugLog(Exception e) {
        logDebug.debug("An error has occurred", e.fillInStackTrace());
        e.printStackTrace();
    }

    @Around("infoLoggerMethod()")
    public Object infoLogger(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Request = path: [{}], method: [{}], arguments: {} ", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        Object result = joinPoint.proceed();
        log.info("Response = path: [{}], method: [{}], return: [{}] ", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), result);
        return result;
    }
}
