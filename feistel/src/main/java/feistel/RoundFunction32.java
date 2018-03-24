package feistel;

@FunctionalInterface
public interface RoundFunction32 {

    int applyAsInt(int round, int input);

}
