namespace java com.github.dapeng.benchmark.service

include "allStruct.thrift"
include "easyStruct1.thrift"
include "easyStruct2.thrift"
include "easyStruct3.thrift"
include "simpleStruct.thrift"
include "complexStruct.thrift"

service benchmarkDemoService {

    void setAllStruct(allStruct.allStruct request)

    void setEasyStruct1(easyStruct1.easyStruct1 request)

    void setEasyStruct2(easyStruct2.easyStruct2 request)

    void setEasyStruct3(easyStruct3.easyStruct3 request)

    void setSimpleStruct(simpleStruct.simpleStruct request)

    void setComplexStruct(complexStruct.complexStruct request)

}