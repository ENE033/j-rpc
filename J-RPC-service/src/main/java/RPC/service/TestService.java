package RPC.service;

import RPC.core.annotation.SyncRPC;
import RPC.core.annotation.TpsLimit;

import java.time.LocalDateTime;
import java.util.Date;

public interface TestService {

    @SyncRPC
    @TpsLimit(rate = 10, interval = 1000L)
    String CPUTask();

    String IOTask();

    //    @SyncRPC
    boolean decCount();

    int getCount();

    String getAnswer(String name);

    String getAnswer(String name, Integer age);

    String single(String name);

    Integer add();

    LocalDateTime getNow();

    Date getNowDate();
}
