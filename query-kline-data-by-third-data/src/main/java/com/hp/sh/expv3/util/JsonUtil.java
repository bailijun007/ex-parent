package com.hp.sh.expv3.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.ValueFilter;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/4/9
 */
public final class JsonUtil {
    public static SerializeFilter COMMON_FORMATTER = new JsonUtil.BigDecimalValueFilter();

    public JsonUtil() {
    }

    public static final String toJsonString(Object o) {
//        return JSON.toJSONString(o, COMMON_FORMATTER, new SerializerFeature[]{SerializerFeature.WriteBigDecimalAsPlain});
        return JSON.toJSONString(o);
    }

    public static void main(String[] args) {
        BigDecimal[] b2 = new BigDecimal[]{new BigDecimal("1.0030000000"), BigDecimal.valueOf(2.0003D)};
        System.out.println(toJsonString(b2));
        JsonUtil.T[] ts = new JsonUtil.T[]{new JsonUtil.T(new BigDecimal("1.0030000000"), "dsa"), new JsonUtil.T(new BigDecimal("1.0030000000"), "dsa")};
        System.out.println(toJsonString(ts));
    }

    public static class T {
        public BigDecimal d;
        public String name;

        public T(BigDecimal d, String name) {
            this.d = d;
            this.name = name;
        }
    }

    public static class BigDecimalValueFilter implements ValueFilter {
        public BigDecimalValueFilter() {
        }

        @Override
        public Object process(Object object, String name, Object value) {
            return null != value && value instanceof BigDecimal ? ((BigDecimal)value).stripTrailingZeros() : value;
        }
    }
}

