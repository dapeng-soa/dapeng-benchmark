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
import org.openjdk.jmh.annotations.*;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXB;
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
@Fork(1)
public class JsonBenchmark {

    @Benchmark
    public void tinyJsonTest() throws IOException, TException {
        final String crmDescriptorXmlPath = "/crm.xml";

        Service crmService = getService(crmDescriptorXmlPath);

        String json = loadJson("/crmService_getPatient-option.json");

        Method method = crmService.methods.stream().filter(_method -> _method.name.equals("getPatient")).collect(Collectors.toList()).get(0);

        doTest(crmService, method, method.request, json, "tinyJsonTest");

    }

    private Service getService(final String xmlFilePath) throws IOException {
        String xmlContent = IOUtils.toString(JsonBenchmark.class.getResource(xmlFilePath), "UTF-8");
        return JAXB.unmarshal(new StringReader(xmlContent), Service.class);
    }

    private String loadJson(final String jsonPath) throws IOException {
        return IOUtils.toString(JsonBenchmark.class.getResource(jsonPath), "UTF-8");
    }

    private void doTest(Service service, Method method, Struct struct, String json, String desc) throws TException {

        InvocationContextImpl invocationContext = (InvocationContextImpl) InvocationContextImpl.Factory.createNewInstance();
        invocationContext.codecProtocol(CodecProtocol.CompressedBinary);

        invocationContext.serviceName(service.name);
        invocationContext.versionName(service.meta.version);
        invocationContext.methodName(method.name);
        invocationContext.callerMid("JsonCaller");

        final ByteBuf requestBuf = PooledByteBufAllocator.DEFAULT.buffer(8192);

        JsonSerializer jsonSerializer = new JsonSerializer(service, method, "1.0.0", struct);

        SoaMessageBuilder<String> builder = new SoaMessageBuilder();

        jsonSerializer.setRequestByteBuf(requestBuf);

        ByteBuf buf = builder.buffer(requestBuf)
                .body(json, jsonSerializer)
                .seqid(10)
                .build();
//        System.out.println("origJson:\n" + json);
//
//
//        System.out.println(dumpToStr(buf));

        JsonSerializer jsonDecoder = new JsonSerializer(service, method, "1.0.0", struct);

        SoaMessageParser<String> parser = new SoaMessageParser<>(buf, jsonDecoder);
        parser.parseHeader();
//        parser.getHeader();
//        parser.parseBody().getBody();
/*        System.out.println(parser.getHeader());
        System.out.println("after enCode and decode:\n" + parser.parseBody().getBody());*/
//        System.out.println(desc + " ends=====================");
        requestBuf.release();
        InvocationContextImpl.Factory.removeCurrentInstance();
    }
}
