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
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class FeistelBenchmark {

    private static final IntUnaryOperator RF = IntUnaryOperator.identity();

    @Param("100001")
    private int input;

    @Param({"4", "7"})
    private int rounds;

    @Benchmark
    public int balanced() {
        return Feistel.compute(input, rounds, RF);
    }

    @Benchmark
    public int unbalanced() {
        return Feistel.unbalanced(input, rounds, 16, RF);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(FeistelBenchmark.class.getName())
                .build();
        new Runner(options).run();
    }
}
