package org.wseresearch.aspects.simple_examples;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;

class TestAspectTest {

    private JoinPoint joinPointNamed(String methodName, Object[] args) {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.getName()).thenReturn(methodName);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(args);
        return joinPoint;
    }

    @Test
    void beforeAdviceLogsTheCalledMethodName() {
        TestAspect aspect = new TestAspect();
        JoinPoint joinPoint = joinPointNamed("doWork", new Object[] {"a", 1});

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(out));
            assertDoesNotThrow(() -> aspect.beforeMethodExecution(joinPoint));
        } finally {
            System.setOut(original);
        }
        assertTrue(out.toString().contains("doWork"),
                "the before-advice should log the invoked method name");
    }

    @Test
    void afterReturningAdviceLogsTheResult() {
        TestAspect aspect = new TestAspect();
        JoinPoint joinPoint = joinPointNamed("compute", new Object[] {});

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(out));
            assertDoesNotThrow(() -> aspect.afterMethodExecution(joinPoint, "42"));
        } finally {
            System.setOut(original);
        }
        String logged = out.toString();
        assertTrue(logged.contains("compute"));
        assertTrue(logged.contains("42"));
    }

    @Test
    void pointcutMethodIsCallable() {
        // the pointcut marker method has an empty body and must not throw
        assertDoesNotThrow(() -> new TestAspect().anyMethodExecution());
    }
}
