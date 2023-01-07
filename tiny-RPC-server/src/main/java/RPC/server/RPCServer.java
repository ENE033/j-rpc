package RPC.server;

import RPC.core.protocol.MessageCodec;
import RPC.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;


public class RPCServer extends ServiceRegistry implements Runnable {

    public RPCServer() {
        scanServices();
    }

    NioEventLoopGroup main = new NioEventLoopGroup();

    NioEventLoopGroup sub = new NioEventLoopGroup();

    @Override
    public void run() {
        new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(main, sub)
                .childHandler(new ChannelInitializer<NioServerSocketChannel>() {
                    @Override
                    protected void initChannel(NioServerSocketChannel nioServerSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioServerSocketChannel.pipeline();
                        // 帧解码器
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 8, 4));
                        // 协议
                        pipeline.addLast(new MessageCodec());
                        // 日志
                        pipeline.addLast(new LoggingHandler());

                    }
                });

    }
}
