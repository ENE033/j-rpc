package org.ene.RPC.core.limit.window.sliding;

import lombok.Data;

@Data
public class SampleWindowInfo {
    int count;
    long length;

    public SampleWindowInfo(long length, int count) {
        this.length = length;
        this.count = count;
    }

    public static SampleWindowInfo create(long length, int count) {
        return new SampleWindowInfo(length, count);
    }

}
