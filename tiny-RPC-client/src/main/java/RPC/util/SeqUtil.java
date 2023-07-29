package RPC.util;

import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class SeqUtil {
    private static final ConcurrentHashMap<Integer, Object> seqSet = new ConcurrentHashMap<>();
    private static final Object PRESENT = new Object();

    public static Integer getSeq() {
        int seq;
        do {
            seq = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        } while (seqSet.contains(seq));
        seqSet.put(seq, PRESENT);
        return seq;
    }

    public static void removeSeq(Integer seq) {
        seqSet.remove(seq);
    }
}
