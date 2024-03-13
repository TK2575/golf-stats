package dev.tk2575.golfstats.details.redis;

import com.github.fppt.jedismock.RedisServer;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class RedisServiceTest {
  
  private RedisService svc;
  
  @BeforeEach
  void setUp() throws IOException {
    var server = RedisServer.newRedisServer().start();
    this.svc = new RedisService(server.getHost(), server.getBindPort());
  }
  
  @Test
  void testSimpleSetAndGet() {
    svc.set("key", "value");
    assertEquals("value", svc.get("key"));
  }

}