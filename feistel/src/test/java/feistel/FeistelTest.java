package feistel;

import org.junit.Test;

import java.util.function.IntUnaryOperator;

import static org.junit.Assert.assertEquals;

public final class FeistelTest {

    @Test
    public void canBeReversed() {
        IntUnaryOperator f1 = Feistel.compute(7, (value) -> value * 12345);
        IntUnaryOperator f2 = Feistel.compute(8, (value) -> value * 54321);
        for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; i++) {
            assertEquals(i, f1.applyAsInt(f1.applyAsInt(i)));
            assertEquals(i, f2.applyAsInt(f2.applyAsInt(i)));
        }
    }

}
