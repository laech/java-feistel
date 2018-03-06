package feistel;

import java.util.function.IntUnaryOperator;

public final class Feistel {

    private Feistel() {
    }

    public static int unbalanced(int input, int rounds, int s, IntUnaryOperator rf) {
        int mask = ~(0xffffffff << s);
        for (int i = 0; i < rounds; i++) {
            int a = input >>> s;
            int b = input & mask;
            int f = rf.applyAsInt(b) & mask;
            int a_ = a;
            a = b;
            b = a_ ^ f;
            input = (a << s | b);
        }
        int a = input >>> s;
        int b = input & mask;
        return (b << s | a);
    }

    public static int compute(int input, int rounds, IntUnaryOperator rf) {
        int a = input >>> 16;
        int b = input & 0xffff;
        for (int i = 0; i < rounds; i++) {
            int F = rf.applyAsInt(b) & 0xffff;
            int a_ = a;
            a = b;
            b = a_ ^ F;
        }
        return (b << 16) | a;
    }

    public static IntUnaryOperator compute(int rounds, IntUnaryOperator rf) {
        return input -> compute(input, rounds, rf);
    }

}
