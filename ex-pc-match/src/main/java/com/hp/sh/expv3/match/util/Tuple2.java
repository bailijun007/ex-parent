/**
 * @author 10086
 * @date 2019/10/16
 */
package com.hp.sh.expv3.match.util;

public class Tuple2<A, B> {

    public final A first;

    public final B second;

    public Tuple2(A a, B b) {
        first = a;
        second = b;
    }

    public String toString() {
        return "(" + first + ", " + second + ")";
    }

}