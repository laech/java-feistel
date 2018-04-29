package feistel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class FeistelTest extends BaseTest {

    private static final Feistel<Integer> increment = Feistel.of(
            Math::incrementExact,
            Math::decrementExact
    );

    private static final Feistel<Integer> negate = Feistel.of(
            Math::negateExact,
            Math::negateExact
    );

    @Test
    void compose() {
        Feistel<Integer> composed = negate.compose(increment);
        assertEquals(-8, (int) composed.apply(7));
    }

    @Test
    void andThen() {
        Feistel<Integer> composed = negate.andThen(increment);
        assertEquals(-6, (int) composed.apply(7));
    }

    @Test
    void reversed() {
        Feistel<Integer> decrement = increment.reversed();
        assertEquals(7, (int) decrement.apply(increment.apply(7)));
        assertEquals(7, (int) decrement.compose(increment).apply(7));
        assertEquals(7, (int) decrement.andThen(increment).apply(7));
    }

    @Test
    void identity() {
        Feistel<Integer> id = Feistel.identity();
        assertEquals(7, (int) id.apply(7));
        assertEquals(7, (int) id.reversed().apply(7));
        assertEquals(7, (int) id.compose(id).apply(7));
        assertEquals(7, (int) id.andThen(id).apply(7));
    }
}
