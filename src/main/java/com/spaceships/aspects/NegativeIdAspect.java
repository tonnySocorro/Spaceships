package com.spaceships.aspects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NegativeIdAspect {
    private static final Logger logger = LoggerFactory.getLogger(NegativeIdAspect.class);

    @Before("execution(* com.spaceships.controllers.SpaceshipsController.getSpaceship(..))  ")
    public void logIfNegativeId(JoinPoint jp) {
        Object[] args = jp.getArgs();

        if (args.length > 0 && args[0] instanceof Long) {
            Long id = (Long) args[0];
            if (id < 0) {
                logger.warn("Se ha recibido un id negativo: " + id);
            }
        }
    }
}