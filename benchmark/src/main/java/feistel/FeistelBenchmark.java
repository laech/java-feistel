package feistel;

import feistel.Feistel.RoundFunction;
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

    @Param("100001")
    private long input;

    @Param("7")
    private int rounds;

    private Feistel.OfLong unbalanced;
    private Feistel.OfLong unbalancedReversed;

    @Setup
    public void setup() {
        unbalanced = new UnbalancedFeistelOfLong(
                rounds, 64, 32, 32, false, RoundFunction.OfLong.identity());
        unbalancedReversed = unbalanced.reversed();
    }

    @Benchmark
    public long balanced() {
        return Feistel.balanced(input, rounds, RF);
    }

    @Benchmark
    public long unbalancedReversed() {
        return unbalancedReversed.applyAsLong(input);
    }

    @Benchmark
    public long unbalanced() {
        return unbalanced.applyAsLong(input);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(FeistelBenchmark.class.getName())
                .build();
        new Runner(options).run();
    }
}
