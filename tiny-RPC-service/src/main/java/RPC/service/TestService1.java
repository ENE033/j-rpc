package RPC.service;

import java.time.LocalDateTime;
import java.util.Date;

public interface TestService1 {

    void doSomething();

    void timeTest(Date date, LocalDateTime localDateTime);

    String timeTest1(Date date, LocalDateTime localDateTime);

}
