package feistel;

import isomorphic.Isomorphism.OfInt;
import isomorphic.Isomorphism.OfLong;

/**
 * Generalized <a href="https://en.wikipedia.org/wiki/Feistel_cipher">Feistel</a>
 * functions.
 * <p>
 * Apply a Feistel function will throw {@linkplain IllegalArgumentException}
 * if the value is considered to be invalid by the specific Feistel function,
 * for example, you have created a 24-bit function but passed in a value that
 * is greater than 2<sup>24</sup> - 1.
 */
public final class Feistel {
    private Feistel() {
    }

    public static OfLong ofLongBinary(int rounds, RoundFunction.OfLong f) {
        return FeistelOfLongBinary.unbalanced(rounds, 64, 32, 32, f);
    }

    public static OfLong ofLongBinary(int rounds, int sourceBits, int targetBits, RoundFunction.OfLong f) {
        return FeistelOfLongBinary.unbalanced(rounds, 64, sourceBits, targetBits, f);
    }

    public static OfLong ofLongNumeric1(int rounds, long a, long b, RoundFunction.OfLong f) {
        return FeistelOfLongNumeric.fe1(rounds, a, b, f);
    }

    public static OfLong ofLongNumeric2(int rounds, long a, long b, RoundFunction.OfLong f) {
        return FeistelOfLongNumeric.fe2(rounds, a, b, f);
    }

    public static OfInt ofIntBinary(int rounds, RoundFunction.OfInt f) {
        return new IntFeistelImpl(FeistelOfLongBinary.unbalanced(
                rounds, 32, 16, 16, IntFeistelImpl.toRoundFunction64(f)));
    }

    public static OfInt ofIntBinary(int rounds, int sourceBits, int targetBits, RoundFunction.OfInt f) {
        return new IntFeistelImpl(FeistelOfLongBinary.unbalanced(
                rounds, 32, sourceBits, targetBits, IntFeistelImpl.toRoundFunction64(f)));
    }

    public static OfInt ofIntNumeric1(int rounds, int a, int b, RoundFunction.OfInt f) {
        IntFeistelImpl.checkNumeric(a, b);
        return new IntFeistelImpl(ofLongNumeric1(
                rounds, a, b, IntFeistelImpl.toRoundFunction64(f)));
    }

    public static OfInt ofIntNumeric2(int rounds, int a, int b, RoundFunction.OfInt f) {
        IntFeistelImpl.checkNumeric(a, b);
        return new IntFeistelImpl(ofLongNumeric2(
                rounds, a, b, IntFeistelImpl.toRoundFunction64(f)));
    }

}
