package feistel;

import java.util.function.LongUnaryOperator;

public interface Feistel64 extends LongUnaryOperator {

    Feistel64 reversed();

}
