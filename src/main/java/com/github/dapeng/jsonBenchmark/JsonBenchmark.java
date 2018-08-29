package com.github.dapeng.jsonBenchmark;

import com.github.dapeng.core.InvocationContextImpl;
import com.github.dapeng.core.enums.CodecProtocol;
import com.github.dapeng.core.metadata.Method;
import com.github.dapeng.core.metadata.Service;
import com.github.dapeng.core.metadata.Struct;
import com.github.dapeng.json.JsonSerializer;
import com.github.dapeng.org.apache.thrift.TException;
import com.github.dapeng.util.SoaMessageBuilder;
import com.github.dapeng.util.SoaMessageParser;
import io.netty.buffer.ByteBuf;
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
import java.util.Map;
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
    }

    @State(Scope.Benchmark)
    public static class ServiceJson{
        private String crmDescriptorXmlPath;
        private Service crmService;
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
        serviceJson.crmService = getService(serviceJson.crmDescriptorXmlPath);

        serviceJson.allStruct = new StructType();
        serviceJson.allStruct.json = loadJson("/allStruct.json");
        serviceJson.allStruct.method = serviceJson.crmService.methods.stream().filter(_method -> _method.name.equals("setAllStruct")).collect(Collectors.toList()).get(0);

        serviceJson.easyStruct1 = new StructType();
        serviceJson.easyStruct1.json = loadJson("/easyStruct1.json");
        serviceJson.easyStruct1.method = serviceJson.crmService.methods.stream().filter(_method -> _method.name.equals("setEasyStruct1")).collect(Collectors.toList()).get(0);

        serviceJson.easyStruct2 = new StructType();
        serviceJson.easyStruct2.json = loadJson("/easyStruct2.json");
        serviceJson.easyStruct2.method = serviceJson.crmService.methods.stream().filter(_method -> _method.name.equals("setEasyStruct2")).collect(Collectors.toList()).get(0);

        serviceJson.easyStruct3 = new StructType();
        serviceJson.easyStruct3.json = loadJson("/easyStruct3.json");
        serviceJson.easyStruct3.method = serviceJson.crmService.methods.stream().filter(_method -> _method.name.equals("setEasyStruct3")).collect(Collectors.toList()).get(0);

        serviceJson.simpleStruct = new StructType();
        serviceJson.simpleStruct.json = loadJson("/simpleStruct.json");
        serviceJson.simpleStruct.method = serviceJson.crmService.methods.stream().filter(_method -> _method.name.equals("setSimpleStruct")).collect(Collectors.toList()).get(0);

        serviceJson.complexStruct = new StructType();
        serviceJson.complexStruct.json = loadJson("/complexStruct.json");
        serviceJson.complexStruct.method = serviceJson.crmService.methods.stream().filter(_method -> _method.name.equals("setComplexStruct")).collect(Collectors.toList()).get(0);
    }

    private Service getService(final String xmlFilePath) throws IOException {
        String xmlContent = IOUtils.toString(JsonBenchmark.class.getResource(xmlFilePath), "UTF-8");
        return JAXB.unmarshal(new StringReader(xmlContent), Service.class);
    }

    private String loadJson(final String jsonPath) throws IOException {
        return IOUtils.toString(JsonBenchmark.class.getResource(jsonPath), "UTF-8");
    }


    @Benchmark
    @Fork(1)
    public int doAllStructTest(ServiceJson serviceJson) throws TException {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.allStruct.method.name);
        invocationContext.serviceName(serviceJson.crmService.name);
        invocationContext.versionName(serviceJson.crmService.meta.version);
        invocationContext.callerMid("JsonCaller");

        ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

       JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.crmService, serviceJson.allStruct.method, "1.0.0", serviceJson.allStruct.method.request);

        SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.allStruct.json, jsonSerializer)
                .seqid(10)
                .build();
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.crmService, serviceJson.allStruct.method, "1.0.0", serviceJson.allStruct.method.request);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        requestBuf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
        return 0;
    }

    @Benchmark
    @Fork(1)
    public int doEasyStruct1Test(ServiceJson serviceJson) throws TException {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.easyStruct1.method.name);
        invocationContext.serviceName(serviceJson.crmService.name);
        invocationContext.versionName(serviceJson.crmService.meta.version);
        invocationContext.callerMid("JsonCaller");

        ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.crmService, serviceJson.easyStruct1.method, "1.0.0", serviceJson.easyStruct1.method.request);

        SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.easyStruct1.json, jsonSerializer)
                .seqid(10)
                .build();
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.crmService, serviceJson.easyStruct1.method, "1.0.0", serviceJson.easyStruct1.method.request);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        requestBuf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
        return 0;
    }

    @Benchmark
    @Fork(1)
    public int doEasyStruct2Test(ServiceJson serviceJson) throws TException {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.easyStruct2.method.name);
        invocationContext.serviceName(serviceJson.crmService.name);
        invocationContext.versionName(serviceJson.crmService.meta.version);
        invocationContext.callerMid("JsonCaller");

        ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.crmService, serviceJson.easyStruct2.method, "1.0.0", serviceJson.easyStruct2.method.request);

        SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.easyStruct2.json, jsonSerializer)
                .seqid(10)
                .build();
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.crmService, serviceJson.easyStruct2.method, "1.0.0", serviceJson.easyStruct2.method.request);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        requestBuf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
        return 0;
    }

    @Benchmark
    @Fork(1)
    public int doEasyStruct3Test(ServiceJson serviceJson) throws TException {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.easyStruct3.method.name);
        invocationContext.serviceName(serviceJson.crmService.name);
        invocationContext.versionName(serviceJson.crmService.meta.version);
        invocationContext.callerMid("JsonCaller");

        ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.crmService, serviceJson.easyStruct3.method, "1.0.0", serviceJson.easyStruct3.method.request);

        SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.easyStruct3.json, jsonSerializer)
                .seqid(10)
                .build();
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.crmService, serviceJson.easyStruct3.method, "1.0.0", serviceJson.easyStruct3.method.request);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        requestBuf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
        return 0;
    }

    @Benchmark
    @Fork(1)
    public int doSimpleStructTest(ServiceJson serviceJson) throws TException {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.simpleStruct.method.name);
        invocationContext.serviceName(serviceJson.crmService.name);
        invocationContext.versionName(serviceJson.crmService.meta.version);
        invocationContext.callerMid("JsonCaller");

        ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.crmService, serviceJson.simpleStruct.method, "1.0.0", serviceJson.simpleStruct.method.request);

        SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.simpleStruct.json, jsonSerializer)
                .seqid(10)
                .build();
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.crmService, serviceJson.simpleStruct.method, "1.0.0", serviceJson.simpleStruct.method.request);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        requestBuf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
        return 0;
    }

    @Benchmark
    @Fork(1)
    public int doComplexStructTest(ServiceJson serviceJson) throws TException {
        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.complexStruct.method.name);
        invocationContext.serviceName(serviceJson.crmService.name);
        invocationContext.versionName(serviceJson.crmService.meta.version);
        invocationContext.callerMid("JsonCaller");

        ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.crmService, serviceJson.complexStruct.method, "1.0.0", serviceJson.complexStruct.method.request);

        SoaMessageBuilder<String> builder = new SoaMessageBuilder();
        jsonSerializer.setRequestByteBuf(requestBuf);
        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.complexStruct.json, jsonSerializer)
                .seqid(10)
                .build();
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.crmService, serviceJson.complexStruct.method, "1.0.0", serviceJson.complexStruct.method.request);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        requestBuf.release();
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
