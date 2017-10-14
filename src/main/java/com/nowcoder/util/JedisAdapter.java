package com.nowcoder.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.controller.CommentController;
import com.nowcoder.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by zhanghe on 2017/6/8.
 * redis使用示例
 */
@Service
public class JedisAdapter implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool jedisPool ;

    //首先改写print函数
    public static void print(int index, Object obj){
        System.out.println(String.format("%d, %s", index, obj.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://localhost:6379/9");  //默认连接本地的6379端口，也可以指点，这里指定第9个数据库（共16个）
        //jedis.flushAll(); //把所有数据库删掉
        //jedis.flushDB(); //把数据库删掉

        //get set
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello","newhello");
        print(1, jedis.get("newhello"));
        jedis.setex("hello2", 15, "world"); //设置15s的时效，15秒后，这个键值对就被删了，这个功能可以用在验证码，缓存上

        //数值操作
        //比如一个帖子的浏览量，可以用redis，因为他是存在内存中，快
        jedis.set("pv", "100");
        jedis.incr("pv");  //给100加一 = 101
        print(2, jedis.get("pv")); //106
        jedis.incrBy("pv",5); //给pv的value加5
        print(2, jedis.get("pv")); // 104
        jedis.decrBy("pv",2); //给pv的value减2
        print(2, jedis.get("pv"));

        print(3, jedis.keys("*")); //所有的都打印出来

        //list
        String listName = "list";
        jedis.del(listName); //把这条数据删掉
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName,"a" + String.valueOf(i));  //lpush就是往list中插入，list-》l
        }
        print(4, jedis.lrange(listName, 0, 10)); //把list中的数取出来，数字是下标
        print(4, jedis.lrange(listName, 0, 3)); //[a9, a8, a7, a6]
        print(5, jedis.llen(listName)); //队列的长度10
        print(6, jedis.lpop(listName)); //，弹出去一个，把a9弹出去了
        print(7, jedis.llen(listName)); //队列长度剩9了
        print(8, jedis.lindex(listName,3)); //把下标是3的数取出来
        print(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx")); //在a4前插入
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb")); //在a4后插入
        print(11, jedis.lrange(listName, 0, 11));

        //hash
        String userKsy = "userxx";  //可以看做用户的key
        jedis.hset(userKsy,"name","jim");  //这些是属性
        jedis.hset(userKsy,"age","12");
        jedis.hset(userKsy,"phone","13945645645");
        print(12, jedis.hget(userKsy,"name"));
        print(13, jedis.hgetAll(userKsy));
        jedis.hdel(userKsy,"phone"); //删除这个属性
        print(14, jedis.hgetAll(userKsy));
        print(16, jedis.hexists(userKsy, "email"));
        print(17, jedis.hexists(userKsy, "age"));
        print(18, jedis.hkeys(userKsy));  //取出key
        print(19, jedis.hvals(userKsy)); //取出value
        jedis.hsetnx(userKsy, "school", "zju"); //不存在，设置
        jedis.hsetnx(userKsy, "name", "yxy");  //存在，所以不设置
        print(20,jedis.hgetAll(userKsy)); //{name=jim, school=zju, age=12}

        //集合
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKey1, String.valueOf(i)); //0~10的集合
            jedis.sadd(likeKey2, String.valueOf(i*i));  //0~10的平方的集合
        }
        print(21,jedis.smembers(likeKey1)); //s就是set，这是把所有的数据取出来 
        print(22,jedis.smembers(likeKey2));
        print(23,jedis.sunion(likeKey1, likeKey2)); //两个集合求并
        print(24,jedis.sdiff(likeKey1, likeKey2)); //两个集合不一样的值
        print(25, jedis.sinter(likeKey1,likeKey2)); //求交
        //也可以做一些查询的功能
        print(26,jedis.sismember(likeKey1, "12")); //查询集合里面有没有
        print(26,jedis.sismember(likeKey2, "16"));
        jedis.srem(likeKey1,"5"); //把5删了
        print(27, jedis.smembers(likeKey1)); //[0, 1, 2, 3, 4, 6, 7, 8, 9]
        jedis.smove(likeKey2, likeKey1, "25"); //把likeKey2中的25复制到likeKey1中
        jedis.smove(likeKey2, likeKey1, "26"); //没有不能复制
        print(28, jedis.smembers(likeKey1));
        print(29, jedis.scard(likeKey1)); //里面有多少个值

        //优先队列 zset
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "jim");
        jedis.zadd(rankKey, 60, "ben");
        jedis.zadd(rankKey, 79, "lucy");
        jedis.zadd(rankKey, 46, "tom");
        jedis.zadd(rankKey, 34, "mei");
        print(30,jedis.zcard(rankKey)); //有多少个
        print(31,jedis.zcount(rankKey, 61, 100)); //61到100之间有多少
        print(32,jedis.zscore(rankKey, "lucy")); //lucy是多少
        jedis.zincrby(rankKey,2,"lucy"); //给lucy加两分
        print(33,jedis.zscore(rankKey, "lucy"));
        jedis.zincrby(rankKey,2,"luc"); //luc是没有的，给luc加两分，最后luc就是2分
        print(34,jedis.zscore(rankKey, "luc"));
        print(35,jedis.zrange(rankKey,0, 100)); //第0到100，luc插进来了 ；[luc, jim, mei, tom, ben, lucy]
        print(36,jedis.zrange(rankKey,1, 3)); //从低到高排序的  [jim, mei, tom]
        print(37,jedis.zrevrange(rankKey,1, 3)); //从高到低排序的 [ben, tom, mei]
        for(Tuple tuple : jedis.zrangeByScoreWithScores(rankKey,"60", "100")){
            print(38, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
            /**
             * 38, ben:60.0
               38, lucy:81.0
             */
        }

        print(39,jedis.zrank(rankKey,"ben")); //4
        print(39,jedis.zrevrank(rankKey,"ben")); //1

        String setKey = "zset";
        jedis.zadd(setKey,1, "a");
        jedis.zadd(setKey,1, "b");
        jedis.zadd(setKey,1, "c");
        jedis.zadd(setKey,1, "d");
        jedis.zadd(setKey,1, "e");
        //可以根据字母排序
        print(40,jedis.zlexcount(setKey,"-","+")); //-是负无穷，+正无穷，就相当于全部，最后结果为5
        print(41,jedis.zlexcount(setKey,"[b","[d")); //b开始，d结束，共3个
        print(42,jedis.zlexcount(setKey,"(b","[d")); //b开始（开），d结束，共2个
        jedis.zrem(setKey, "b");//直接把b删掉
        print(43,jedis.zrange(setKey,0,10)); //[a, c, d, e]
        jedis.zremrangeByLex(setKey,"(c","+"); //根据字典序排序，c以上的全部干掉
        print(44, jedis.zrange(setKey,0,10)); //[a, c]

        //连接池
        /*JedisPool pool = new JedisPool("redis://localhost:6379/9");
        for (int i = 0; i < 100; i++) {
            Jedis j = pool.getResource();
            print(45,j.get("pv"));
            j.close();
        }*/

        //redis做缓存,对象来的时候，先序列化成JSON，然后set进redis，然后需要的时候再get
        //存进去
        User u = new User();
        u.setName("xx");
        u.setPassword("pp");
        u.setHeadUrl("a.png");
        u.setId(1);
        jedis.set("u", JSONObject.toJSONString(u));
        print(46,jedis.get("user1")); //{"headUrl":"a.png","id":1,"name":"xx","password":"pp"}

        //取出来
        String value = jedis.get("u");
        User u2 = JSON.parseObject(value, User.class);
        System.out.println(u2.getName() + "," + u2.getPassword() +  "," + u2.getHeadUrl() + "," + u2.getId()); //xx,pp,a.png,1


    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     *
     * @throws Exception in the event of misconfiguration (such
     *                   as failure to set an essential property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool("redis://localhost:6379/9");
    }

    public long sadd(String key, String value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sadd(key, value);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage() );
        } finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.srem(key, value);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage() );
        } finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage() );
        } finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key, String value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sismember(key, value);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage() );
        } finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return false;
    }
    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }
    //开启一个事务
    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
        }
        return null;
    }

    //执行
    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            tx.discard();
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException ioe) {
                    // ..
                }
            }

            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
    //把用户加到关注列表中
    public long zadd(String key, double score, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zadd(key, score, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Double zscore(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

}
