package org.ene.RPC.core.chain;

import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.constants.CommonConstant;
import java.util.Objects;

public class ClientFilterChain extends AbstractFilterChain {

    protected ChainNode head = new ChainNode(new AbstractFilterChain.HeadFiler());
    protected ChainNode tail = new ChainNode(new AbstractFilterChain.TailFiler());

    public ClientFilterChain() {
        ChainNode last = tail;
        for (int i = sortedClassList.size() - 1; i >= 0; i--) {
            Class<?> aClass = sortedClassList.get(i);
            FilterComponent annotation = aClass.getAnnotation(FilterComponent.class);
            if (Objects.equals(annotation.group(), CommonConstant.CLIENT)) {
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

    public void handler(InvocationWrapper inv) {
        head.invoke(inv);
    }
}
