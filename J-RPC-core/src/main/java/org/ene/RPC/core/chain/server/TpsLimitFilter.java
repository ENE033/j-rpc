package org.ene.RPC.core.chain.server;

import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.annotation.TpsLimit;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.InvocationWrapper;
import org.ene.RPC.core.constants.CommonConstant;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@FilterComponent(group = CommonConstant.SERVER, order = 1)
public class TpsLimitFilter implements SFilter {

    private final TpsLimiter tpsLimiter = new TpsLimiter();

    @Override
    public void invoke(ChainNode nextNode, InvocationWrapper inv) {
        Method method = inv.getMethod();
        // 不限流直接放行
        if (!method.isAnnotationPresent(TpsLimit.class)) {
            nextNode.invoke(inv);
            return;
        }
        if (!tpsLimiter.isAllowable(inv)) {
            log.info("调用服务失败，已经到达服务的最大tps，请求被限流，InvocationWrapper：{}", JSONObject.toJSONString(inv));
            return;
        }
        nextNode.invoke(inv);
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
                return allows.decrementAndGet() >= 0;
            }
        }

    }

}
