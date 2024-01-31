package org.ene.RPC.core.limit.window;


public interface Window {
    boolean allowable(WindowInfo windowInfo);
}
