package RPC.core.chain;

import RPC.core.annotation.FilterComponent;
import RPC.core.chain.AbstractFilterChain;
import RPC.core.chain.Filter;
import RPC.core.constants.CommonConstant;
import cn.hutool.core.util.ClassUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ClientFilterChain extends AbstractFilterChain {

    protected static List<Filter> filters = new ArrayList<>();

    static {
        for (Class<?> aClass : sortedClassList) {
            FilterComponent annotation = aClass.getAnnotation(FilterComponent.class);
            if (Objects.equals(annotation.group(), CommonConstant.CLIENT)) {
                try {
                    filters.add((Filter) aClass.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
