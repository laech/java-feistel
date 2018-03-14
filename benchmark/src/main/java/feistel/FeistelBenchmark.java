package feistel;

import feistel.Feistel.RoundFunction;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(1)
@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class FeistelBenchmark {

    @Param("100001")
    private long input;

    @Param("7")
    private int rounds;

    private Feistel.OfLong balanced;
    private Feistel.OfLong balancedReversed;
    private Feistel.OfLong unbalanced;
    private Feistel.OfLong unbalancedReversed;

    @Setup
    public void setup() {
        unbalanced = new UnbalancedFeistelOfLong(
                rounds, 64, 32, 32, false, RoundFunction.OfLong.identity());
        unbalancedReversed = unbalanced.reversed();

        balanced = new BalancedFeistelOfLong(
                rounds, false, RoundFunction.OfLong.identity());
        balancedReversed = balanced.reversed();
    }

    @Benchmark
    public long balanced() {
        return balanced.applyAsLong(input);
    }

    @Benchmark
    public long balancedReversed() {
        return balancedReversed.applyAsLong(input);
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
