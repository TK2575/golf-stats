package dev.tk2575.golfstats.details.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RedisConfig {
  @Value("${redis.host}")
  private String host;

  @Value("${redis.port}")
  private int port;
}
