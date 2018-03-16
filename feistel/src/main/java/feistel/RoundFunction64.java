package feistel;

@FunctionalInterface
public interface RoundFunction64 {

    long applyAsLong(int round, long input);

}
