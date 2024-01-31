package org.ene.RPC.core.limit.window.fix;

import lombok.Data;
import org.ene.RPC.core.limit.window.AbstractWindow;
import org.ene.RPC.core.limit.window.Window;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class FixWindow extends AbstractWindow implements Window {

    @Data
    public static class Stat implements AbstractWindow.Stat {
        private final FixWindowInfo windowInfo;

        // 上一次Reset的时间
        private final AtomicLong lastResetTime;

        // 窗口当前的允许的请求量
        private final AtomicInteger allows;

        public Stat(FixWindowInfo windowInfo) {
            this.windowInfo = windowInfo;
            this.lastResetTime = new AtomicLong(System.currentTimeMillis());
            this.allows = new AtomicInteger(windowInfo.getCount());
        }

        AtomicBoolean updating = new AtomicBoolean(false);

        @Override
        public boolean allowable() {
            long now = System.currentTimeMillis();

            if (now > lastResetTime.get() + windowInfo.getLength()) {
                synchronized (this) {
                    if (now > lastResetTime.get() + windowInfo.getLength()) {
                        allows.set(windowInfo.getCount());
                        lastResetTime.set(now);
                    }
                }
            }

//            while (now > lastResetTime.get() + windowInfo.getLength()) {
//                if (updating.compareAndSet(false, true)) {
//                    if (now > lastResetTime.get() + windowInfo.getLength()) {
//                        allows.set(windowInfo.getCount());
//                        lastResetTime.set(now);
//                        updating.set(false);
//                    }
//                } else {
//                    Thread.yield();
//                }
//            }

            return allows.get() >= 0 && allows.decrementAndGet() >= 0;
        }
    }

}
