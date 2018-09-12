package com.github.dapeng.serviceBenchmark;

import com.github.dapeng.core.SoaException;
import com.github.dapeng.demo.DemoServiceClient;
import com.github.dapeng.demo.domain.Demo;
import com.github.dapeng.demo.service.DemoService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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
public class ServiceBenchmark {
    private Demo demo;
    private DemoService service;

    @Setup(Level.Iteration)
    public void serviceTestInit() {
        demo = new Demo();
        demo.expectCost = 10;
        demo.bytes = 20;
        service = new DemoServiceClient();
    }

    /**
     * dapengVersion 2.0.5
     * Benchmark                                     Mode  Cnt   Score   Error  Units  Thread   ThreadPool    Log   clientCount
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  13.464 ± 0.250  ms/op    1        biz      INFO       1
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  29.179 ± 5.000  ms/op    10       biz      INFO       1
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  25.621 ± 0.592  ms/op    10       biz      OFF        1
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  137.973 ± 5.466  ms/op   50       biz      INFO       1
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  127.533 ± 1.336  ms/op   50       biz      OFF        1
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  579.824 ± 31.288  ms/op  50       io       INFO       1
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  294.024 ± 55.985  ms/op  100      biz      INFO       1
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  254.906 ± 2.261  ms/op   100      biz      OFF        1
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  13.875 ± 1.567  ms/op    1        biz      INFO       2
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  59.005 ± 12.752  ms/op   10       biz      INFO       2
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  285.409 ± 16.404  ms/op  50       biz      INFO       2
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  254.719 ± 4.239  ms/op   50       biz      OFF        2
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  512.761 ± 4.058  ms/op   100      biz      INFO       2
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  508.415 ± 3.482  ms/op   100      biz      OFF        2
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  76.844 ± 0.914  ms/op    10       biz      INFO       3
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  387.514 ± 6.536  ms/op   50       biz      INFO       3
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  381.435 ± 2.018  ms/op   50       biz      OFF        3
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  513.759 ± 9.275  ms/op   50       biz      INFO       4
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  509.454 ± 5.258  ms/op   50       biz      OFF        4
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  639.529 ± 6.081  ms/op   50       biz      INFO       5
     * serviceBenchmark.ServiceBenchmark.serviceTest  avgt    5  635.478 ± 0.500  ms/op   50       biz      OFF        5
     * 在本地测试中，使用BIZ线程池，当同时启动3个客户端，每个客户端启动100个线程时，服务请求会超时
     * 在本地测试中，使用IO线程池，启动一个客户端，并启动100个线程时，服务请求会超时
     * 在本地测试中，使用IO线程池，当同时启动2个客户端，每个客户端启动50个线程时，服务请求会超时
     * <p>
     * Benchmark                                             Mode  Cnt    Score    Error  Units   Thread   ThreadPool    Log     batchSize
     * serviceBenchmark.ServiceBenchmark.serviceTest          ss    5  13.970 ± 0.769  ms/op       1        biz        INFO         1
     * serviceBenchmark.ServiceBenchmark.serviceTest          ss    5  143.339 ± 16.504  ms/op     1        biz        INFO         10
     * serviceBenchmark.ServiceBenchmark.serviceTest          ss    5  717.774 ± 80.252  ms/op     1        biz        INFO         50
     * serviceBenchmark.ServiceBenchmark.serviceTest          ss    5  5800.491 ± 494.532  ms/op   1        biz        INFO         200
     * serviceBenchmark.ServiceBenchmark.serviceTest          ss       14458.863          ms/op     1        biz        INFO         500
     */
    @Benchmark
    @Fork(1)
    //@Threads(50)
    @Group("serviceTest")
    @GroupThreads(100)
    public void serviceTest(Blackhole blackhole) throws SoaException {
        blackhole.consume(service.demoTest(demo));
    }


    @Benchmark
    @Fork(1)
    @Group("serviceTest")
    @GroupThreads(100)
    public String serviceTest0() throws SoaException {
        return service.demoTest(demo);
    }

    @Benchmark
    @Fork(1)
    @Group("serviceTest")
    @GroupThreads(100)
    public String serviceTest1() throws SoaException {
        return service.demoTest(demo);
    }
/*
    @Benchmark
    @Fork(1)
    @Group("serviceTest")
    @GroupThreads(100)
    public String serviceTest2() throws SoaException {
        return service.demoTest(demo);
    }

    @Benchmark
    @Fork(1)
    @Group("serviceTest")
    @GroupThreads(50)
    public String serviceTest3() throws SoaException {
        return service.demoTest(demo);
    }*/

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ServiceBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
