package org.javaproteam27.socialnetwork.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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

    @Around("infoLoggerMethod()")
    public Object infoLogger(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Request = path: [{}], method: [{}], arguments: {} ", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        Object result = joinPoint.proceed();
        log.info("Response = path: [{}], method: [{}], return: [{}] ", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), result);
        return result;
    }

    @Around("debugLoggerMethod()")
    public Object debugLogger(ProceedingJoinPoint joinPoint) throws Throwable {
        String args = Arrays.toString(joinPoint.getArgs());
        logDebug.debug("Request = path: [{}], method: [{}], arguments: {} ",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), args);
        try {
            Object result = joinPoint.proceed();
            logDebug.debug("Response = path: [{}], method: [{}], return: [{}] ",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result);

        } catch (Exception e) {
            logDebug.debug("An error has occurred", e.fillInStackTrace());
            e.printStackTrace();
        }
        return joinPoint.proceed();
    }
}
