package RPC.core.chain;


import RPC.core.annotation.FilterComponent;
import cn.hutool.core.util.ClassUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractFilterChain implements FilterChain {

    private static final Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation("RPC.core.chain", FilterComponent.class);
    public static List<Class<?>> sortedClassList;

    @Slf4j
    public static class HeadFiler implements Filter {
        @Override
        public void invoke(ChainNode nextNode, InvocationWrapper inv) {
            log.info("责任链开始执行，InvocationWrapper：" + JSONObject.toJSONString(inv));
            nextNode.invoke(inv);
        }
    }

    @Slf4j
    public static class TailFiler implements Filter {
        @Override
        public void invoke(ChainNode nextNode, InvocationWrapper inv) {
            log.info("责任链到达链尾，InvocationWrapper：" + JSONObject.toJSONString(inv));

        }
    }

    static {
        sortedClassList = classes.stream().sorted((a, b) -> {
            FilterComponent aAnnotation = a.getAnnotation(FilterComponent.class);
            FilterComponent bAnnotation = b.getAnnotation(FilterComponent.class);
            return Double.compare(aAnnotation.order(), bAnnotation.order());
        }).collect(Collectors.toList());
    }

}
