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
public class FeistelNumericBenchmark {

    @Param("62000")
    private int input;

    private BigInteger bigInput;

    @Param("7")
    private int rounds;

    @Param("320")
    private int a;

    @Param("200")
    private int b;

    private Feistel64Numeric1 longFeistel1;
    private Feistel64Numeric2 longFeistel2;
    private FeistelBigIntegerNumeric1 bigFeistel1;
    private FeistelBigIntegerNumeric2 bigFeistel2;

    @Setup
    public void setup() {
        bigInput = BigInteger.valueOf(input);

        RoundFunction<BigInteger> f = (round, value) -> value;
        BigInteger a_ = BigInteger.valueOf(a);
        BigInteger b_ = BigInteger.valueOf(b);
        bigFeistel1 = new FeistelBigIntegerNumeric1(rounds, a_, b_, false, f);
        bigFeistel2 = new FeistelBigIntegerNumeric2(rounds, a_, b_, false, f);

        RoundFunction64 f64 = (round, value) -> value;
        longFeistel1 = new Feistel64Numeric1(rounds, a, b, false, f64);
        longFeistel2 = new Feistel64Numeric2(rounds, a, b, false, f64);
    }

    @Benchmark
    public BigInteger bigIntegerNumeric1() {
        return bigFeistel1.apply(bigInput);
    }

    @Benchmark
    public BigInteger bigIntegerNumeric2() {
        return bigFeistel2.apply(bigInput);
    }

    @Benchmark
    public long longNumeric1() {
        return longFeistel1.applyAsLong(input);
    }

    @Benchmark
    public long longNumeric2() {
        return longFeistel2.applyAsLong(input);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(FeistelNumericBenchmark.class.getName())
                .build();
        new Runner(options).run();
    }
}
