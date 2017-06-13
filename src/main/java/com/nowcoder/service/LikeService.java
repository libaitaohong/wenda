package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhanghe on 2017/6/10.
 * 点赞
 */
@Service
public class LikeService {

    @Autowired
    JedisAdapter jedisAdapter; //所有的数据都包装在redis中

    //找到当前有多少人喜欢
    public long getLikeCount(int entityType, int entityId){
        String likekey = RedisKeyUtil.getLikeKey(entityType,entityId);
        return jedisAdapter.scard(likekey);
    }

    //点赞或者踩之后再看帖子的时候，相应的地方会加亮，所以要获取该用户对于这个帖子的状态
    public int getLikeStatus(int userId, int entityType, int entityId){
        String likekey = RedisKeyUtil.getLikeKey(entityType,entityId);
        if (jedisAdapter.sismember(likekey,String.valueOf(userId))){
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        //如果不喜欢就返回-1，否则返回0，代表既没有喜欢也没有不喜欢
        return jedisAdapter.sismember(disLikeKey,String.valueOf(userId)) ? -1 : 0;
    }

    //喜欢函数，某个人喜欢某样东西添加进redis，东西是entityType+entityId
    public  long like(int userId, int entityType, int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId); //自动生成key
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        //就是点赞之后，如果之前点过踩，把踩删掉
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }

    public  long dislike(int userId, int entityType, int entityId){
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        jedisAdapter.sadd(disLikeKey,String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId); //自动生成key
        jedisAdapter.srem(likeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }
}
