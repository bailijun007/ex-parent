/**
 * @author corleone
 * @date 2018/7/6 0006
 */
package com.hp.sh.expv3.match.enums;

public interface BaseBitEnum<T extends BaseBitEnum> {

    int getBit();

    default boolean test(T privilege) {
        return (getBit() & privilege.getBit()) == getBit();
    }

    default boolean test(int privilege) {
        return (getBit() & privilege) == getBit();
    }

    default int add(T p) {
        return getBit() | p.getBit();
    }

    default int add(int p) {
        return getBit() | p;
    }

    default int sub(T p) {
        return getBit() & ~(p.getBit());
    }

    default int sub(int p) {
        return getBit() & ~(p);
    }

    static boolean test(int bit1, int bit2) {
        return (bit1 & bit2) == bit1;
    }

    static int add(int bit1, int bit2) {
        return bit1 | bit2;
    }

    static int sub(int bit1, int bit2) {
        return bit1 & ~(bit2);
    }

}
