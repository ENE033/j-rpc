package RPC.core.chain;

import RPC.core.annotation.FilterComponent;
import RPC.core.chain.AbstractFilterChain;
import RPC.core.chain.ChainNode;
import RPC.core.chain.Filter;
import RPC.core.chain.InvocationWrapper;
import RPC.core.constants.CommonConstant;

import java.util.Objects;

public class ServerFilterChain extends AbstractFilterChain {

    protected static ChainNode head = new ChainNode(new AbstractFilterChain.HeadFiler());
    protected static ChainNode tail = new ChainNode(new AbstractFilterChain.TailFiler());

    static {
        ChainNode last = tail;
        for (int i = sortedClassList.size() - 1; i >= 0; i--) {
            Class<?> aClass = sortedClassList.get(i);
            FilterComponent annotation = aClass.getAnnotation(FilterComponent.class);
            if (Objects.equals(annotation.group(), CommonConstant.SERVER)) {
                try {
                    Filter filter = (Filter) aClass.newInstance();
                    last = new ChainNode(last, filter);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        head.setNext(last);
    }

    public static void handler(InvocationWrapper inv) {
        head.invoke(inv);
    }

}

