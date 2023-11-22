package org.ene.RPC.core.util;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class SeqUtil {
    private static final ConcurrentHashMap<Integer, Method> seqMap = new ConcurrentHashMap<>();

    public static Integer getSeq(Method method) {
        int seq;
        do {
            seq = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        } while (seqMap.contains(seq));
        seqMap.put(seq, method);
        return seq;
    }

    public static Method getMethod(int seq) {
        return seqMap.get(seq);
    }

    public static void removeSeq(Integer seq) {
        seqMap.remove(seq);
    }
}
