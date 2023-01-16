package RPC.server;

import RPC.core.handler.RequestHandler;
import RPC.core.protocol.MessageCodec;
import RPC.registry.ServiceScanner;
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
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;


@Slf4j
public class RPCServer extends ServiceScanner implements Runnable {

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
                            pipeline.addLast(new LoggingHandler());
                            // 协议
                            pipeline.addLast(new MessageCodec());
                            // 请求消息处理器
                            pipeline.addLast(new RequestHandler());
                        }
                    })
                    .bind(inetSocketAddress);
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
}
