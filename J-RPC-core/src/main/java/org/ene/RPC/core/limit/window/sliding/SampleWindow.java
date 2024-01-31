package org.ene.RPC.core.limit.window.sliding;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class SampleWindow {

    private final SampleWindowInfo sampleWindowInfo;

    // 时间id
    private final AtomicLong timeId;

    // 窗口当前的允许的请求量
    private final AtomicInteger allows;

    SampleWindow(SampleWindowInfo sampleWindowInfo, long timeId) {
        this.sampleWindowInfo = sampleWindowInfo;
        this.timeId = new AtomicLong(timeId);
        this.allows = new AtomicInteger(sampleWindowInfo.getCount());
    }

    boolean allowable() {
        return allows.get() >= 0 && allows.decrementAndGet() >= 0;
    }

    boolean updateWindowTimeIdCas(long oldTimeId, long newTimeId) {
        return timeId.compareAndSet(oldTimeId, newTimeId);
    }

    // 线程不安全，外层使用cas保证线程安全
    void resetCount() {
        allows.set(sampleWindowInfo.getCount());
    }
}
