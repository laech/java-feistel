package feistel;

import feistel.Feistel.RoundFunction;
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
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
public class BigIntegerFeistelBenchmark {

    @Param("100001")
    private int x;

    private BigInteger input;

    @Param("7")
    private int rounds;

    @Param("320")
    private int a;

    @Param("200")
    private int b;

    private Feistel<BigInteger> feistel;

    @Setup
    public void setup() {
        input = BigInteger.valueOf(x);
        feistel = Feistel.numeric(
                rounds,
                BigInteger.valueOf(a),
                BigInteger.valueOf(b),
                RoundFunction.identity());
    }

    @Benchmark
    public BigInteger numeric() {
        return feistel.apply(input);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(BigIntegerFeistelBenchmark.class.getName())
                .build();
        new Runner(options).run();
    }
}
