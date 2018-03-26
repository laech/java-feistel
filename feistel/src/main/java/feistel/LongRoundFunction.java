package feistel;

@FunctionalInterface
public interface LongRoundFunction {

    long applyAsLong(int round, long input);

}
