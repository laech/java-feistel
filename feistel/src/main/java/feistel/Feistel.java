package feistel;

import isomorphic.Isomorphism;
import isomorphic.Isomorphism.OfInt;
import isomorphic.Isomorphism.OfLong;

import java.math.BigInteger;

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

    /**
     * Returns an arbitrary-sized binary Feistel.
     * <p>
     * This implementation is adapted from
     * <em>Unbalanced Feistel Networks and Block-Cipher Design</em>
     * by Bruce Schneier and John Kelsey.
     *
     * @param totalBits  total number of bits, defining the set of valid
     *                   elements of the domain and codomain of the returned
     *                   function - {0,1,...,2<sup>totalBits</sup> - 1}
     * @param sourceBits number of bits of the source block (the left half)
     * @param targetBits number of bits of the target block (the right half)
     * @param rounds     total number of rounds
     * @param f          the round function
     * @throws IllegalArgumentException if any of the following is true:
     *                                  <ul>
     *                                  <li>
     *                                  {@code totalBits},
     *                                  {@code sourceBits},
     *                                  {@code targetBits}, or
     *                                  {@code rounds}
     *                                  is negative</li>
     *                                  <li>
     *                                  {@code sourceBits} +
     *                                  {@code targetBits} &gt;
     *                                  {@code totalBits}
     *                                  </li>
     *                                  </ul>
     * @throws NullPointerException     if the round function is null
     */
    public static Isomorphism<BigInteger, BigInteger> ofBigIntegerBinary(
            int totalBits,
            int sourceBits,
            int targetBits,
            int rounds,
            RoundFunction<BigInteger> f
    ) {
        return FeistelOfBigIntegerBinary.unbalanced(
                rounds, totalBits, sourceBits, targetBits, f
        );
    }

    /**
     * Returns an arbitrary-sized numeric Feistel.
     * <p>
     * This implements the <em>FE2</em> algorithm from <em>Format-Preserving Encryption</em>
     * by Mihir Bellare, Thomas Ristenpart, Phillip Rogaway, and Till Stegers.
     * <p>
     * {@code a x b} defines the set of valid elements of the domain and codomain
     * of the returned function - {0,1,...,a x b - 1}
     *
     * @param rounds total number of rounds
     * @param f      the round function
     * @throws IllegalArgumentException if {@code a}, {@code b}, or {@code rounds}
     *                                  is negative
     * @throws NullPointerException     if {@code a}, {@code b}, or {@code f} is null
     */
    public static Isomorphism<BigInteger, BigInteger> ofBigIntegerNumeric(
            BigInteger a,
            BigInteger b,
            int rounds,
            RoundFunction<BigInteger> f
    ) {
        return FeistelOfBigIntegerNumeric.fe2(rounds, a, b, f);
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
