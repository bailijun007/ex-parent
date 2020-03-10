/**
 * @author zw
 * @date 2019/8/2
 */
package com.hp.sh.expv3.bb.kline.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;

import java.math.BigDecimal;

public final class JsonUtil {

    public final static String toJsonString(Object o) {
        return JSON.toJSONString(o, COMMON_FORMATTER, SerializerFeature.WriteBigDecimalAsPlain);
    }

    public final static SerializeFilter COMMON_FORMATTER = new BigDecimalValueFilter();

    public static class BigDecimalValueFilter implements ValueFilter {

        @Override
        public Object process(Object object, String name, Object value) {
            if (null != value && value instanceof BigDecimal) {
                return ((BigDecimal) value).stripTrailingZeros();
            }
            return value;
        }

    }

    public static class T {
        public BigDecimal d;
        public String name;

        public T(BigDecimal d, String name) {
            this.d = d;
            this.name = name;
        }
    }

    public static void main(String[] args) {
        BigDecimal[] b2 = new BigDecimal[]{new BigDecimal("1.0030000000"), BigDecimal.valueOf(2.00030000d)};

        System.out.println(JsonUtil.toJsonString(b2));

        T[] ts = new T[]{
                new T(new BigDecimal("1.0030000000"), "dsa"),
                new T(new BigDecimal("1.0030000000"), "dsa")
        };

        System.out.println(JsonUtil.toJsonString(ts));

    }


}