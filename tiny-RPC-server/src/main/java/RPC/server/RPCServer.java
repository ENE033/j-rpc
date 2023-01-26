package RPC.server;

import RPC.core.ServiceProvider;
import RPC.core.ServiceRegistry;
import RPC.core.annotation.Service;
import RPC.core.annotation.ServiceScan;
import RPC.core.handler.RequestHandler;
import RPC.core.protocol.MessageCodec;
import RPC.core.util.ReflectUtil;
import RPC.registry.ServiceScanner;
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

import java.net.InetSocketAddress;


@Slf4j
public class RPCServer extends AbstractRPCServer implements Runnable, ServiceScanner {

    InetSocketAddress inetSocketAddress;

    public RPCServer(String host, Integer port) {
        this(new InetSocketAddress(host, port));
    }

    public RPCServer(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
        scanServices(inetSocketAddress);
    }

    NioEventLoopGroup main = new NioEventLoopGroup();

    NioEventLoopGroup sub = new NioEventLoopGroup();


    @Override
    public void run() {
        try {
            // 初始化服务端的配置
            init();
            ChannelFuture channelFuture = new ServerBootstrap()
                    .channel(NioServerSocketChannel.class)
                    .group(main, sub)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            // 帧解码器
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 8, 4));
                            // 日志
                            // pipeline.addLast(new LoggingHandler());
                            // 协议
                            pipeline.addLast(new MessageCodec());
                            // 请求消息处理器
                            pipeline.addLast(new RequestHandler(writeBackStrategy));
                        }
                    })
                    .bind(new InetSocketAddress("0.0.0.0", inetSocketAddress.getPort()));
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

    public void scanServices(InetSocketAddress inetSocketAddress) {
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
            for (Class<?> clazz : ClassUtil.scanPackageByAnnotation(basePackage, Service.class)) {
                for (Class<?> anInterface : clazz.getInterfaces()) {
                    ServiceProvider.addService(anInterface.getCanonicalName(), clazz);
                    ServiceRegistry.registry(anInterface.getCanonicalName(), inetSocketAddress);
//                    for (Method declaredMethod : anInterface.getDeclaredMethods()) {
//                        ServiceProvider.addMethod(anInterface.getCanonicalName(), declaredMethod);
//                    }
                }
            }
        }
    }


}
