/**
 * @author corleone
 * @date 2018/7/6 0006
 */
package com.hp.sh.expv3.config.redis;


import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.extension.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.commands.MultiKeyCommands;
import redis.clients.jedis.params.ZAddParams;

import java.util.*;
import java.util.Map.Entry;

public class RedisUtil {

    private JedisPool jedisPool;

    public RedisUtil(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public JedisCommands getJedis() {
        return jedisPool.getResource();
    }

    public MultiKeyCommands getJedisMultiKeyCommand() {
        return jedisPool.getResource();
    }

    public void returnJedisMultiKeyCommand(MultiKeyCommands jedis) {
        ((Jedis) jedis).close();
    }

    public void returnJedis(JedisCommands jedis) {
        ((Jedis) jedis).close();
    }

    public Jedis getOriginalJedis() {
        return jedisPool.getResource();
    }

    public void publish(String channel, String msg) {
        Jedis jedis = getOriginalJedis();
        try {
            jedis.publish(channel, msg);
        } finally {
            returnJedis(jedis);
        }
    }

    public void returnJedis(Jedis jedis) {
        jedis.close();
    }

    public <T> Map<String, T> hgetAll(String key, Class<T> cls) {
        JedisCommands jedis = getJedis();
        Map<String, String> values;
        try {
            values = jedis.hgetAll(key);
        } finally {
            returnJedis(jedis);
        }
        if (values == null || values.size() == 0) {
            return null;
        }
        try {
            TreeMap<String, T> objs = new TreeMap<>();
            for (Entry<String, String> entry : values.entrySet()) {
                String value = entry.getValue();
                objs.put(entry.getKey(), JSON.parseObject(value, cls));
            }
            return objs;
        } catch (Exception ex) {
            return null;
        }
    }

    public String hget(String key, String field) {
        JedisCommands jedis = getJedis();
        String value = "";
        try {
            value = jedis.hget(key, field);
        } finally {
            returnJedis(jedis);
        }
        return value;
    }

    public void hset(String key, String field, String value) {
        JedisCommands jedis = getJedis();
        try {
            jedis.hset(key, field, value);
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * Sets field in the hash stored at key to value, only if field does not yet exist.
     * If key does not exist, a new key holding a hash is created. If field already exists, this operation has no effect.
     *
     * @param value
     * @param key
     * @param field
     */
    public void hsetnx(String key, String field, String value) {
        JedisCommands jedis = getJedis();
        try {
            jedis.hsetnx(key, field, value);
        } finally {
            returnJedis(jedis);
        }
    }

    public void hmset(Map<String, String> objs, String key) {
        Map<String, String> values = new HashMap<>();
        for (Entry<String, String> entry : objs.entrySet()) {
            values.put(entry.getKey(), entry.getValue());
        }

        JedisCommands jedis = getJedis();
        try {
            jedis.hmset(key, values);
        } finally {
            returnJedis(jedis);
        }
    }

    public <T> void hmset(String key, Map<String, T> objs) {
        Map<String, String> values = new HashMap<>();
        for (Entry<String, T> entry : objs.entrySet()) {
            T obj = entry.getValue();
            String value = JsonUtil.toJsonString(obj);
            values.put(entry.getKey(), value);
        }

        JedisCommands jedis = getJedis();
        try {
            jedis.hmset(key, values);
        } finally {
            returnJedis(jedis);
        }
    }

    public <T> T hget(String key, String field, Class<T> cls) {
        JedisCommands jedis = getJedis();
        String value = "";
        try {
            value = jedis.hget(key, field);
        } finally {
            returnJedis(jedis);
        }
        if (StringUtils.isEmpty(value)) return null;
        try {
            return JSON.parseObject(value, cls);
        } catch (Exception ex) {
            return null;
        }
    }

    public <T> void hset(String key, String field, T obj) {
        String value = JsonUtil.toJsonString(obj);
        JedisCommands jedis = getJedis();
        try {
            jedis.hset(key, field, value);
        } finally {
            returnJedis(jedis);
        }
    }

    public void hdel(String key, String field) {
        JedisCommands jedis = getJedis();
        try {
            jedis.hdel(key, field);
        } finally {
            returnJedis(jedis);
        }
    }

    public Set<String> hkeys(String key) {
        JedisCommands jedis = getJedis();
        Set<String> keys;
        try {
            keys = jedis.hkeys(key);
        } finally {
            returnJedis(jedis);
        }
        return keys;
    }

    public <T> Map<String, T> hmget(String key, String[] fields, Class<T> cls) {
        Map<String, String> hmget = hmget(key, fields);
        Map<String, T> rs = new HashMap<>();
        if (null != hmget) {
            for (Entry<String, String> kv : hmget.entrySet()) {
                rs.put(kv.getKey(), JSON.parseObject(kv.getValue(), cls));
            }
        }
        return rs;
    }

    public <T> void set(String key, T obj, int expireInSecond) {
        String value = (obj instanceof String) ? obj.toString() : JsonUtil.toJsonString(obj);
        JedisCommands jedis = getJedis();
        try {
            if (expireInSecond > 0) {
                jedis.setex(key, expireInSecond, value);
            } else {
                jedis.set(key, value);
            }
        } finally {
            returnJedis(jedis);
        }
    }


    public boolean hexists(String key, String field) {
        JedisCommands jedis = getJedis();
        boolean ret = false;
        try {
            ret = jedis.hexists(key, field);
        } finally {
            returnJedis(jedis);
        }
        return ret;
    }


    public void hdel(String key, String... fields) {
        JedisCommands jedis = getJedis();
        try {
            jedis.hdel(key, fields);
        } finally {
            returnJedis(jedis);
        }
    }

    public void hdel(String key, Collection<String> fields) {
        if (null == fields || fields.isEmpty()) {
        } else {
            Set<String> fs = new HashSet<>();
            for (String field : fields) {
                if (null == field || field.trim().isEmpty()) {
                } else {
                    fs.add(field.trim());
                }
            }
            if (fs.isEmpty()) {
            } else {
                String[] keys = new String[fs.size()];
                List<String> list = new ArrayList<>(fs);
                for (int i = 0; i < list.size(); i++) {
                    keys[i] = list.get(i);
                }
                hdel(key, keys);
            }
        }
    }

    public boolean exists(String key) {
        JedisCommands jedis = getJedis();
        boolean value = false;
        try {
            value = jedis.exists(key);
        } finally {
            returnJedis(jedis);
        }
        return value;
    }


    public String get(String key) {
        JedisCommands jedis = getJedis();
        String value = "";
        try {
            value = jedis.get(key);
        } finally {
            returnJedis(jedis);
        }
        return value;
    }

    public void set(String key, String value) {
        JedisCommands jedis = getJedis();
        try {
            jedis.set(key, value);
        } finally {
            returnJedis(jedis);
        }
    }

    public void setex(String key, String value, int ex) {
        JedisCommands jedis = getJedis();
        try {
            jedis.setex(key, ex, value);
        } finally {
            returnJedis(jedis);
        }
    }

    public void del(String key) {
        JedisCommands jedis = getJedis();
        try {
            Long del = jedis.del(key);
        } finally {
            returnJedis(jedis);
        }
    }

    public List<String> hvals(String key) {
        JedisCommands jedis = getJedis();
        List<String> values;
        try {
            values = jedis.hvals(key);
        } finally {
            returnJedis(jedis);
        }
        return values;
    }

    public long hincrBy(String key, String field, long v) {
        // no transaction support
        JedisCommands jedis = getJedis();
        long ret = 0;
        try {
            ret = jedis.hincrBy(key, field, v);
        } finally {
            returnJedis(jedis);
        }
        return ret;
    }

    public Long incr(String key) {
        // no transaction support
        JedisCommands jedis = getJedis();
        long value = 0;
        try {
            value = jedis.incr(key);
        } finally {
            returnJedis(jedis);
        }
        return value;
    }

    public Long incrBy(String key, long v) {
        // no transaction support
        JedisCommands jedis = getJedis();
        long value = 0;
        try {
            value = jedis.incrBy(key, v);
        } finally {
            returnJedis(jedis);
        }
        return value;
    }

    public void expire(String key, int ex) {
        JedisCommands jedis = getJedis();
        try {
            jedis.expire(key, ex);
        } finally {
            returnJedis(jedis);
        }
    }

    public Map<String, String> hget(Map<String, String> keyFields) {
        Jedis jedis = getOriginalJedis();
        Map<String, String> result = new HashMap<>();
        try {
            Pipeline p = jedis.pipelined();
            Map<String, Response<String>> responses = new HashMap<>(keyFields.size());
            for (Entry<String, String> entry : keyFields.entrySet()) {
                responses.put(entry.getKey(), p.hget(entry.getKey(), entry.getValue()));
            }
            p.sync();
            for (Entry<String, Response<String>> kv : responses.entrySet()) {
                result.put(kv.getKey(), kv.getValue().get());
            }
        } finally {
            returnJedis(jedis);
        }
        return result;
    }

    public <T> Map<Long, T> hget(Set<Long> fields, String key, Class<T> cls) {
        if (null == fields || fields.size() == 0) {
            return new HashMap<>();
        }
        Set<String> fs = new HashSet<>(fields.size());
        for (Long field : fields) {
            fs.add("" + field);
        }
        Map<String, String> rs = hget(key, fs);
        Map<Long, T> map = new HashMap<>(rs.size());
        for (Entry<String, String> kv : rs.entrySet()) {
            map.put(Long.parseLong(kv.getKey()), JSON.parseObject(kv.getValue(), cls));
        }
        return map;
    }

    public Map<String, String> hget(String key, Set<String> fields) {
        if (null == fields || fields.size() == 0) {
            return new HashMap<>();
        }
        Jedis jedis = getOriginalJedis();
        Map<String, String> result = new HashMap<>();
        try {
            Pipeline p = jedis.pipelined();
            Map<String, Response<String>> responses = new HashMap<>(fields.size());
            for (String field : fields) {
                responses.put(field, p.hget(key, field));
            }
            p.sync();
            for (Entry<String, Response<String>> kv : responses.entrySet()) {
                result.put(kv.getKey(), kv.getValue().get());
            }
        } finally {
            returnJedis(jedis);
        }
        return result;
    }

    public String lindex(String key, long index) {
        JedisCommands jedis = getJedis();
        String value = "";
        try {
            value = jedis.lindex(key, index);
        } finally {
            returnJedis(jedis);
        }
        return value;
    }

    /**
     * @param key
     * @param start,include
     * @param end,exclude
     * @return
     */
    public List<String> lrange(String key, long start, long end) {
        JedisCommands jedis = getJedis();
        List<String> value = new ArrayList<>();
        try {
            jedis.lrange(key, start, end);
        } finally {
            returnJedis(jedis);
        }
        return value;
    }

    public void lpush(String key, String value) {
        JedisCommands jedis = getJedis();
        try {
            jedis.lpush(key, value);
        } finally {
            returnJedis(jedis);
        }
    }

    public String lpop(String key) {
        JedisCommands jedis = getJedis();
        String result = "";
        try {
            result = jedis.lpop(key);
        } finally {
            returnJedis(jedis);
        }
        return result;
    }

    public void rpush(String key, String value) {
        JedisCommands jedis = getJedis();
        try {
            jedis.rpush(key, value);
        } finally {
            returnJedis(jedis);
        }
    }

    public void ltrim(String key, long start, long end) {
        JedisCommands jedis = getJedis();
        try {
            jedis.ltrim(key, start, end);
        } finally {
            returnJedis(jedis);
        }
    }

    public long llen(String key) {
        JedisCommands jedis = getJedis();
        long value = 0;
        try {
            value = jedis.llen(key);
        } finally {
            returnJedis(jedis);
        }
        return value;
    }

    public void zremrangeByRank(String key, long start, long end) {
        JedisCommands jedis = getJedis();
        try {
            jedis.zremrangeByRank(key, start, end);
        } finally {
            returnJedis(jedis);
        }
    }

    public void zremrangeByScore(String key, double start, double end) {
        JedisCommands jedis = getJedis();
        try {
            jedis.zremrangeByScore(key, start, end);
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * Removes the specified members from the sorted set stored at key. Non existing members are ignored.
     * An error is returned when key exists and does not hold a sorted set.
     *
     * @param key
     * @param members
     */
    public void zrem(String key, String... members) {
        Jedis jedis = getOriginalJedis();
        try {
            jedis.zrem(key, members);
        } finally {
            returnJedis(jedis);
        }
    }

    public void zadd(String key, Map<String, Double> scoreMembers) {
        Jedis jedis = getOriginalJedis();
        try {
            jedis.zadd(key, scoreMembers);
        } finally {
            returnJedis(jedis);
        }
    }

    public void zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        Jedis jedis = getOriginalJedis();
        try {
            jedis.zadd(key, scoreMembers, params);
        } finally {
            returnJedis(jedis);
        }
    }

    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        JedisCommands jedis = getJedis();
        Set<Tuple> tuples;
        try {
            tuples = jedis.zrevrangeWithScores(key, start, end);
        } finally {
            returnJedis(jedis);
        }
        return tuples;
    }

    public Set<String> zrevrange(String key, long start, long end) {
        JedisCommands jedis = getJedis();
        try {
            return jedis.zrevrange(key, start, end);
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * replaced by {@linkplain #zrangeByScoreWithScores zrangeByScoreWithScores }
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Deprecated
    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        JedisCommands jedis = getJedis();
        Set<Tuple> tuples;
        try {
            tuples = jedis.zrangeWithScores(key, start, end);
        } finally {
            returnJedis(jedis);
        }
        return tuples;
    }

    /**
     * 若count = null，则分页条件失效
     * min,max 可选 double -inf +inf
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Map<String, Double> zrangeByScoreWithScores(String key, String min, String max, Integer offset, Integer count) {
        Map<String, Double> scoreIdxByElement = new HashMap<>();
        JedisCommands jedis = getJedis();
        try {
            Set<Tuple> tuples = (null == count) ? jedis.zrangeByScoreWithScores(key, min, max) : jedis.zrangeByScoreWithScores(key, min, max, offset.intValue(), count.intValue());
            if (null != tuples && !tuples.isEmpty()) {
                for (Tuple tuple : tuples) {
                    scoreIdxByElement.put(tuple.getElement(), tuple.getScore());
                }
            }
        } finally {
            returnJedis(jedis);
        }
        return scoreIdxByElement;
    }

    /**
     * 若count = null，则分页条件失效
     * min,max 可选 double -inf +inf
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Map<String, Double> zrevrangeByScoreWithScores(String key, String min, String max, Integer offset, Integer count) {
        Map<String, Double> scoreIdxByElement = new HashMap<>();
        JedisCommands jedis = getJedis();
        try {
            Set<Tuple> tuples = (null == count) ? jedis.zrevrangeByScoreWithScores(key, max, min) : jedis.zrevrangeByScoreWithScores(key, max, min, offset.intValue(), count.intValue());
            if (null != tuples && !tuples.isEmpty()) {
                for (Tuple tuple : tuples) {
                    scoreIdxByElement.put(tuple.getElement(), tuple.getScore());
                }
            }
        } finally {
            returnJedis(jedis);
        }
        return scoreIdxByElement;
    }

    /**
     * 若count = null，则分页条件失效
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<String> zrangeByScore(String key, String min, String max, Integer offset, Integer count) {
        JedisCommands jedis = getJedis();
        try {
            return (null == count) ? jedis.zrangeByScore(key, min, max) : jedis.zrangeByScore(key, min, max, offset.intValue(), count.intValue());
        } finally {
            returnJedis(jedis);
        }
    }

    public Set<String> zrange(String key, long start, long end) {
        JedisCommands jedis = getJedis();
        try {
            return jedis.zrange(key, start, end);
        } finally {
            returnJedis(jedis);
        }
    }

    public void rename(String key, String newKey) {
        Jedis jedis = getOriginalJedis();
        try {
            jedis.rename(key, newKey);
        } finally {
            returnJedis(jedis);
        }
    }

    public Set<String> keys(String pattern) {
        Jedis jedis = getOriginalJedis();
        try {
            Set<String> keys = jedis.keys(pattern);
            return keys;
        } finally {
            returnJedis(jedis);
        }
    }

    public Map<String, String> hmget(String key, String[] fields) {
        Map<String, String> map = new HashMap<>();
        if (null == fields || 0 == fields.length) {
        } else {
            Jedis jedis = getOriginalJedis();
            try {
                List<String> values = jedis.hmget(key, fields);
                for (int i = 0; i < fields.length; i++) {
                    if (null == values.get(i)) {
                    } else {
                        map.put(fields[i], values.get(i));
                    }
                }
            } finally {
                returnJedis(jedis);
            }
        }
        return map;
    }

    public Map<String, String> hmget(String key, Collection<String> fields) {
        if (null == fields || 0 == fields.size()) {
            return new HashMap<>();
        } else {
            String[] fs = new String[fields.size()];
            fields.toArray(fs);
            return hmget(key, fs);
        }
    }


    public Map<String, String> hgetAll(String key) {
        Map<String, String> empty = new HashMap<>();
        if (null == key || "".equals(key.trim())) {
            return empty;
        }
        Jedis jedis = getOriginalJedis();
        try {
            Map<String, String> value = jedis.hgetAll(key);
            if (null == value) {
                value = empty;
            }
            return value;
        } finally {
            returnJedis(jedis);
        }
    }

    public Set<Tuple> zpopmin(String key, int count) {
        Jedis jedis = getOriginalJedis();
        try {
            Set<Tuple> tupls = jedis.zpopmin(key, count);
            if (null == tupls) {
                tupls = new TreeSet<>();
            }
            return tupls;
        } finally {
            returnJedis(jedis);
        }
    }

    public Set<Tuple> zpopmax(String key, int count) {
        Jedis jedis = getOriginalJedis();
        try {
            Set<Tuple> tupls = jedis.zpopmax(key, count);
            if (null == tupls) {
                tupls = new TreeSet<>();
            }
            return tupls;
        } finally {
            returnJedis(jedis);
        }
    }

    public String info() {
        Jedis jedis = getOriginalJedis();
        try {
            String info = jedis.info();
            return info;
        } finally {
            returnJedis(jedis);
        }
    }

    public void mdel(Set<String> keySet) {
        if (null == keySet || keySet.isEmpty()) {
            return;
        }

        List<String> keyList = new ArrayList<>(keySet);
        keyList.sort(Comparator.naturalOrder());
        String[] keyArray = new String[keyList.size()];
        for (int i = 0; i < keyList.size(); i++) {
            keyArray[i] = keyList.get(i);
        }

        MultiKeyCommands jedisMultiKeyCommand = getJedisMultiKeyCommand();
        try {
            jedisMultiKeyCommand.del(keyArray);
        } finally {
            returnJedisMultiKeyCommand(jedisMultiKeyCommand);
        }

    }

    public Map<String, String> mget(Set<String> keySet) {
        Map<String, String> rs = new HashMap<>();

        if (null == keySet || keySet.isEmpty()) {
            return rs;
        }

        List<String> keyList = new ArrayList<>(keySet);
        keyList.sort(Comparator.naturalOrder());
        String[] keyArray = new String[keyList.size()];
        for (int i = 0; i < keyList.size(); i++) {
            keyArray[i] = keyList.get(i);
        }

        List<String> mget = new ArrayList<>(keyArray.length);
        MultiKeyCommands jedisMultiKeyCommand = getJedisMultiKeyCommand();
        try {
            mget = jedisMultiKeyCommand.mget(keyArray);
        } finally {
            returnJedisMultiKeyCommand(jedisMultiKeyCommand);
        }

        if (mget.size() == keySet.size()) {
            for (int i = 0; i < keyList.size(); i++) {
                if (mget.size() > i) {
                    rs.put(keyList.get(i), mget.get(i));
                }
            }
        }

        return rs;
    }

    public void mset(Map<String, String> map) {
        if (null == map || map.isEmpty()) {
            return;
        }

        String[] kvs = new String[map.size() * 2];

        List<String> keys = new ArrayList<>(map.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key);
            if (null != key && null != value) {
                kvs[i * 2] = key;
                kvs[i * 2 + 1] = value;
            }
        }

        MultiKeyCommands jedisMultiKeyCommand = getJedisMultiKeyCommand();
        try {
            jedisMultiKeyCommand.mset(kvs);
        } finally {
            returnJedisMultiKeyCommand(jedisMultiKeyCommand);
        }

    }

}