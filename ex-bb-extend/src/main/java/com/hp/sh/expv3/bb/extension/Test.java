package com.hp.sh.expv3.bb.extension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/9
 */
public class Test {

    public static void main(String[] args) {
        final TreeMap<Integer, Integer> treeMap = buildTargetFrequence2TriggerFrequence(new ArrayList<Integer>() {{
            add(1);
            add(5);
            add(10);
            add(15);
            add(30);
            add(60);
            add(360);
            add(1440);
        }});

        for (Map.Entry<Integer, Integer> integerIntegerEntry : treeMap.entrySet()) {
            System.out.println(integerIntegerEntry.getKey() + ":" + integerIntegerEntry.getValue());
        }
        System.out.println("============");
        TreeMap<Integer, TreeSet<Integer>> trigger2TarFrequence = buildTrigger2TarFrequence(treeMap);
        for (Map.Entry<Integer, TreeSet<Integer>> kv : trigger2TarFrequence.entrySet()) {
            System.out.println(kv.getKey() + "--->" + String.join(";",kv.getValue().stream().map(t -> "" + t).collect(Collectors.toList())));
        }

    }


    private static TreeMap<Integer, TreeSet<Integer>> buildTrigger2TarFrequence(TreeMap<Integer, Integer> targetFreq2TriggerFreq) {
        TreeMap<Integer, TreeSet<Integer>> trigger2Tar = new TreeMap<>();
        final Set<Map.Entry<Integer, Integer>> kvs = targetFreq2TriggerFreq.entrySet();
        for (Map.Entry<Integer, Integer> kv : kvs) {
            final Integer targetFreq = kv.getKey();
            final Integer triggerFreq = kv.getValue();
            TreeSet<Integer> targetFreqs = trigger2Tar.get(triggerFreq);
            if (targetFreqs == null) {
                targetFreqs = new TreeSet<Integer>();
                trigger2Tar.put(triggerFreq, targetFreqs);
            }
            targetFreqs.add(targetFreq);
        }
        return trigger2Tar;
    }

    private static TreeMap<Integer, Integer> buildTargetFrequence2TriggerFrequence(List<Integer> supportFrequence) {
        // 1,5,10,15,30
        supportFrequence.sort(Comparator.naturalOrder());
        TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();

        for (int i = 0; i < supportFrequence.size(); i++) {
            if (i == 0) {
                continue;
            } else {
                map.put(supportFrequence.get(i), 1);
            }
        }

        for (Integer tarFreq : map.keySet()) {
            final Integer oldTriggerFreq = map.get(tarFreq);
            for (Integer nextTriggerFreq : supportFrequence) {
                if (nextTriggerFreq >= tarFreq) {
                    break;
                }
                if (nextTriggerFreq < tarFreq && nextTriggerFreq > oldTriggerFreq) {
                    BigDecimal remainder = BigDecimal.valueOf(tarFreq).divideAndRemainder(BigDecimal.valueOf(nextTriggerFreq))[1];
                    if (0 == BigDecimal.ZERO.compareTo(remainder)) {
                        map.put(tarFreq, nextTriggerFreq);
                    }
                }
            }
        }
        return map;
    }

}
