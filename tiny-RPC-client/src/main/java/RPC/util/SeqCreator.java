package RPC.util;

import java.util.BitSet;
import java.util.Random;

public class SeqCreator {
    private static Random random = new Random();
    private static BitSet bitSet = new BitSet();

    public static Integer getSeq() {
        int seq;
        do {
            seq = random.nextInt(Integer.MAX_VALUE);
        } while (bitSet.get(seq));
        bitSet.set(seq);
        return seq;
    }
}
