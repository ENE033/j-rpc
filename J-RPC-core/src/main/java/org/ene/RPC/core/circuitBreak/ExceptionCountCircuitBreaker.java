package org.ene.RPC.core.circuitBreak;

import lombok.Data;
import org.ene.RPC.core.annotation.CircuitBreakRule;
import org.ene.RPC.core.constants.CircuitBreakConstant;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ExceptionCountCircuitBreaker implements CircuitBreaker {

    Map<String, Stat> statMap = new ConcurrentHashMap<>();

    @Override
    public boolean tryPass(CircuitBreakRule circuitBreakRule, Method method) {
        String name = method.getName();

        Stat stat;
        if ((stat = statMap.get(name)) == null) {
            statMap.putIfAbsent(name, new Stat(circuitBreakRule.timeOut()));
            stat = statMap.get(name);
        }

        AtomicInteger state = stat.getState();

        return state.get() == CircuitBreakConstant.CLOSE
                || (state.get() == CircuitBreakConstant.OPEN
                && stat.isTimeOut()
                && stat.halfOpen2open());
    }

    @Override
    public void receiveResult(String methodName, boolean success) {
        Stat stat = statMap.get(methodName);
        if (stat == null || stat.getState().get() == CircuitBreakConstant.CLOSE) {
            return;
        }
        if (stat.getState().get() == CircuitBreakConstant.HALF_OPEN) {
            boolean x = success ? stat.halfOpen2close() : stat.halfOpen2open();
        }
    }

    @Data
    public static class Stat {

        volatile AtomicInteger state;

        long lastOpen;

        long timeOut;

        Stat(long timeOut) {
            state.set(CircuitBreakConstant.CLOSE);
            this.timeOut = timeOut;
        }

        boolean isTimeOut() {
            return System.currentTimeMillis() - lastOpen > timeOut;
        }

        boolean halfOpen2close() {
            return state.compareAndSet(CircuitBreakConstant.HALF_OPEN, CircuitBreakConstant.CLOSE);
        }

        boolean close2open() {
            if (state.compareAndSet(CircuitBreakConstant.CLOSE, CircuitBreakConstant.OPEN)) {
                lastOpen = System.currentTimeMillis();
                return true;
            }
            return false;
        }

        boolean halfOpen2open() {
            if (state.compareAndSet(CircuitBreakConstant.HALF_OPEN, CircuitBreakConstant.OPEN)) {
                lastOpen = System.currentTimeMillis();
                return true;
            }
            return false;
        }

        boolean open2halfOpen() {
            return state.compareAndSet(CircuitBreakConstant.OPEN, CircuitBreakConstant.HALF_OPEN);
        }
    }

}
