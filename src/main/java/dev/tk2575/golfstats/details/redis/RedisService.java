package dev.tk2575.golfstats.details.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

public class RedisService {
  private final JedisPooled jedis;
  private final Gson gson = new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();

  public RedisService(String host, int port) {
    this.jedis = new JedisPooled(host.equals("0.0.0.0") ? "localhost" : host, port);
  }

  public GolfRound getRound(String roundId) {
    String round = jedis.get(generateKey(RedisGolfRound.KEY, roundId));
    return gson.fromJson(round, RedisGolfRound.class).toGolfRound();
  }

  public String saveRound(GolfRound round) {
    String s = getSequence(RedisGolfRound.KEY);
    RedisGolfRound redisRound = new RedisGolfRound(round, s);
    jedis.set(generateKey(RedisGolfRound.KEY, s), gson.toJson(redisRound));
    return s;
  }
  
  public List<GolfRound> getAllRounds() {
    return getAllRounds(false);
  }

  public List<GolfRound> getAllRounds(boolean validOnly) {
    Set<String> keys = new HashSet<>();
    String cursor = "0";
    do {
      ScanParams params = new ScanParams().match(generateKey(RedisGolfRound.KEY, "*")).count(1000);
      ScanResult<String> scan = jedis.scan(cursor, params);
      cursor = scan.getCursor();
      keys.addAll(scan.getResult());
    } while (!cursor.equals("0"));
    
    List<RedisGolfRound> roundDtos = jedis.mget(keys.toArray(String[]::new)).stream()
        .map(each -> gson.fromJson(each, RedisGolfRound.class)).toList();
    if (validOnly) {
      roundDtos = roundDtos.stream().filter(each -> each.isValid()).toList();
    }
    return roundDtos.stream().map(RedisGolfRound::toGolfRound).toList();
  }

  void set(String key, String value) {
    this.jedis.set(key, value);
  }

  String get(String key) {
    return this.jedis.get(key);
  }

  private String generateKey(String objectKey, String id) {
    return String.join(":", objectKey, id);
  }

  private String getSequence(String objectKey) {
    return getSequence(objectKey, 3);
  }

  private String getSequence(String objectKey, int retries) throws IllegalStateException {
    while (retries-- > 0) {
      jedis.incr(objectKey);
      String s = jedis.get(objectKey);
      if (jedis.get(generateKey(objectKey, s)) == null) {
        return s;
      }
    }
    throw new IllegalStateException("Failed to generate unique sequence for " + objectKey);
  }

}
