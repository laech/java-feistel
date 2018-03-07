package feistel.benchmark;

import feistel.Feistel;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.function.IntUnaryOperator;

@Fork(1)
@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class FeistelBenchmark {

    private static final IntUnaryOperator RF = IntUnaryOperator.identity();
    private static final int ROUNDS = 7;

    private static final Feistel.OfInt ofInt = new Feistel.OfInt(ROUNDS, 16, 16, RF);

    @Param("100001")
    private int input;

    @Benchmark
    public int balanced() {
        return Feistel.balanced(input, ROUNDS, RF);
    }

    @Benchmark
    public int unbalanced() {
        return Feistel.unbalanced(input, ROUNDS, 16, 16, RF);
    }

    @Benchmark
    public int unbalancedOfInt() {
        return ofInt.applyAsInt(input);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(FeistelBenchmark.class.getName())
                .build();
        new Runner(options).run();
    }
}
