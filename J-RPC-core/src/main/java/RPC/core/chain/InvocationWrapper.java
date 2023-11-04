package RPC.core.chain;

import RPC.core.protocol.ResponseMessage;
import RPC.core.protocol.ResponseStatus;
import RPC.core.execute.ExecuteStrategy;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Data
@Slf4j
@ToString
public class InvocationWrapper {

    // 从request中获取到的原始信息
    Invocation inv;
    // 超时时间
    int timeOut;
    // 实体
    Object obj;
    // 接口类信息
    Class<?> clazz;
    // 接口方法
    Method method;
    // 执行策略
    ExecuteStrategy executeStrategy;
    // channel上下文
    ChannelHandlerContext channelHandlerContext;

    public void execute() {
        Method finalMethod = method;
        Integer finalSeq = inv.getSeq();
        Object[] args = inv.getArgs();
        executeStrategy.writeBack(() -> {
            ResponseMessage responseMessage = new ResponseMessage();

            try {
                Object result = finalMethod.invoke(obj, args);
                responseMessage.setS(ResponseStatus.S);
                responseMessage.setR(result);
            } catch (Throwable e) {
                log.error("服务端方法执行异常", e);
                responseMessage.setS(ResponseStatus.F);
                responseMessage.setR("服务端方法执行异常");
//                responseMessage.setE(e.getCause());
//                e.getCause();
//                StackTraceElement[] stackTrace = e.getCause().getStackTrace();
//                for (StackTraceElement stackTraceElement : stackTrace) {
//                    System.out.println(stackTraceElement.toString());
//                }
            } finally {
                responseMessage.setSeq(finalSeq);
                channelHandlerContext.writeAndFlush(responseMessage);
            }
        });
    }

}
