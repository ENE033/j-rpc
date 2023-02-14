package RPC.client;

import RPC.core.config.ClientRPCConfig;
import RPC.proxy.RPCClientProxyFactory;
import RPC.service.TestService;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class JmeterTest implements JavaSamplerClient {
    TestService testService;

    @Override
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        ClientRPCConfig clientRPCConfig = new ClientRPCConfig();
        clientRPCConfig.setNacosConfigAddress("1.12.233.55:8848");
        clientRPCConfig.setNacosRegistryAddress("1.12.233.55:8848");
        clientRPCConfig.setNacosConfigGroup("DEFAULT_GROUP");
        clientRPCConfig.setNacosConfigDataId("rpc.properties");
        RPCClientProxyFactory rpcClientProxyFactory = new RPCClientProxyFactory(clientRPCConfig);
        rpcClientProxyFactory.getProxy(TestService.class);

//        System.out.println("测试准备开始");
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult sampleResult = new SampleResult();

        sampleResult.sampleStart();
        try {
            String result = testService.getAnswer("测试TPS");
//            Integer result = testService.add();
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
//        System.out.println("测试结束");
    }

    @Override
    public Arguments getDefaultParameters() {
        return new Arguments();
    }
}
