package org.ene.RPC.core.protocol;

import lombok.extern.slf4j.Slf4j;
import org.ene.RPC.core.nacos.config.NacosConfig;
import org.ene.RPC.core.serializer.SerializerStrategy;
import org.ene.RPC.core.serializer.SerializerMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@ChannelHandler.Sharable
@Slf4j
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    private final static byte[] wagz = "wagz".getBytes(StandardCharsets.UTF_8);

    private final NacosConfig nacosConfig;

    public MessageCodec(NacosConfig nacosConfig) {
        this.nacosConfig = nacosConfig;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> list) throws Exception {
        ByteBuf buffer = channelHandlerContext.alloc().buffer();
        // 魔数
        buffer.writeBytes(wagz);
        // 版本号
        buffer.writeByte(0);
        // 消息类型
        buffer.writeByte(message.getT());
        byte serializeType = nacosConfig.getConfigAsByte(NacosConfig.SERIALIZE_TYPE);
        // 序列化方式
        buffer.writeByte(serializeType);
        // 无意义
        buffer.writeByte(0);
//        // 序列号
//        buffer.writeInt(message.getSeq());
        // 序列化消息
        byte[] messageBytes = SerializerMap.get(serializeType).serializer(message);
        // 消息长度
        buffer.writeInt(messageBytes.length);
        // 消息体
        buffer.writeBytes(messageBytes);

        list.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {
        byte[] magic = new byte[4];
        buf.readBytes(magic);
        if (!Arrays.equals(magic, wagz)) {
            log.warn("魔数不同，拒收消息");
            return;
        }
        // 版本号
        buf.readByte();
        // 消息类型
        byte messageType = buf.readByte();
        // 序列化方式
        byte serializerType = buf.readByte();
        // 无意义
        buf.readByte();
//        Integer seq = buf.readInt();
        // 消息长度
        int length = buf.readInt();
        // 写入字节数组
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        // 获取反序列化器
        SerializerStrategy serializerStrategy = SerializerMap.get(serializerType);
        // 反序列化消息
        Message message = (Message) serializerStrategy.deSerializer(Message.getClassType(messageType), bytes);
        list.add(message);
    }

}
