package RPC.service;

import RPC.core.annotation.SyncRPC;

import java.time.LocalDateTime;
import java.util.Date;

public interface TestService {

    @SyncRPC
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
