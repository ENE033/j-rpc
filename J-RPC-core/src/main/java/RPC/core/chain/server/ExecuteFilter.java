package RPC.core.chain.server;

import RPC.core.annotation.FilterComponent;
import RPC.core.annotation.SyncRPC;
import RPC.core.chain.ChainNode;
import RPC.core.chain.InvocationWrapper;
import RPC.core.constants.CommonConstant;
import RPC.core.execute.ExecuteStrategy;
import RPC.core.execute.ExecuteStrategyMap;

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
