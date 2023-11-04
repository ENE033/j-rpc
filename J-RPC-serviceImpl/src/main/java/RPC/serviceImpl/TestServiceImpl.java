package RPC.serviceImpl;

import RPC.core.annotation.RPCService;
import RPC.service.TestService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RPCService(beanName = "testService11")
public class TestServiceImpl implements TestService {
    public static int count = 0;


    @Override
    public String CPUTask() {
        count++;
        return "CPU任务结束" + count;
    }

    @Override
    public String IOTask() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "IO结果";
    }


    @Override
    public boolean decCount() {
        if (count > 0) {
            count--;
            return true;
        }
        return false;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public String getAnswer(String name) {
        if (name == null) {
            return "这是一个空对象";
        }
        return name.toLowerCase();
    }

    @Override
    public String getAnswer(String name, Integer age) {
        if (age == null) {
            return getAnswer(name) + "空年龄";
        }
        return getAnswer(name) + age;
//        return name + age;
    }

    @Override
    public String single(String name) {
        System.out.println(count + "开始");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(count + "结束");
        count++;
        return name;
    }

    @Override
    public Integer add() {
//        for (int i = 0; i < 1000; i++) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        count++;
//        }
        return count;
    }

    @Override
    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    @Override
    public Date getNowDate() {
        return new Date();
    }

}
