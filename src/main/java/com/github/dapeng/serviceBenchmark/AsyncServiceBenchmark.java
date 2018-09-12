package com.github.dapeng.serviceBenchmark;

import com.github.dapeng.core.SoaException;
import com.github.dapeng.demo.DemoServiceAsyncClient;
import com.github.dapeng.demo.domain.Demo;
import com.github.dapeng.demo.service.DemoServiceAsync;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author hui
 * @date 2018/9/12 0012 14:04
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, batchSize = 10)
@Measurement(iterations = 5, batchSize = 50)
@State(Scope.Benchmark)
public class AsyncServiceBenchmark {
    private Demo demo;
    private DemoServiceAsync serviceAsync;

    @Setup(Level.Iteration)
    public void serviceTestInit() {
        demo = new Demo();
        demo.expectCost = 10;
        demo.bytes = 20;
        serviceAsync = new DemoServiceAsyncClient();
    }

    /**
     * dapengVersion 2.0.5
     * Benchmark                                                  Mode  Cnt    Score    Error  Units   Thread   ThreadPool    Log     batchSize
     * serviceBenchmark.AsyncServiceBenchmark.serviceAsyncTest     ss    5  1.453 ± 2.232  ms/op        1        biz        INFO         1
     * serviceBenchmark.AsyncServiceBenchmark.serviceAsyncTest     ss    5  11.552 ± 4.439  ms/op       1        biz        INFO         10
     * serviceBenchmark.AsyncServiceBenchmark.serviceAsyncTest     ss    5  50.105 ± 14.187  ms/op      1        biz        INFO         50
     * serviceBenchmark.AsyncServiceBenchmark.serviceAsyncTest     ss    5  47.540 ± 9.186  ms/op       1        biz        OFF          50
     * serviceBenchmark.AsyncServiceBenchmark.serviceAsyncTest     ss       50.530          ms/op        1         io        INFO         50
     * serviceBenchmark.AsyncServiceBenchmark.serviceAsyncTest     ss       213.447          ms/op       1        biz        INFO         200
     * serviceBenchmark.AsyncServiceBenchmark.serviceAsyncTest     ss       184.005          ms/op       1        biz        OFF          200
     * 在异步情况下，本地测试中，当batchSize>500时，部分请求会返回超时
     * 在异步情况下，本地测试中，使用IO线程池时，当batchSize>100时，部分请求会返回超时
     */
    @Benchmark
    @Fork(1)
    @Threads(1)
    public void serviceAsyncTest(Blackhole blackhole) throws SoaException {
        blackhole.consume(serviceAsync.demoTest(demo));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ServiceBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
