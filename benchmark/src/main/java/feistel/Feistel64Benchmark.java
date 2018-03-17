package feistel;

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
public class Feistel64Benchmark {

    @Param("100001")
    private long input;

    @Param("7")
    private int rounds;

    private Feistel64 balanced;
    private Feistel64 balancedReversed;
    private Feistel64 unbalanced;
    private Feistel64 unbalancedReversed;

    @Setup
    public void setup() {
        RoundFunction64 f = (round, value) -> value;

        unbalanced = new Feistel64Unbalanced(rounds, 64, 32, 32, false, f);
        unbalancedReversed = unbalanced.reversed();

        balanced = new Feistel64Balanced(rounds, false, f);
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
                .include(Feistel64Benchmark.class.getName())
                .build();
        new Runner(options).run();
    }
}
