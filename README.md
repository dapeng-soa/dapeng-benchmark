# dapeng-benchmark

dapeng-benchmark基于JMH测试工具对dapeng-soa的单元功能进行测试。目前，该项目中已经添加了对dapoeng的限流功能、
对json字符串的序列化与反序列化的功能和对dapeng容器服务调用的测试。

### 对限流功能的测试

分别针对callId与callIp的限流规则对限流功能的性能进行了的测试，分别从平均时间和吞吐量两个方面对其性能进行了度量。

### 对JSON序列化和反序列化功能的测试

针对json的序列化与反序列化，准备了一个benchmarkDemo项目，在项目中，定义了若干个结构体，所有的结构体分为不同的复杂程度，
其中包括，一个包含dapeng所有能支持的数据类型的结构体， 若干个包括简单结构， 中等复杂结构（例如10个左右字段， 其中2-3个是结构体， 
1个嵌套2层的结构， 还有数组， ）， 复杂结构（例如50个字段， 其中10个嵌套结构体， 5个3层以上的嵌套结构体， 还有数组， map， set），
 针对这些结构体分别给不同的接口方法，用于json序列化与反序列化功能的测试。
 
 分别从平均时间和吞吐量两个方面对其性能进行了度量。
 
 
 ### 对dapeng容器服务调用性能的测试
 
在该项测试中使用了基于dapeng的demoService服务用于对dapeng容器服务调用性能的测试。

在该项测试中所有的性能数据都是基于本地机器测试得来。

该项测试中采用的客户端进行了测试，分别从同步客户端和异步客户端来进行了测试。

在同步客户端测试中，分别针对不同的线程属性和客户端数量进行的测试。
在异步客户端测试中，针对一次的请求数量进行了测试。
同时两者都考虑了服务调用走BIZ线程池和走IO线程池、是否开启服务日志的性能区别。

