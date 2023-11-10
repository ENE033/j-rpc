package org.ene.RPC.core.constants.mapping;

import com.google.common.collect.HashBiMap;
import org.ene.RPC.core.constants.ClassConstant;

public class StaticDict implements ClassConstant {
    private final static HashBiMap<String, Class<?>> mapping = HashBiMap.create(64);

    static {
        mapping.put(INT_STR, int.class);
        mapping.put(LONG_STR, long.class);
        mapping.put(BYTE_STR, byte.class);
        mapping.put(SHORT_STR, short.class);
        mapping.put(CHAR_STR, char.class);
        mapping.put(FLOAT_STR, float.class);
        mapping.put(DOUBLE_STR, double.class);
        mapping.put(BOOLEAN_STR, boolean.class);
        mapping.put(INT_W_STR, Integer.class);
        mapping.put(LONG_W_STR, Long.class);
        mapping.put(BYTE_W_STR, Byte.class);
        mapping.put(SHORT_W_STR, Short.class);
        mapping.put(CHAR_W_STR, Character.class);
        mapping.put(FLOAT_W_STR, Float.class);
        mapping.put(DOUBLE_W_STR, Double.class);
        mapping.put(BOOLEAN_W_STR, Boolean.class);
        mapping.put(OBJECT_STR, Object.class);
        mapping.put(STRING_STR, String.class);
        mapping.put(INT_ARRAY_STR, int[].class);
        mapping.put(LONG_ARRAY_STR, long[].class);
        mapping.put(BYTE_ARRAY_STR, byte[].class);
        mapping.put(SHORT_ARRAY_STR, short[].class);
        mapping.put(CHAR_ARRAY_STR, char[].class);
        mapping.put(FLOAT_ARRAY_STR, float[].class);
        mapping.put(DOUBLE_ARRAY_STR, double[].class);
        mapping.put(BOOLEAN_ARRAY_STR, boolean[].class);
        mapping.put(INT_W_ARRAY_STR, Integer[].class);
        mapping.put(LONG_W_ARRAY_STR, Long[].class);
        mapping.put(BYTE_W_ARRAY_STR, Byte[].class);
        mapping.put(SHORT_W_ARRAY_STR, Short[].class);
        mapping.put(CHAR_W_ARRAY_STR, Character[].class);
        mapping.put(FLOAT_W_ARRAY_STR, Float[].class);
        mapping.put(DOUBLE_W_ARRAY_STR, Double[].class);
        mapping.put(BOOLEAN_W_ARRAY_STR, Boolean[].class);
        mapping.put(OBJECT_ARRAY_STR, Object[].class);
        mapping.put(STRING_ARRAY_STR, String[].class);
    }

    public static String clazz2Name(Class<?> clazz) {
        return mapping.inverse().get(clazz);
    }

    public static Class<?> name2Clazz(String string) {
        return mapping.get(string);
    }

}
