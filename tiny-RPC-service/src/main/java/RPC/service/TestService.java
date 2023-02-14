package RPC.service;

import java.time.LocalDateTime;

public interface TestService {

    String getAnswer(String name);

    String getAnswer(String name, Integer age);

    Integer add();

    LocalDateTime getNow();
}
