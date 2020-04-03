package com.hp.sh.expv3.pc.trade.util;


import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class StringReplaceUtil {

    public static final String replace(String pattern, Map<String, String> values) {
        StringSubstitutor ss = new StringSubstitutor(values, "#{", "}");
        return ss.replace(pattern);
    }

    public static final String replace(String pattern, Map<String, String> values, String prefix, String suffix) {
        StringSubstitutor ss = new StringSubstitutor(values, prefix, suffix);
        return ss.replace(pattern);
    }

    public static String buildNamespace(String pattern, String asset, String symbol, String prefix) {
        return StringReplaceUtil.replace(pattern, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("prefix",  prefix);
        }});
    }

}
