package org.ene.RPC.core.limit.window.fix;

import lombok.Data;

@Data
public class FixWindowInfo {
    // 窗口允许通过的请求的大小
    private final int count;
    // 长度（毫秒）
    private final long length;
    // 限流的粒度 方法名，ip
    private final String key;

    public static FixWindowInfo create(int count, long length, String key) {
        return new FixWindowInfo(count, length, key);
    }

}
