package org.ene.RPC.core.limit.window.fix;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FixWindow {

    private final Map<String, Stat> statMap = new ConcurrentHashMap<>();

    public boolean allowable(FixWindowInfo windowInfo) {
        int count = windowInfo.getCount();
        String key = windowInfo.getKey();

        if (count <= 0) {
            return true;
        }

        Stat stat;
        if ((stat = statMap.get(key)) == null) {
            statMap.putIfAbsent(key, new Stat(windowInfo));
            stat = statMap.get(key);
        }
        return stat.allowable();
    }

    @Data
    static class Stat {
        private final FixWindowInfo windowInfo;

        // 上一次Reset的时间
        private final AtomicLong lastResetTime;

        // 窗口当前的允许的请求量
        private final AtomicInteger allows;


        Stat(FixWindowInfo windowInfo) {
            this.windowInfo = windowInfo;
            this.lastResetTime = new AtomicLong(System.currentTimeMillis());
            this.allows = new AtomicInteger(windowInfo.getCount());
        }

        public boolean allowable() {
            long now = System.currentTimeMillis();
            if (now > lastResetTime.get() + windowInfo.getLength()) {
                allows.set(windowInfo.getCount());
                lastResetTime.set(now);
            }
            return allows.get() >= 0 && allows.decrementAndGet() >= 0;
        }
    }

}
