package org.ene.RPC.client.instant;

import org.ene.RPC.core.config.ClientRPCConfig;
import org.ene.RPC.client.proxy.RPCClientProxyFactory;
import org.ene.RPC.service.TestService;
import org.ene.RPC.service.TestService1;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class JmeterTest1 implements JavaSamplerClient {
    TestService testService;
    TestService1 testService1;
    static ClientRPCConfig clientRPCConfig;
    static RPCClientProxyFactory rpcClientProxyFactory;

    static {
        clientRPCConfig = new ClientRPCConfig();
        clientRPCConfig.setNacosConfigAddress("139.159.207.128:8848");
        clientRPCConfig.setNacosRegistryAddress("139.159.207.128:8848");
        clientRPCConfig.setNacosConfigGroup("DEFAULT_GROUP");
        clientRPCConfig.setNacosConfigDataId("rpc.properties");
        rpcClientProxyFactory = new RPCClientProxyFactory(clientRPCConfig);
    }


    @Override
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        testService = rpcClientProxyFactory.getProxy(TestService.class);
        testService1 = rpcClientProxyFactory.getProxy(TestService1.class);
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult sampleResult = new SampleResult();

        sampleResult.sampleStart();
        try {
//            String r = testService1.timeTest1(new Date(), LocalDateTime.now());
//            String r = testService.IOTask();
//            String r = testService.CPUTask();

            String result = testService.getAnswer("测试TPS");
//            Integer r = testService.add();
//            boolean r = testService.decCount();
//            sampleResult.setResponseData(String.valueOf(r), "utf-8");
            sampleResult.setResponseData(result, "utf-8");
            sampleResult.setDataType(SampleResult.TEXT);
            sampleResult.setSuccessful(true);
        } catch (RuntimeException e) {
            sampleResult.setSuccessful(false);
        } finally {
            sampleResult.sampleEnd();
        }

        return sampleResult;
    }

    @Override
    public void teardownTest(JavaSamplerContext javaSamplerContext) {
    }

    @Override
    public Arguments getDefaultParameters() {
        return new Arguments();
    }
}
