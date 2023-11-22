package org.ene.RPC.core.chain.client.filter;

import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.client.SenderWrapper;
import org.ene.RPC.core.constants.CommonConstant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@FilterComponent(group = CommonConstant.SENDER, order = 1)
public class MethodFilter implements SenderFilter {
    @Override
    public Object filter(ChainNode nextNode, SenderWrapper senderWrapper) {
        Method method = senderWrapper.getMethod();
        Class<?> clazz = senderWrapper.getClazz();
        Object[] args = senderWrapper.getArgs();
        Class<?>[] argsClazz = method.getParameterTypes();
        try {
            clazz.getDeclaredMethod(method.getName(), argsClazz);
        } catch (NoSuchMethodException e) {
            Object backed = null;
            try {
                backed = method.invoke(senderWrapper, args);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
            return backed;
        }
        return nextNode.stream(senderWrapper);
    }
}
