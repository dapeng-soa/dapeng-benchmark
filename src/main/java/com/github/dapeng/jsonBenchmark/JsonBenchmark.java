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
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.openjdk.jmh.annotations.*;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

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
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Thread)
public class JsonBenchmark {
    @State(Scope.Benchmark)
    public static class ServiceJson{
        private String crmDescriptorXmlPath;
        private Service crmService;
        private String json;
        private Method method;
        private String desc;
    }
    @Setup
    public void tinyJsonTest(ServiceJson serviceJson) throws IOException, TException {
        serviceJson.crmDescriptorXmlPath = "/crm.xml";

        serviceJson.crmService = getService(serviceJson.crmDescriptorXmlPath);

        serviceJson.json = loadJson("/crmService_getPatient-option.json");

        serviceJson.method = serviceJson.crmService.methods.stream().filter(_method -> _method.name.equals("getPatient")).collect(Collectors.toList()).get(0);


        //doTest(crmService, method, method.request, json, "tinyJsonTest");

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
    public int doTest(ServiceJson serviceJson) throws TException {


        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.methodName(serviceJson.method.name);
        invocationContext.serviceName(serviceJson.crmService.name);
        invocationContext.versionName(serviceJson.crmService.meta.version);
        invocationContext.methodName(serviceJson.method.name);
        invocationContext.callerMid("JsonCaller");

        ByteBuf requestBuf = UnpooledByteBufAllocator.DEFAULT.buffer(8192);

       JsonSerializer jsonSerializer = new JsonSerializer(serviceJson.crmService, serviceJson.method, "1.0.0", serviceJson.method.request);

        SoaMessageBuilder<String> builder = new SoaMessageBuilder();



        jsonSerializer.setRequestByteBuf(requestBuf);


        ByteBuf buf = builder.buffer(requestBuf)
                .body(serviceJson.json, jsonSerializer)
                .seqid(10)
                .build();
        JsonSerializer jsonDecoder = new JsonSerializer(serviceJson.crmService, serviceJson.method, "1.0.0", serviceJson.method.request);
        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
        requestBuf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();

        return 0;
    }

}
