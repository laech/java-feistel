package feistel;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Fork(1)
@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class FeistelBinaryBenchmark {

    @Param("100001")
    private long input;

    private BigInteger inputBigInteger;

    @Param("7")
    private int rounds;

    private LongFeistelBinaryBalanced longBalanced;
    private LongFeistelBinaryUnbalanced longUnbalanced;
    private BigIntegerFeistelBinaryBalanced bigIntegerBalanced;
    private BigIntegerFeistelBinaryUnbalanced bigIntegerUnbalanced;

    @Setup
    public void setup() {
        inputBigInteger = BigInteger.valueOf(input);

        LongRoundFunction f64 = (round, value) -> value;
        RoundFunction<BigInteger> f = (round, value) -> value;

        longUnbalanced = new LongFeistelBinaryUnbalanced(rounds, 64, 32, 32, false, f64);
        longBalanced = new LongFeistelBinaryBalanced(rounds, 64, false, f64);
        bigIntegerBalanced = new BigIntegerFeistelBinaryBalanced(rounds, 64, false, f);
        bigIntegerUnbalanced = new BigIntegerFeistelBinaryUnbalanced(rounds, 64, 32, 32, false, f);
    }

    @Benchmark
    public long longBalanced() {
        return longBalanced.applyAsLong(input);
    }

    @Benchmark
    public long longUnbalanced() {
        return longUnbalanced.applyAsLong(input);
    }

    @Benchmark
    public BigInteger bigIntegerBalanced() {
        return bigIntegerBalanced.apply(inputBigInteger);
    }

    @Benchmark
    public BigInteger bigIntegerUnbalanced() {
        return bigIntegerUnbalanced.apply(inputBigInteger);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(FeistelBinaryBenchmark.class.getName())
                .build();
        new Runner(options).run();
    }
}
