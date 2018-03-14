package feistel;

import feistel.Feistel.RoundFunction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.math.BigInteger;
import java.util.stream.LongStream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public final class NumericFeistelSmallDomainTest {

    private final int rounds;
    private final BigInteger m;
    private final BigInteger n;
    private final RoundFunction<BigInteger> f;
    private final Feistel<BigInteger> feistel;

    public NumericFeistelSmallDomainTest(int rounds, int m, int n, RoundFunction<BigInteger> f) {
        this.rounds = rounds;
        this.m = BigInteger.valueOf(m);
        this.n = BigInteger.valueOf(n);
        this.f = f;
        this.feistel = Feistel.numeric(rounds, this.m, this.n, f);
    }

    @Parameters(name = "{0}, {1}, {2}")
    public static Object[][] data() {
        RoundFunction<BigInteger> f = (round, value) ->
                value.multiply(BigInteger.valueOf(31)).shiftLeft(round);
        return new Object[][]{
                {0, 0, 0, f},
                {0, 0, 1, f},
                {0, 1, 1, f},
                {1, 0, 0, f},
                {1, 0, 1, f},
                {1, 1, 1, f},
                {11, 1, 1, f},
                {7, 0, 200, f},
                {7, 320, 0, f},
                {7, 320, 200, f},
                {11, 320, 200, f},
                {12, 99, 99, f},
        };
    }

    @Test
    public void isPermutation() {
        BigInteger count = m.multiply(n);
        assertEquals(count.longValue(), LongStream
                .range(0, count.longValue())
                .mapToObj(BigInteger::valueOf)
                .map(feistel)
                .distinct()
                .peek(i -> {
                    String message = String.valueOf(i);
                    assertTrue(message, i.compareTo(ZERO) >= 0);
                    assertTrue(message, i.compareTo(count) < 0);
                })
                .count());
    }

    @Test
    public void canBeReversed() {
        Feistel<BigInteger> reversed = feistel.reversed();
        for (BigInteger i = ZERO, end = m.multiply(n); i.compareTo(end) < 0; i = i.add(ONE)) {
            BigInteger j = feistel.apply(i);
            String message = String.valueOf(j);
            assertTrue(message, j.compareTo(ZERO) >= 0);
            assertTrue(message, j.compareTo(end) < 0);
            assertEquals(i, reversed.apply(j));
        }
    }

}
