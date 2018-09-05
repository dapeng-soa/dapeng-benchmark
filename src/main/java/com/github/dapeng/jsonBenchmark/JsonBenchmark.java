package com.github.dapeng.jsonBenchmark;

import com.github.dapeng.core.BeanSerializer;
import com.github.dapeng.core.InvocationContextImpl;
import com.github.dapeng.core.SoaException;
import com.github.dapeng.core.SoaHeader;
import com.github.dapeng.core.enums.CodecProtocol;
import com.github.dapeng.core.helper.SoaHeaderHelper;
import com.github.dapeng.core.helper.SoaSystemEnvProperties;
import com.github.dapeng.core.metadata.Method;
import com.github.dapeng.core.metadata.Service;
import com.github.dapeng.core.metadata.Struct;
import com.github.dapeng.json.JsonSerializer;
import com.github.dapeng.json.OptimizedMetadata;
import com.github.dapeng.org.apache.thrift.TException;
import com.github.dapeng.util.SoaMessageBuilder;
import com.github.dapeng.util.SoaMessageParser;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.openjdk.jmh.annotations.*;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.scheduling.annotation.Async;

import javax.xml.bind.JAXB;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.io.StringReader;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hui
 * @date 2018/8/23 0023 9:06
 */
@BenchmarkMode({Mode.AverageTime,Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Thread)
public class JsonBenchmark {

    public class StructType{
        private String  json;
        private Method  method;
        private OptimizedMetadata.OptimizedStruct optimizedStruct;
    }

    @State(Scope.Benchmark)
    public static class ServiceJson{
        private String crmDescriptorXmlPath;
        private OptimizedMetadata.OptimizedService optimizedService;
        private StructType allStruct;
        private StructType easyStruct1;
        private StructType easyStruct2;
        private StructType easyStruct3;
        private StructType simpleStruct;
        private StructType complexStruct;
    }


    @Setup
    public void jsonTestInit(ServiceJson serviceJson) throws IOException, TException {
        serviceJson.crmDescriptorXmlPath = "/benchmarkDemoService.xml";
        Service service = getService(serviceJson.crmDescriptorXmlPath);
        serviceJson.optimizedService = new OptimizedMetadata.OptimizedService(service);

        serviceJson.allStruct = new StructType();
        serviceJson.allStruct.json = loadJson("/allStruct.json");
        serviceJson.allStruct.method = service.methods.stream().filter(_method -> _method.name.equals("setAllStruct")).collect(Collectors.toList()).get(0);
        serviceJson.allStruct.optimizedStruct = new OptimizedMetadata.OptimizedStruct(serviceJson.allStruct.method.request);

        serviceJson.easyStruct1 = new StructType();
        serviceJson.easyStruct1.json = loadJson("/easyStruct1.json");
        serviceJson.easyStruct1.method = service.methods.stream().filter(_method -> _method.name.equals("setEasyStruct1")).collect(Collectors.toList()).get(0);
        serviceJson.easyStruct1.optimizedStruct = new OptimizedMetadata.OptimizedStruct(serviceJson.easyStruct1.method.request);

        serviceJson.easyStruct2 = new StructType();
        serviceJson.easyStruct2.json = loadJson("/easyStruct2.json");
        serviceJson.easyStruct2.method = service.methods.stream().filter(_method -> _method.name.equals("setEasyStruct2")).collect(Collectors.toList()).get(0);
        serviceJson.easyStruct2.optimizedStruct = new OptimizedMetadata.OptimizedStruct(serviceJson.easyStruct2.method.request);

        serviceJson.easyStruct3 = new StructType();
        serviceJson.easyStruct3.json = loadJson("/easyStruct3.json");
        serviceJson.easyStruct3.method = service.methods.stream().filter(_method -> _method.name.equals("setEasyStruct3")).collect(Collectors.toList()).get(0);
        serviceJson.easyStruct3.optimizedStruct = new OptimizedMetadata.OptimizedStruct(serviceJson.easyStruct3.method.request);

        serviceJson.simpleStruct = new StructType();
        serviceJson.simpleStruct.json = loadJson("/simpleStruct.json");
        serviceJson.simpleStruct.method = service.methods.stream().filter(_method -> _method.name.equals("setSimpleStruct")).collect(Collectors.toList()).get(0);
        serviceJson.simpleStruct.optimizedStruct = new OptimizedMetadata.OptimizedStruct(serviceJson.simpleStruct.method.request);

        serviceJson.complexStruct = new StructType();
        serviceJson.complexStruct.json = loadJson("/complexStruct.json");
        serviceJson.complexStruct.method = service.methods.stream().filter(_method -> _method.name.equals("setComplexStruct")).collect(Collectors.toList()).get(0);
        serviceJson.complexStruct.optimizedStruct = new OptimizedMetadata.OptimizedStruct(serviceJson.complexStruct.method.request);

    }

    private Service getService(final String xmlFilePath) throws IOException {
        String xmlContent = IOUtils.toString(JsonBenchmark.class.getResource(xmlFilePath), "UTF-8");
        return JAXB.unmarshal(new StringReader(xmlContent), Service.class);
    }

    private String loadJson(final String jsonPath) throws IOException {
        return IOUtils.toString(JsonBenchmark.class.getResource(jsonPath), "UTF-8");
    }

    private static <REQ> ByteBuf buildRequestBuf(String service, String version, String method, int seqid, REQ request, BeanSerializer<REQ> requestSerializer) throws SoaException {
/*        AbstractByteBufAllocator allocator =
                SoaSystemEnvProperties.SOA_POOLED_BYTEBUF ?
                        PooledByteBufAllocator.DEFAULT : UnpooledByteBufAllocator.DEFAULT;*/
        AbstractByteBufAllocator allocator =UnpooledByteBufAllocator.DEFAULT;
        final ByteBuf requestBuf = allocator.buffer(8192);

        SoaMessageBuilder<REQ> builder = new SoaMessageBuilder<>();

        try {
            SoaHeader header = SoaHeaderHelper.buildHeader(service, version, method);

            ByteBuf buf = builder.buffer(requestBuf)
                    .header(header)
                    .body(request, requestSerializer)
                    .seqid(seqid)
                    .build();
            return buf;
        } catch (TException e) {
            e.printStackTrace();
        }

        return null;
    }

     /**
      * dapengVersion 2.0.5
      *
      * Benchmark                                         Mode  Cnt  Score    Error   Units
      * jsonBenchmark.JsonBenchmark.doAllStructTest      thrpt    5  6.146 ± 12.685  ops/ms
      * jsonBenchmark.JsonBenchmark.doAllStructTest       avgt    5  0.126 ±  0.039   ms/op
      *
      */
    @Benchmark
    @Fork(1)
    public int doAllStructTest(ServiceJson serviceJson) throws TException {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.allStruct.method.name);
        invocationContext.serviceName(serviceJson.optimizedService.getService().name);
        invocationContext.versionName(serviceJson.optimizedService.getService().meta.version);
        invocationContext.callerMid("JsonCaller");

       // ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.optimizedService, serviceJson.allStruct.method, "1.0.0", serviceJson.allStruct.optimizedStruct);

        ByteBuf buf = buildRequestBuf(serviceJson.optimizedService.getService().name, "1.0.0", serviceJson.allStruct.method.name, 10, serviceJson.allStruct.json, jsonSerializer);

/*
        SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.allStruct.json, jsonSerializer)
                .seqid(10)
                .build();*/
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.optimizedService, serviceJson.allStruct.method, "1.0.0", serviceJson.allStruct.optimizedStruct);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        buf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
        return 0;
    }

    /**
     * dapengVersion 2.0.5
     *
     * Benchmark                                         Mode  Cnt  Score    Error   Units
     * jsonBenchmark.JsonBenchmark.doEasyStruct1Test    thrpt    5  9.234 ±  0.511  ops/ms
     * jsonBenchmark.JsonBenchmark.doEasyStruct1Test     avgt    5  0.094 ±  0.018   ms/op
     *
     */
    @Benchmark
    @Fork(1)
    public int doEasyStruct1Test(ServiceJson serviceJson) throws TException {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.easyStruct1.method.name);
        invocationContext.serviceName(serviceJson.optimizedService.getService().name);
        invocationContext.versionName(serviceJson.optimizedService.getService().meta.version);
        invocationContext.callerMid("JsonCaller");

       // ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.optimizedService, serviceJson.easyStruct1.method, "1.0.0", serviceJson.easyStruct1.optimizedStruct);

        ByteBuf buf = buildRequestBuf(serviceJson.optimizedService.getService().name, "1.0.0", serviceJson.easyStruct1.method.name, 10, serviceJson.easyStruct1.json, jsonSerializer);

/*
        SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.easyStruct1.json, jsonSerializer)
                .seqid(10)
                .build();*/
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.optimizedService, serviceJson.easyStruct1.method, "1.0.0", serviceJson.easyStruct1.optimizedStruct);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        buf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
        return 0;
    }

    /**
     * dapengVersion 2.0.5
     *
     * Benchmark                                         Mode  Cnt  Score    Error   Units
     * jsonBenchmark.JsonBenchmark.doEasyStruct2Test    thrpt    5  9.399 ±  0.921  ops/ms
     * jsonBenchmark.JsonBenchmark.doEasyStruct2Test     avgt    5  0.090 ±  0.007   ms/op
     *
     */
    @Benchmark
    @Fork(1)
    public int doEasyStruct2Test(ServiceJson serviceJson) throws TException {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.easyStruct2.method.name);
        invocationContext.serviceName(serviceJson.optimizedService.getService().name);
        invocationContext.versionName(serviceJson.optimizedService.getService().meta.version);
        invocationContext.callerMid("JsonCaller");

      //  ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.optimizedService, serviceJson.easyStruct2.method, "1.0.0", serviceJson.easyStruct2.optimizedStruct);

        ByteBuf buf = buildRequestBuf(serviceJson.optimizedService.getService().name, "1.0.0", serviceJson.easyStruct2.method.name, 10, serviceJson.easyStruct2.json, jsonSerializer);


/*        SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.easyStruct2.json, jsonSerializer)
                .seqid(10)
                .build();*/
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.optimizedService, serviceJson.easyStruct2.method, "1.0.0", serviceJson.easyStruct2.optimizedStruct);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        buf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
        return 0;
    }

    /**
     * dapengVersion 2.0.5
     *
     * Benchmark                                         Mode  Cnt  Score    Error   Units
     * jsonBenchmark.JsonBenchmark.doEasyStruct3Test    thrpt    5  8.937 ±  0.766  ops/ms
     * jsonBenchmark.JsonBenchmark.doEasyStruct3Test     avgt    5  0.092 ±  0.004   ms/op
     *
     */
    @Benchmark
    @Fork(1)
    public int doEasyStruct3Test(ServiceJson serviceJson) throws TException  {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.easyStruct3.method.name);
        invocationContext.serviceName(serviceJson.optimizedService.getService().name);
        invocationContext.versionName(serviceJson.optimizedService.getService().meta.version);
        invocationContext.callerMid("JsonCaller");

       // ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.optimizedService, serviceJson.easyStruct3.method, "1.0.0", serviceJson.easyStruct3.optimizedStruct);

        ByteBuf buf = buildRequestBuf(serviceJson.optimizedService.getService().name, "1.0.0", serviceJson.easyStruct3.method.name, 10, serviceJson.easyStruct3.json, jsonSerializer);


  /*      SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.easyStruct3.json, jsonSerializer)
                .seqid(10)
                .build();*/
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.optimizedService, serviceJson.easyStruct3.method, "1.0.0", serviceJson.easyStruct3.optimizedStruct);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        buf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
        return 0;
    }

    /**
     * dapengVersion 2.0.5
     *
     * Benchmark                                         Mode  Cnt  Score    Error   Units
     * jsonBenchmark.JsonBenchmark.doSimpleStructTest   thrpt    5  7.517 ±  1.232  ops/ms
     * jsonBenchmark.JsonBenchmark.doSimpleStructTest    avgt    5  0.113 ±  0.018   ms/op
     *
     */
    @Benchmark
    @Fork(1)
    public int doSimpleStructTest(ServiceJson serviceJson) throws TException {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.simpleStruct.method.name);
        invocationContext.serviceName(serviceJson.optimizedService.getService().name);
        invocationContext.versionName(serviceJson.optimizedService.getService().meta.version);
        invocationContext.callerMid("JsonCaller");

        //ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.optimizedService, serviceJson.simpleStruct.method, "1.0.0", serviceJson.simpleStruct.optimizedStruct);

        ByteBuf buf = buildRequestBuf(serviceJson.optimizedService.getService().name, "1.0.0", serviceJson.simpleStruct.method.name, 10, serviceJson.simpleStruct.json, jsonSerializer);


/*        SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.simpleStruct.json, jsonSerializer)
                .seqid(10)
                .build();*/
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.optimizedService, serviceJson.simpleStruct.method, "1.0.0", serviceJson.simpleStruct.optimizedStruct);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        buf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
        return 0;
    }

    /**
     * dapengVersion 2.0.5
     *
     * Benchmark                                         Mode  Cnt  Score    Error   Units
     * jsonBenchmark.JsonBenchmark.doComplexStructTest  thrpt    5  2.611 ±  0.346  ops/ms
     * jsonBenchmark.JsonBenchmark.doComplexStructTest   avgt    5  0.430 ±  0.266   ms/op
     */
    @Benchmark
    @Fork(1)
    public int doComplexStructTest(ServiceJson serviceJson) throws TException {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.complexStruct.method.name);
        invocationContext.serviceName(serviceJson.optimizedService.getService().name);
        invocationContext.versionName(serviceJson.optimizedService.getService().meta.version);
        invocationContext.callerMid("JsonCaller");

       // ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.optimizedService, serviceJson.complexStruct.method, "1.0.0", serviceJson.complexStruct.optimizedStruct);

        ByteBuf buf = buildRequestBuf(serviceJson.optimizedService.getService().name, "1.0.0", serviceJson.complexStruct.method.name, 10, serviceJson.complexStruct.json, jsonSerializer);

/*        SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        SoaHeader header = SoaHeaderHelper.buildHeader(serviceJson.optimizedService.getService().name, "1.0.0", serviceJson.complexStruct.method.name);
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .header(header)
                .body(serviceJson.complexStruct.json, jsonSerializer)
                .seqid(10)
                .build();*/
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.optimizedService, serviceJson.complexStruct.method, "1.0.0", serviceJson.complexStruct.optimizedStruct);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        buf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
        return 0;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JsonBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
