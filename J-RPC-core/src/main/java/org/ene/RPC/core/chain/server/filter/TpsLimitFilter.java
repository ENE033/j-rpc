package org.ene.RPC.core.chain.server.filter;

import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.annotation.TpsLimit;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.server.InvocationWrapper;
import org.ene.RPC.core.constants.CommonConstant;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ene.RPC.core.exception.JRPCException;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 限流处理器
 * 负责流量控制
 */
@Slf4j
@FilterComponent(group = CommonConstant.INVOKER, order = 1)
public class TpsLimitFilter implements InvokerFilter {

    private final TpsLimiter tpsLimiter = new TpsLimiter();

    @Override
    public Object stream(ChainNode nextNode, InvocationWrapper inv) {
        Method method = inv.getMethod();
        // 不限流直接放行
        if (!method.isAnnotationPresent(TpsLimit.class)) {
            return nextNode.stream(inv);
        }
        // 限流器不允许通过
        if (!tpsLimiter.isAllowable(inv)) {
            log.warn("调用服务失败，已经到达服务的最大tps，请求被限流，InvocationWrapper：{}", JSONObject.toJSONString(inv));
            throw new JRPCException(JRPCException.LIMIT_EXCEEDED_EXCEPTION,
                    "调用服务失败，已经到达服务的最大tps，请求被限流，InvocationWrapper："
                            + JSONObject.toJSONString(inv));
        }
        // 限流器允许通过
        return nextNode.stream(inv);
    }


    static class TpsLimiter {
        private final Map<String, Stat> statMap = new ConcurrentHashMap<>();

        boolean isAllowable(InvocationWrapper inv) {
            Method method = inv.getMethod();
            TpsLimit tpsLimit = method.getAnnotation(TpsLimit.class);
            int rate = tpsLimit.rate();
            long interval = tpsLimit.interval();
            String methodKey = method.toGenericString();

            if (rate > 0) {
                Stat stat;
                if ((stat = statMap.get(methodKey)) == null) {
                    statMap.putIfAbsent(methodKey, new Stat(methodKey, rate, interval));
                    stat = statMap.get(methodKey);
                }
                return stat.isAllowable();
            }
            return true;
        }


        @Data
        static class Stat {
            private final String name;

            private final AtomicLong lastResetTime;

            private final long interval;

            private final AtomicInteger allows;

            private final int rate;

            Stat(String name, int rate, long interval) {
                this.name = name;
                this.rate = rate;
                this.interval = interval;
                this.lastResetTime = new AtomicLong(System.currentTimeMillis());
                this.allows = new AtomicInteger(rate);
            }

            public boolean isAllowable() {
                long now = System.currentTimeMillis();
                if (now > lastResetTime.get() + interval) {
                    allows.set(rate);
                    lastResetTime.set(now);
                }
                return allows.get() >= 0 && allows.decrementAndGet() >= 0;
            }
        }

    }

}
