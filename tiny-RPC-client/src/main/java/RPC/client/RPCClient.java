package RPC.client;

import RPC.core.config.ClientRPCConfig;
import RPC.core.handler.ResponseHandler;
import RPC.core.protocol.MessageCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RPCClient {
    //
    public static final Map<InetSocketAddress, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private final Object lock = new Object();

    private final ClientRPCConfig clientRPCConfig;

    public RPCClient(ClientRPCConfig clientRPCConfig) {
        this.clientRPCConfig = clientRPCConfig;
    }

    /**
     * 获取与服务端的连接
     *
     * @param inetSocketAddress
     * @return
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel;
        // 如果有就从缓存中取
        // 使用双检锁保证只创建一次连接
        if ((channel = CHANNEL_MAP.get(inetSocketAddress)) == null) {
            synchronized (lock) {
                if ((channel = CHANNEL_MAP.get(inetSocketAddress)) == null) {
                    // 没有就创建
                    CHANNEL_MAP.put(inetSocketAddress, create(inetSocketAddress));
                    return CHANNEL_MAP.get(inetSocketAddress);
                }
            }
        }
        return channel;
    }

    /**
     * 创建一个连接
     *
     * @param inetSocketAddress
     * @return
     */
    public Channel create(InetSocketAddress inetSocketAddress) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .group(eventLoopGroup)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
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
                            pipeline.addLast(new MessageCodec(clientRPCConfig));
                            // 响应消息处理器
                            pipeline.addLast(new ResponseHandler());
                        }
                    })
                    .connect(inetSocketAddress);
            channelFuture.sync();
            Channel channel = channelFuture.channel();

            channel.closeFuture().addListener(future -> {
                eventLoopGroup.shutdownGracefully();
                CHANNEL_MAP.remove(inetSocketAddress);
            });
            return channel;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("发生了错误");
    }
}
