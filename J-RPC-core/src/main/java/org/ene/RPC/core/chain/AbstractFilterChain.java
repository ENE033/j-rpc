package org.ene.RPC.core.chain;


import org.ene.RPC.core.annotation.FilterComponent;
import cn.hutool.core.util.ClassUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 责任链抽象，需要确保每个过滤器线程安全的
 */
public abstract class AbstractFilterChain implements FilterChain {

    private static final Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation("org.ene.RPC.core.chain", FilterComponent.class);
    public static final List<Class<?>> sortedClassList;

    static {
        sortedClassList = classes.stream().sorted((a, b) -> {
            FilterComponent aAnnotation = a.getAnnotation(FilterComponent.class);
            FilterComponent bAnnotation = b.getAnnotation(FilterComponent.class);
            return Double.compare(aAnnotation.order(), bAnnotation.order());
        }).collect(Collectors.toList());
    }

    protected ChainNode head = new ChainNode(new AbstractFilterChain.HeadFiler());
    protected ChainNode tail = new ChainNode(new AbstractFilterChain.TailFiler());

    public AbstractFilterChain(String group) {
        ChainNode last = tail;
        for (int i = sortedClassList.size() - 1; i >= 0; i--) {
            Class<?> aClass = sortedClassList.get(i);
            FilterComponent annotation = aClass.getAnnotation(FilterComponent.class);
            if (Objects.equals(annotation.group(), group)) {
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

    public void handler(Flow flow) {
        head.stream(flow);
    }

    @Slf4j
    public static class HeadFiler implements Filter {
        @Override
        public Object filter(ChainNode nextNode, Flow flow) {
            log.info("责任链开始执行，Flow：{}", JSONObject.toJSONString(flow));
            return nextNode.stream(flow);
        }
    }

    @Slf4j
    public static class TailFiler implements Filter {
        @Override
        public Object filter(ChainNode nextNode, Flow flow) {
            log.info("责任链到达链尾，Flow：{}", JSONObject.toJSONString(flow));
            return null;
        }
    }


}
