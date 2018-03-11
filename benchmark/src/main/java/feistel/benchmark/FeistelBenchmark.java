package feistel.benchmark;

import feistel.Feistel;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.function.LongUnaryOperator;

@Fork(1)
@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class FeistelBenchmark {

    private static final LongUnaryOperator RF = LongUnaryOperator.identity();
    private static final int ROUNDS = 7;

    private static final Feistel.OfLong ofLong = new Feistel.OfLong(ROUNDS, 32, 32, RF);

    @Param("100001")
    private long input;

    @Benchmark
    public long balanced() {
        return Feistel.balanced(input, ROUNDS, RF);
    }

    @Benchmark
    public long unbalanced() {
        return Feistel.unbalanced(input, ROUNDS, 32, 32, RF);
    }

    @Benchmark
    public long unbalancedOfLong() {
        return ofLong.applyAsLong(input);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(FeistelBenchmark.class.getName())
                .build();
        new Runner(options).run();
    }
}
