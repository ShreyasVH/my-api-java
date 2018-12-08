package myapi.services;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import myapi.utils.Utils;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class RedisService
{
    @Getter
    @Setter
    private Jedis redis;

    @Getter
    @Setter
    private Boolean isConnected;

    public RedisService()
    {
        JedisPool pool = new JedisPool(System.getenv("REDIS_IP"), Integer.parseInt(System.getenv("REDIS_PORT")));
        setIsConnected(true);
        try
        {
            redis = pool.getResource();
        }
        catch(JedisConnectionException e)
        {
            setIsConnected(false);
        }
    }

    public void set(String key, Object value)
    {
        redis.set(key.getBytes(), Utils.serialize(value));
    }

    public Object get(String key)
    {
        Object value = new Object();
        byte[] val = redis.get(key.getBytes());
        if(val != null)
        {
            value = Utils.deserialize(val);
        }
        return value;
    }
}
