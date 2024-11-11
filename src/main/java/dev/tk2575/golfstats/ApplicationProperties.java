package dev.tk2575.golfstats;

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
public class ApplicationProperties {
  @Value("${redis.host}")
  private String redisHost;

  @Value("${redis.port}")
  private int redisPort;
  
  @Value("${notion.token}")
  private String notionToken;
}
