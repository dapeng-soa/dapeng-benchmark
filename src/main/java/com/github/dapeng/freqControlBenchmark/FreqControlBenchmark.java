package com.github.dapeng.freqControlBenchmark;

import com.github.dapeng.core.FreqControlRule;
import com.github.dapeng.impl.filters.freq.ShmManager;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * @author hui
 * @date 2018/8/29 0029 20:28
 */

@BenchmarkMode({Mode.AverageTime,Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Thread)
public class FreqControlBenchmark {

    @State(Scope.Benchmark)
    public static class RuleType {
        private FreqControlRule ipRule;
        private FreqControlRule idRule;
    }

    public ShmManager shmManager;

    @Setup
    public void freqControlTestInit( RuleType ruleType) {
        shmManager = ShmManager.getInstance();

        ruleType.idRule = new FreqControlRule();
        ruleType.idRule.app = "com.today.hello";
        ruleType.idRule.ruleType = "callId";
        ruleType.idRule.targets = new HashSet<Integer>(){{add(45);add(56);}};
        ruleType.idRule.minInterval = 60;
        ruleType.idRule.maxReqForMinInterval = 10;
        ruleType.idRule.midInterval = 3600;
        ruleType.idRule.maxReqForMidInterval = 50;
        ruleType.idRule.maxInterval = 86400;
        ruleType.idRule.maxReqForMaxInterval = 80;

        ruleType.ipRule = new FreqControlRule();
        ruleType.ipRule.app = "com.today.hello";
        ruleType.ipRule.ruleType = "callIp";
        ruleType.ipRule.targets = new HashSet<Integer>(){{add(15245255);add(-54455852);}};
        ruleType.ipRule.minInterval = 60;
        ruleType.ipRule.maxReqForMinInterval = 10;
        ruleType.ipRule.midInterval = 3600;
        ruleType.ipRule.maxReqForMidInterval = 50;
        ruleType.ipRule.maxInterval = 86400;
        ruleType.ipRule.maxReqForMaxInterval = 80;
    }


    /**
     * dapengVersion 2.0.5
     *
     * Benchmark                                              Mode  Cnt     Score      Error   Units
     * freqControlBenchmark.FreqControlBenchmark.ipRuleTest  thrpt    5  6854.374 ± 1214.048  ops/ms
     * freqControlBenchmark.FreqControlBenchmark.ipRuleTest   avgt    5    ≈ 10⁻⁴              ms/op
     */
    @Benchmark
    @Fork(1)
    public int ipRuleTest(RuleType ruleType) {
        shmManager.reportAndCheck(ruleType.ipRule,-12546356);
        return 0;
    }

     /**
      * dapengVersion 2.0.5
      *
      * Benchmark                                              Mode  Cnt     Score      Error   Units
      * freqControlBenchmark.FreqControlBenchmark.idRuleTest  thrpt    5  6564.251 ±  449.920  ops/ms
      * freqControlBenchmark.FreqControlBenchmark.idRuleTest   avgt    5    ≈ 10⁻⁴              ms/op
      */
    @Benchmark
    @Fork(1)
    public int idRuleTest(RuleType ruleType) {
        shmManager.reportAndCheck(ruleType.idRule,256);
        return 0;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FreqControlBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
