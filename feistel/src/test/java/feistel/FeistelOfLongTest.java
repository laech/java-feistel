package feistel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class FeistelOfLongTest extends BaseTest {

    private static final Feistel<Long> increment = Feistel.ofLong(
            Math::incrementExact,
            Math::decrementExact
    );

    private static final Feistel<Long> negate = Feistel.ofLong(
            Math::negateExact,
            Math::negateExact
    );

    @Test
    void compose() {
        Feistel<Long> composed = negate.compose(increment);
        assertEquals(-8, (long) composed.apply(7L));
    }

    @Test
    void andThen() {
        Feistel<Long> composed = negate.andThen(increment);
        assertEquals(-6, (long) composed.apply(7L));
    }

    @Test
    void reversed() {
        Feistel<Long> decrement = increment.reversed();
        assertEquals(7, (long) decrement.apply(increment.apply(7L)));
        assertEquals(7, (long) decrement.compose(increment).apply(7L));
        assertEquals(7, (long) decrement.andThen(increment).apply(7L));
    }

    @Test
    void identity() {
        Feistel<Long> id = Feistel.identity();
        assertEquals(7, (long) id.apply(7L));
        assertEquals(7, (long) id.reversed().apply(7L));
        assertEquals(7, (long) id.compose(id).apply(7L));
        assertEquals(7, (long) id.andThen(id).apply(7L));
    }
}
