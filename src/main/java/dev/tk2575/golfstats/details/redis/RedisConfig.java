package dev.tk2575.golfstats.details.redis;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ToString
@Getter
public class RedisConfig {
  @Value("${redis.host}")
  private String host;

  @Value("${redis.port}")
  private int port;
}
