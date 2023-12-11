package org.ene.RPC.autoconfigure;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jrpc")
public class JRPCProperties {

//    Nacos nacos;

    Client client;

    Server server;

    @Data
    static class Server {
        String host;
        Integer port;
        Nacos nacos;
    }


    @Data
    static class Client {
        Nacos nacos;
    }


    @Data
    static class Nacos {

        Configuration configuration;

        Registry registry;

        @Data
        static class Configuration {
            String address;
            String dataId;
            String group;
        }

        @Data
        static class Registry {
            String address;
        }

    }


}
