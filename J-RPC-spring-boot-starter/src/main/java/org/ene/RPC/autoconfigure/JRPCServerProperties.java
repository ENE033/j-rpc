package org.ene.RPC.autoconfigure;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jrpc")
public class JRPCServerProperties {

    Nacos nacos;

    Server server;

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

    @Data
    static class Server {
        String host;
        Integer port;
    }

}
