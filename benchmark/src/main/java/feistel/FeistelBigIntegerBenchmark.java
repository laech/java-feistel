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
public class FeistelBigIntegerBenchmark {

    @Param("62000")
    private int x;

    private BigInteger input;

    @Param("7")
    private int rounds;

    @Param("320")
    private int a;

    @Param("200")
    private int b;

    private FeistelBigIntegerNumeric1 numeric1;
    private FeistelBigIntegerNumeric2 numeric2;

    @Setup
    public void setup() {
        input = BigInteger.valueOf(x);
        RoundFunction<BigInteger> f = (round, value) -> value;
        BigInteger a_ = BigInteger.valueOf(this.a);
        BigInteger b_ = BigInteger.valueOf(this.b);
        numeric1 = new FeistelBigIntegerNumeric1(rounds, a_, b_, false, f);
        numeric2 = new FeistelBigIntegerNumeric2(rounds, a_, b_, false, f);
    }

    @Benchmark
    public BigInteger numeric1() {
        return numeric1.apply(input);
    }

    @Benchmark
    public BigInteger numeric2() {
        return numeric2.apply(input);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(FeistelBigIntegerBenchmark.class.getName())
                .build();
        new Runner(options).run();
    }
}
