package feistel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class FeistelOfIntTest extends BaseTest {

    private static final Feistel.OfInt increment = Feistel.ofInt(
            Math::incrementExact,
            Math::decrementExact
    );

    private static final Feistel.OfInt negate = Feistel.ofInt(
            Math::negateExact,
            Math::negateExact
    );

    @Test
    void compose() {
        Feistel.OfInt composed = negate.compose(increment);
        assertEquals(-8, (int) composed.apply(7));
    }

    @Test
    void andThen() {
        Feistel.OfInt composed = negate.andThen(increment);
        assertEquals(-6, (int) composed.apply(7));
    }

    @Test
    void inverse() {
        Feistel.OfInt decrement = increment.inverse();
        assertEquals(7, (int) decrement.apply(increment.apply(7)));
        assertEquals(7, (int) decrement.compose(increment).apply(7));
        assertEquals(7, (int) decrement.andThen(increment).apply(7));
    }

    @Test
    void identity() {
        Feistel.OfInt id = Feistel.OfInt.identity();
        assertEquals(7, (int) id.apply(7));
        assertEquals(7, (int) id.inverse().apply(7));
        assertEquals(7, (int) id.compose(id).apply(7));
        assertEquals(7, (int) id.andThen(id).apply(7));
    }
}
