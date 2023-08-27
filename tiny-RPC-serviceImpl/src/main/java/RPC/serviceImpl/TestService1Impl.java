package RPC.serviceImpl;

import RPC.core.annotation.RPCService;
import RPC.service.TestService1;

import java.time.LocalDateTime;
import java.util.Date;

@RPCService(beanName = "testService1")
public class TestService1Impl implements TestService1 {
    @Override
    public void doSomething() {
        System.out.println("2214");
    }


    @Override
    public String timeTest(Date date, LocalDateTime localDateTime) {
        System.out.println(date);
        System.out.println(localDateTime);
        return "111";
    }
}
