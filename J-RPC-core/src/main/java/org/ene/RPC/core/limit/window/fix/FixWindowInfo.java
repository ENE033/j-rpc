package org.ene.RPC.core.limit.window.fix;

import org.ene.RPC.core.limit.window.WindowInfo;

public class FixWindowInfo extends WindowInfo {

    public FixWindowInfo(long length, int count, String key) {
        super(length, count, key);
    }

    public static FixWindowInfo create(long length, int count, String key) {
        return new FixWindowInfo(length, count, key);
    }

}
