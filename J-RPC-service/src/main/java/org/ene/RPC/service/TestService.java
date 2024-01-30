package org.ene.RPC.service;

import org.ene.RPC.core.annotation.SyncRPC;
import org.ene.RPC.core.annotation.TpsLimit;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public interface TestService {

    @SyncRPC
    @TpsLimit(count = 10, length = 1000L)
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

    CompletableFuture<String> asyncCall(String string);

    CompletableFuture<Date> asyncCallDate(Date date);
}
