package com.nowcoder.util;

/**
 * Created by zhanghe on 2017/6/10.
 * 生成rediskey
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";

    //喜欢某种东西
    public static String getLikeKey(int entityType, int  entityId){
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    //不喜欢某种东西
    public static String getDisLikeKey(int entityType, int  entityId){
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

}
