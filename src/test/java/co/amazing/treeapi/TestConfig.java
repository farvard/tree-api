package co.amazing.treeapi;

import org.junit.rules.ExternalResource;
import redis.embedded.RedisServer;

public class TestConfig extends ExternalResource {

    private static RedisServer redisServer;
    private static int count = 0;

    protected void before() {
        if (redisServer == null) {
            redisServer = RedisServer.builder()
                  .port(6379)
                  .setting("bind 127.0.0.1")
                  .setting("maxmemory 128M")
                  .build();
            redisServer.start();
        }
    }

    protected void after() {
        if (redisServer != null) {
            redisServer.stop();
            redisServer = null;
        }
    }

}
