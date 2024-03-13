package dev.tk2575.golfstats.details.redis;

import redis.clients.jedis.JedisPooled;

public class RedisService {
  private final JedisPooled jedis;

  public RedisService(String host, int port) {
    this.jedis = new JedisPooled(host.equals("0.0.0.0") ? "localhost" : host, port);
  }

  public void set(String key, String value) {
    this.jedis.set(key, value);
  }
  
  public String get(String key) {
    return this.jedis.get(key);
  }

}
