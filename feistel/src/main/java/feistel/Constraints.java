package feistel;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

final class Constraints {
    private Constraints() {
    }

    static void requireNonNegative(BigInteger value, String name) {
        if (value.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(
                    name + " cannot be negative: " + value);
        }
    }

    static void requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    name + " cannot be negative: " + value);
        }
    }

    static void requireNonNegative(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    name + " cannot be negative: " + value);
        }
    }

    static void requireNonNegative(long value, long max) {
        if (value < 0 || value > max) {
            throw new IllegalArgumentException(
                    "value out of range (min=0, max=" + max + "): " + value);
        }
    }

    static void requireNonNegative(BigInteger value, BigInteger max) {
        if (value.compareTo(ZERO) < 0 || value.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                    "value out of range (min=0, max=" + max + "): " + value);
        }
    }

    static BigInteger calculateMax(int totalBits) {
        return ONE.shiftLeft(totalBits).subtract(ONE);
    }
}
