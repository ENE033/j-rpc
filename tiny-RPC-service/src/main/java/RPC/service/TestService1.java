package RPC.service;

import java.time.LocalDateTime;
import java.util.Date;

public interface TestService1 {

    void doSomething();

    String timeTest(Date date, LocalDateTime localDateTime);

}
