package org.ene.RPC.core.limit.window;


public class WindowInfo {

    // 长度（毫秒）
    private final long length;
    // 窗口允许通过的请求的大小
    private final int count;
    // 限流的粒度 方法名，ip
    private final String key;

    public WindowInfo(long length, int count, String key) {
        this.length = length;
        this.count = count;
        this.key = key;
    }

    public long getLength() {
        return length;
    }

    public int getCount() {
        return count;
    }

    public String getKey() {
        return key;
    }
}
