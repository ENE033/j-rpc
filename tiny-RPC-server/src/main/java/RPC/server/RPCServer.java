package RPC.server;

import RPC.core.ServiceController;
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
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Slf4j
public class RPCServer extends AbstractRPCServer implements Runnable, ServiceScanner {

    private final ServerRPCConfig serverRpcConfig;

    private ServiceRegistry serviceRegistry;

    private ServiceController serviceController;

    public RPCServer(ServerRPCConfig serverRpcConfig) {
        this.serverRpcConfig = serverRpcConfig;
        this.serverRpcConfig.init();
    }

    NioEventLoopGroup main = new NioEventLoopGroup();

    NioEventLoopGroup sub = new NioEventLoopGroup();

    private final EventExecutorGroup EXECUTOR_GROUP = new DefaultEventExecutorGroup(16);

    @PostConstruct
    @Override
    public void run() {
        try {
            // 初始化，用于扩展
            init();
            serviceRegistry = new ServiceRegistry(serverRpcConfig);
            serviceController = new ServiceController(applicationContext);
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
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 8, 4));
                            // 日志
                            // pipeline.addLast(new LoggingHandler());
                            // 协议
                            pipeline.addLast(new MessageCodec(serverRpcConfig));
                            // 请求消息处理器
                            pipeline.addLast(new RequestHandler(serviceController, serverRpcConfig));
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
        // 获取主启动类的类名
        String mainClassName = ReflectUtil.getMainClassName();
        // 通过反射创建
        Class<?> mainClass = null;
        try {
            mainClass = Thread.currentThread().getContextClassLoader().loadClass(mainClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assert mainClass != null;
        if (!mainClass.isAnnotationPresent(ServiceScan.class)) {
            throw new RuntimeException("主启动类中没有指定服务扫描路径");
        }

        ServiceScan annotation = mainClass.getAnnotation(ServiceScan.class);
        String[] basePackages = annotation.basePackage();
        for (String basePackage : basePackages) {
            for (Class<?> clazz : ClassUtil.scanPackageByAnnotation(basePackage, RPCService.class)) {
                for (Class<?> anInterface : clazz.getInterfaces()) {
                    RPCService RPCAnnotation = clazz.getAnnotation(RPCService.class);
                    serviceController.addService(anInterface.getCanonicalName(), anInterface, clazz, RPCAnnotation.beanName());
                    serviceRegistry.registryServiceToNacos(anInterface.getCanonicalName());
                }
            }
        }
    }


}
