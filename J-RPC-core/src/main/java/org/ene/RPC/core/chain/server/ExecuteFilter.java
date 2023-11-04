package org.ene.RPC.core.chain.server;

import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.annotation.SyncRPC;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.InvocationWrapper;
import org.ene.RPC.core.constants.CommonConstant;
import org.ene.RPC.core.execute.ExecuteStrategy;
import org.ene.RPC.core.execute.ExecuteStrategyMap;

import java.lang.reflect.Method;

@FilterComponent(group = CommonConstant.SERVER, order = 128)
public class ExecuteFilter implements SFilter {
    @Override
    public void invoke(ChainNode nextNode, InvocationWrapper inv) {
        Method method = inv.getMethod();
        ExecuteStrategy executeStrategy = ExecuteStrategyMap.get(method.isAnnotationPresent(SyncRPC.class) ? ExecuteStrategy.SYNC : ExecuteStrategy.ASYNC);
        inv.setExecuteStrategy(executeStrategy);
        nextNode.invoke(inv);
    }
}
