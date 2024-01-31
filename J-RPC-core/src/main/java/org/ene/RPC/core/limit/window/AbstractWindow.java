package org.ene.RPC.core.limit.window;

import org.ene.RPC.core.limit.window.fix.FixWindow;
import org.ene.RPC.core.limit.window.fix.FixWindowInfo;
import org.ene.RPC.core.limit.window.sliding.SlidingWindow;
import org.ene.RPC.core.limit.window.sliding.SlidingWindowInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractWindow implements Window {

    private final Map<String, Stat> statMap = new ConcurrentHashMap<>();

    // 模板方法
    public boolean allowable(WindowInfo windowInfo) {
        int count = windowInfo.getCount();
        String key = windowInfo.getKey();
        if (count <= 0) {
            return true;
        }
        Stat stat;
        if ((stat = statMap.get(key)) == null) {
            if (windowInfo instanceof FixWindowInfo) {
                statMap.putIfAbsent(key, new FixWindow.Stat((FixWindowInfo) windowInfo));
            } else if (windowInfo instanceof SlidingWindowInfo) {
                statMap.putIfAbsent(key, new SlidingWindow.Stat((SlidingWindowInfo) windowInfo));
            }
            stat = statMap.get(key);
        }
        return stat.allowable();
    }

    public interface Stat {
        boolean allowable();
    }

}
