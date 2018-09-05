package com.github.dapeng.serviceBenchmark;

import com.github.dapeng.core.SoaException;
import com.github.dapeng.demo.DemoServiceClient;
import com.github.dapeng.demo.domain.Demo;
import com.github.dapeng.demo.service.DemoService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author hui
 * @date 2018/8/30 0030 16:26
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
@Fork(1)
public class ServiceBenchmark {
    private Demo demo;
    private DemoService service;

    @Setup(Level.Trial)
    public void serviceTestInit(){
        demo = new Demo();
        demo.expectCost = 10;
        demo.bytes = 20;
        service = new DemoServiceClient();
    }

     /**
      * Benchmark                                     Mode  Cnt   Score   Error  Units  Thread   synchronize
      * sericeBenchmark.ServiceBenchmark.serviceTest  avgt    5  13.336 ± 0.381  ms/op    1         ENABLE
      * sericeBenchmark.ServiceBenchmark.serviceTest  avgt    5  13.341 ± 0.811  ms/op    1         DISABLE
      * sericeBenchmark.ServiceBenchmark.serviceTest  avgt    5  13.670 ± 6.984  ms/op    2         ENABLE
      * sericeBenchmark.ServiceBenchmark.serviceTest  avgt    5  12.788 ± 0.791  ms/op    2         DISABLE
      * sericeBenchmark.ServiceBenchmark.serviceTest  avgt    5  14.480 ± 5.672  ms/op    5         ENABLE
      * sericeBenchmark.ServiceBenchmark.serviceTest  avgt    5  14.398 ± 4.636  ms/op    5         DISABLE
      * sericeBenchmark.ServiceBenchmark.serviceTest  avgt    5  16.687 ± 7.586  ms/op    10        ENABLE
      * sericeBenchmark.ServiceBenchmark.serviceTest  avgt    5  15.281 ± 6.309  ms/op    10        DISABLE
      *
      */
    @Benchmark
    public String serviceTest() throws SoaException {
        return service.demoTest(demo);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ServiceBenchmark.class.getSimpleName())
                .syncIterations(true)
                .build();
        new Runner(opt).run();
    }
}
