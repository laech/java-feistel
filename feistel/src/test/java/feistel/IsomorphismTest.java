package feistel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class IsomorphismTest extends BaseTest {

    private static final Isomorphism<Integer, String> serialize =
            Isomorphism.of(
                    String::valueOf,
                    Integer::parseInt
            );

    private static final Isomorphism<Integer, Integer> negate =
            Isomorphism.of(
                    Math::negateExact,
                    Math::negateExact
            );

    private static final Isomorphism<Integer, Integer> increment =
            Isomorphism.of(
                    Math::incrementExact,
                    Math::decrementExact
            );

    @Test
    void compose() {
        assertEquals(-8, (int) negate
                .compose(increment)
                .compose(serialize.inverse())
                .apply("7"));
    }

    @Test
    void andThen() {
        assertEquals("-6", negate
                .andThen(increment)
                .andThen(serialize)
                .apply(7));
    }

    @Test
    void inverse() {
        Isomorphism<Integer, Integer> decrement = increment.inverse();
        assertEquals(7, (int) decrement.apply(increment.apply(7)));
        assertEquals(7, (int) decrement.compose(increment).apply(7));
        assertEquals(7, (int) decrement.andThen(increment).apply(7));
    }

    @Test
    void identity() {
        Isomorphism<Integer, Integer> id = Isomorphism.identity();
        assertEquals(7, (int) id.apply(7));
        assertEquals(7, (int) id.inverse().apply(7));
        assertEquals(7, (int) id.compose(id).apply(7));
        assertEquals(7, (int) id.andThen(id).apply(7));
    }
}
