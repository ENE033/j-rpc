package RPC.serviceImpl;

import RPC.core.annotation.Service;
import RPC.service.TestService;

@Service
public class TestServiceImpl implements TestService {
    @Override
    public String getAnswer(String name) {
        return name.toLowerCase();
    }

    @Override
    public String getAnswer(String name, Integer age) {
        return name + age;
    }
}
