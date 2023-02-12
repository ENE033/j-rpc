package RPC.server;

import RPC.core.ServiceProvider;
import RPC.core.ServiceRegistry;
import RPC.core.annotation.RPCService;
import RPC.core.annotation.ServiceScan;
import RPC.core.config.ServerRPCConfig;
import RPC.core.handler.RequestHandler;
import RPC.core.protocol.MessageCodec;
import RPC.core.util.ReflectUtil;
import RPC.core.scanner.ServiceScanner;
import cn.hutool.core.util.ClassUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;


@Slf4j
public class RPCServer extends AbstractRPCServer implements Runnable, ServiceScanner {

    private ServerRPCConfig serverRpcConfig;

    private ServiceRegistry serviceRegistry;

    private ServiceProvider serviceProvider;

//    InetSocketAddress inetSocketAddress;

//    public RPCServer(String host, Integer port, ServerRPCConfig serverRpcConfig) {
//        this(new InetSocketAddress(host, port), serverRpcConfig);
//    }

    public RPCServer(ServerRPCConfig serverRpcConfig) {
        // for test
//        if (serverRpcConfig == null) {
//            serverRpcConfig = new ServerRPCConfig();
//            serverRpcConfig.setExposedHost("localhost");
//            serverRpcConfig.setNettyPort(4556);
//        }
        this.serverRpcConfig = serverRpcConfig;
        this.serverRpcConfig.init();
        this.serviceRegistry = new ServiceRegistry(this.serverRpcConfig);
        this.serviceProvider = new ServiceProvider();
    }

    NioEventLoopGroup main = new NioEventLoopGroup();

    NioEventLoopGroup sub = new NioEventLoopGroup();

    @PostConstruct
    @Override
    public void run() {
        try {
            // 初始化服务端的配置
            init();
            // 扫描服务，将本机ip和netty服务器的端口暴露
            scanServices(serverRpcConfig.getExposedHost(), serverRpcConfig.getNettyPort());
            ChannelFuture channelFuture = new ServerBootstrap()
                    .channel(NioServerSocketChannel.class)
                    .group(main, sub)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            // 心跳机制
                            // pipeline.addLast(new IdleStateHandler(5, 5, 5, TimeUnit.MINUTES));
                            // 帧解码器
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 8, 4));
                            // 日志
                            // pipeline.addLast(new LoggingHandler());
                            // 协议
                            pipeline.addLast(new MessageCodec(serverRpcConfig));
                            // 请求消息处理器
                            pipeline.addLast(new RequestHandler(serviceProvider, serverRpcConfig));
                        }
                    })
                    .bind(new InetSocketAddress("0.0.0.0", serverRpcConfig.getNettyPort()));
            channelFuture.sync();
            Channel channel = channelFuture.channel();
            channel.closeFuture().addListener(future -> {
                main.shutdownGracefully();
                sub.shutdownGracefully();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 扫描服务
     *
     * @param exposedHost 服务暴露的公网ip
     * @param nettyPort   服务所在的端口
     */
    public void scanServices(String exposedHost, Integer nettyPort) {
        // for test
//        if (serviceRegistry == null) {
//            serviceRegistry = new ServiceRegistry("1.12.233.55:8848");
//        }

        String mainClassName = ReflectUtil.getMainClassName();
        Class<?> mainClass = null;
        try {
            mainClass = Thread.currentThread().getContextClassLoader().loadClass(mainClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assert mainClass != null;
        if (!mainClass.isAnnotationPresent(ServiceScan.class)) {
            return;
        }
        ServiceScan annotation = mainClass.getAnnotation(ServiceScan.class);
        String[] basePackages = annotation.basePackage();

        for (String basePackage : basePackages) {
            for (Class<?> clazz : ClassUtil.scanPackageByAnnotation(basePackage, RPCService.class)) {
                for (Class<?> anInterface : clazz.getInterfaces()) {
                    RPCService RPCAnnotation = clazz.getAnnotation(RPCService.class);
                    serviceProvider.addService(anInterface.getCanonicalName(), clazz, RPCAnnotation.beanName(), applicationContext);
                    serviceRegistry.registry(anInterface.getCanonicalName(), new InetSocketAddress(exposedHost, nettyPort));
                }
            }
        }
    }


}
