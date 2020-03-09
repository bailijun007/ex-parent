package com.hp.sh.expv3.bb.extension.util;

import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

public class StringReplaceUtil {

    public static final String replace(String pattern, Map<String, String> values) {
        StringSubstitutor ss = new StringSubstitutor(values, "%{", "}");
        return ss.replace(pattern);
    }

    public static final String replace(String pattern, Map<String, String> values, String prefix, String suffix) {
        StringSubstitutor ss = new StringSubstitutor(values, prefix, suffix);
        return ss.replace(pattern);
    }
}
