package org.ene.RPC.core.limit.window.sliding;

import org.ene.RPC.core.limit.window.WindowInfo;

public class SlidingWindowInfo extends WindowInfo {
    // 滑动窗口的长度
    // private final long length;

    // 滑动窗口允许通过的请求的大小
    // private final int count;

    // 滑动窗口中样本窗口的数量
    private final int size;

    // 限流的粒度 方法名，ip
    // private final String key;

    public int getSize() {
        return size;
    }

    public SlidingWindowInfo(long length, int count, String key, int size) {
        super(length, count, key);
        this.size = size;
    }

    public static SlidingWindowInfo create(long length, int count, String key, int size) {
        // 样本窗口数量不能小于1
        return new SlidingWindowInfo(length, count, key, Math.max(size, 1));
    }

}
