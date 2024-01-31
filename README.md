JRPC是一个轻量级的基于Netty实现的微服务治理框架，在实现远程调用的基础之上，加入了服务发现，配置管理，负载均衡，熔断降级，流量限制等服务治理功能；JRPC框架提供了SpringBoot-Starter，整合了SpringBoot框架，并提供了相应的注解进行服务注册、服务引用、定义相关配置等；使用了策略模式、责任链模式等设计模式使代码具有强扩展性



## 快速开始

基本需要：jdk8，naocs



### 配置管理

#### 序列化

0-jdk

1-json

2-hessian

3-kryo

#### 负载均衡

0-随机权重算法

1-一致性哈希算法

#### 服务端执行策略

0-单线程执行

1-多线程执行

#### 代理创建模式

0-JDK

1-CGLIB

![](https://cdn.jsdelivr.net/gh/ENE033/pic/1704333018257.png)	



### 启动服务端

引入starter依赖

```xml
<dependency>
    <groupId>org.ene</groupId>
    <artifactId>J-RPC-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```



application.yml配置

```yml
jrpc:
  server:
    host: localhost # rpc服务端的地址
    nacos:
      configuration:
        address: xx.xx.xx.xx:8848 # 配置中心的地址
        dataId: rpc.properties # 配置文件的dataId
        group: DEFAULT_GROUP # 配置文件的group
      registry:
        address: xx.xx.xx.xx:8848 # 注册中心的地址
    port: 4668 # rpc服务端的端口号
```



提供接口

```java
public interface TestApi {
    String getResult(int num);
}
```



提供实现类，并加上@JRPCService注解

```java
@JRPCService
@Component
public class TestApiImpl implements TestApi {
    @Override
    public String getResult(int num) {
        return String.valueOf(num);
    }
}
```



启动SpringBoot项目，JRPC的服务端会随即启动

![](https://cdn.jsdelivr.net/gh/ENE033/pic/20240104101106.png)	



### 启动客户端

引入starter依赖

```xml
<dependency>
    <groupId>org.ene</groupId>
    <artifactId>J-RPC-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```



application.yml配置

```yml
jrpc:
  client:
    nacos:
      configuration:
        address: xx.xx.xx.xx:8848 # 配置中心的地址
        dataId: rpc.properties # 配置文件的dataId
        group: DEFAULT_GROUP # 配置文件的group
      registry:
        address: xx.xx.xx.xx:8848 # 注册中心的地址
```



编写service，加上@JRPCCaller进行远程服务引用注入

```java
@Service
public class TestServiceImpl {

    @JRPCCaller
    TestApi testApi;

    public void call() {
        String result = testApi.getResult(23);
        System.out.println(result);
    }

}
```



简单发起远程调用

```java
@SpringBootApplication
public class RpcClientDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(RpcClientDemoApplication.class, args);

        TestServiceImpl bean = run.getBean(TestServiceImpl.class);
        bean.call();
    }

}
```



执行结果

![](https://cdn.jsdelivr.net/gh/ENE033/pic/20240104102055.png)

![](https://cdn.jsdelivr.net/gh/ENE033/pic/20240104102144.png)	





## 架构

流程图

![](https://cdn.jsdelivr.net/gh/ENE033/pic/JRPC%E6%9E%B6%E6%9E%84%E5%9B%BE.png)



逻辑分层图

![](https://cdn.jsdelivr.net/gh/ENE033/pic/JRPC%E5%88%86%E5%B1%82%E5%9B%BE.png)	



## 具体实现

### 协议

![](https://cdn.jsdelivr.net/gh/ENE033/pic/JRPC%E5%8D%8F%E8%AE%AE.png)	

协议头部非常轻量，仅保留能够正常收发报文的能力

远程调用所需要的信息被放到了消息体中，扩展起来更加灵活



请求类型的消息具有如下字段：

- 序列号
- 调用的服务接口的全限定名
- 调用的方法名
- 参数类型数组
- 参数数组

响应类型的消息具有如下字段：

- 序列号
- 响应状态
- 响应结果





### 序列化

提供了四种序列化方式，可以在nacos中进行配置：

- JDK
- Json
- Hessian
- Kryo



序列化方式的衡量标准：

1. 通用性：通用性是指序列化框架是否支持跨语言、跨平台。
2. 易用性：易用性是指序列化框架是否便于使用、调试，会影响开发效率。
3. 可扩展性：随着业务的发展，传输实体可能会发生变化，但是旧实体有可能还会被使用。这时候就需要考虑所选择的序列化框架是否具有良好的扩展性。
4. 性能：序列化性能主要包括时间开销和空间开销。序列化的数据通常用于持久化或网络传输，所以其大小是一个重要的指标。而编解码时间同样是影响序列化协议选择的重要指标，因为如今的系统都在追求高性能。
5. Java数据类型和语法支持：不同序列化框架所能够支持的数据类型以及语法结构是不同的。这里我们要对Java的数据类型和语法特性进行测试，来看看不同序列化框架对Java数据类型和语法结构的支持度。



横向对比：

JDK：Java原生的序列化方式，序列化后的报文体积过大，序列化和反序列化性能也比较差

Json：可跨语言，协议简单易懂，序列化之后报文体积以及序列化和反序列化性能中等，对于Java部分的数据类型不支持，比如Date，LocalDateTime

Hessian：序列化后的报文体积小，序列化和反序列化的性能优，也需要实现Serializable接口，但是不受到serialVersionUID影响，对Java的数据类型支持不错

Kryo：依赖底层ASM库来生成字节码，序列化后的报文体积小，序列化和反序列化的性能优，简单易用，可以使用ThreadLocal或者Kryo对象池进行优化





### 报文压缩

在传输RPC的请求消息时，往往需要传输参数的类型数组

而基本数据类型及其包装类，以及它们的数组类型，这些都是非常常用的数据类型

于是可以使用Guava中的双向Map来存储常用类对象，并对其进行编码，减少常用类对象传输的体积占用



![](https://cdn.jsdelivr.net/gh/ENE033/pic/JRPC%E9%9D%99%E6%80%81%E5%AD%97%E5%85%B8.png)	



### 多级缓存

将RPC调用的Class对象和Method对象用Map缓存起来，避免多次反射带来性能损耗



### 服务扫描与注册

提供`@ServiceScan`注解，通过指定`basePackages`来指定RPC服务扫描的包路径

在RPC的服务端启动的时候，会进行服务扫描

将指定路径的包下的所有带有`@JRPCService`的类扫描并记录到map，并把类实现的接口的全限定名以及服务端的ip地址和端口注册到nacos中，作为服务暴露

如果没有IOC容器，会将bean注册到ConcurrentHashMap中，如果有IOC容器，那么关联IOC容器中的Bean作为RPC执行的对象



### 服务发现

使用namingService的getAllInstances方法从Nacos中拉取该接口的全限定名下的所有可用节点

在拉取下服务列表之后，会把拉取到的服务信息存放在ServiceInfoHolder中的serviceInfoMap里面，当服务节点发送变化时，会第一时间更新ServiceInfoHolder中的信息，而不需要每次都去nacos中拉取最新的服务列表，所以在nacos宕机之后，RPC框架仍可以提供服务，但是无法再加入新的节点，以及感知到服务节点的变化



### 服务调用

服务的调用通过动态代理来完成

每一个RPCCaller都是一个代理对象，代理模式为jdk动态代理。将调用的方法，参数传入发送责任链，后续逻辑在发送责任链中进行



### 负载均衡

框架提供了两种负载均衡策略：

- 随机权重算法
- 一致性哈希算法

在拉取了服务节点列表之后，会根据nacos的配置，结合策略模式选取负载均衡选择器来进行负载均衡

基本实现原理：

随机权重算法：先从注册中心获取节点列表，然后将所有节点的权重全部相加起来，相加的过程中判断是否所有节点的权重都一样，如果一样那么随机返回一个节点，如果不是全部一样，那么从0~所有节点的总权值中随机取一个数，然后用这个数以此递减每个节点的权值，直到减到某个节点的权值小于0时，就调用这个节点

一致性哈希算法：先从注册中心获取服务节点列表，给每个节点建立虚拟节点，参考nginx的做法，一个权重为1的节点需要分配160个虚拟节点，那么在生成虚拟节点的时候，可以给节点的地址加盐进行计算md5值，md5值是有32位16进制数，1个16进制是半个字节，32个16进制数就是16字节，这16字节就可以分割成4个4字节的int整数，使用这4个int就可以为一个节点建立四个虚拟节点，对于权重为1的节点，这样计算40次，就可以获得160个虚拟节点，把这些虚拟节点放到一个TreeMap中；在选择节点进行服务的时候，我们可以通过这次rpc请求的方法签名和参数来进行hash，然后通过TreeMap的ceilingEntry找到第一个比该hash值大的节点，如果没有找到，那么返回TreeMap中的第一个节点，以此模拟环的效果



### 熔断

![](https://cdn.jsdelivr.net/gh/ENE033/pic/JRPC%E7%86%94%E6%96%AD.png)	



![](https://cdn.jsdelivr.net/gh/ENE033/pic/20240111171620.png)	



### 流量控制

#### 固定窗口

![](https://cdn.jsdelivr.net/gh/ENE033/pic/JRPC%E6%B5%81%E9%87%8F%E6%8E%A7%E5%88%B6.png)	



#### 滑动窗口

![](https://cdn.jsdelivr.net/gh/ENE033/pic/%E6%BB%91%E5%8A%A8%E7%AA%97%E5%8F%A3%E9%99%90%E6%B5%81%E7%AE%97%E6%B3%95.png)	
