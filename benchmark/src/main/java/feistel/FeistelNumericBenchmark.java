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

    private LongFeistelNumeric1 longNumeric1;
    private LongFeistelNumeric2 longNumeric2;
    private BigIntegerFeistelNumeric1 bigNumeric1;
    private BigIntegerFeistelNumeric2 bigNumeric2;

    @Setup
    public void setup() {
        bigInput = BigInteger.valueOf(input);

        RoundFunction<BigInteger> f = (round, value) -> value;
        BigInteger a_ = BigInteger.valueOf(a);
        BigInteger b_ = BigInteger.valueOf(b);
        bigNumeric1 = new BigIntegerFeistelNumeric1(rounds, a_, b_, false, f);
        bigNumeric2 = new BigIntegerFeistelNumeric2(rounds, a_, b_, false, f);

        RoundFunction.OfLong f64 = (round, value) -> value;
        longNumeric1 = new LongFeistelNumeric1(rounds, a, b, false, f64);
        longNumeric2 = new LongFeistelNumeric2(rounds, a, b, false, f64);
    }

    @Benchmark
    public BigInteger bigIntegerNumeric1() {
        return bigNumeric1.apply(bigInput);
    }

    @Benchmark
    public BigInteger bigIntegerNumeric2() {
        return bigNumeric2.apply(bigInput);
    }

    @Benchmark
    public long longNumeric1() {
        return longNumeric1.applyAsLong(input);
    }

    @Benchmark
    public long longNumeric2() {
        return longNumeric2.applyAsLong(input);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(FeistelNumericBenchmark.class.getName())
                .build();
        new Runner(options).run();
    }
}
