package feistel;

@FunctionalInterface
public interface IntRoundFunction {

    int applyAsInt(int round, int input);

}
