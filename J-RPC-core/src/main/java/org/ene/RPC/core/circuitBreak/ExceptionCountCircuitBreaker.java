package org.ene.RPC.core.circuitBreak;

import lombok.Data;
import org.ene.RPC.core.annotation.CircuitBreakRule;
import org.ene.RPC.core.constants.CircuitBreakConstant;
import org.ene.RPC.core.limit.window.fix.FixWindow;
import org.ene.RPC.core.limit.window.fix.FixWindowInfo;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ExceptionCountCircuitBreaker implements CircuitBreaker {

    Map<String, Breaker> breakerMap = new ConcurrentHashMap<>();

    private final FixWindow fixWindow = new FixWindow();

    @Override
    public boolean tryPass(CircuitBreakRule circuitBreakRule, Method method) {
        String name = method.getName();

        Breaker breaker;
        if ((breaker = breakerMap.get(name)) == null) {
            breakerMap.putIfAbsent(name, new Breaker(circuitBreakRule.timeOut()));
            breaker = breakerMap.get(name);
        }

        AtomicInteger state = breaker.getState();

        return state.get() == CircuitBreakConstant.CLOSE
                || (state.get() == CircuitBreakConstant.OPEN
                && breaker.isTimeOut()
                && breaker.open2halfOpen());
    }

    @Override
    public void receiveResult(CircuitBreakRule circuitBreakRule, Method method, boolean success) {
        String methodKey = method.toGenericString();
        Breaker breaker = breakerMap.get(methodKey);
        // 没有breaker，或者当前熔断器的状态为开启，不做处理
        if (breaker == null || breaker.getState().get() == CircuitBreakConstant.OPEN) {
            return;
        }
        // 如果熔断器的状态为半开启，那么判断此次调用是否成功，成功则关闭，不成功则开启
        if (breaker.getState().get() == CircuitBreakConstant.HALF_OPEN) {
            boolean ignore = success ? breaker.halfOpen2close() : breaker.halfOpen2open();
            return;
        }
        // 如果熔断器的状态为关闭
        // 调用失败时，统计失败的次数
        // 如果失败次数到达上限，那么熔断器打开
        int count = circuitBreakRule.exceptionCountThreshold();
        long length = circuitBreakRule.interval();
        if (!success && !fixWindow.allowable(FixWindowInfo.create(count,length,methodKey))) {
            breaker.close2open();
        }
    }

    @Data
    public static class Breaker {

        volatile AtomicInteger state;

        long lastOpen;

        long timeOut;

        Breaker(long timeOut) {
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
