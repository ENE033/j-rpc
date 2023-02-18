package RPC.service;

import java.time.LocalDateTime;
import java.util.Date;

public interface TestService {

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
