package com.nowcoder.async;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhanghe on 2017/6/20.
 */
public class EventModel {
    private EventType type; //事件类型，eg，评论
    private int actorId; //触发者，eg：谁评论了
    private int entityType;//触发载体 eg：评论了哪一个题目
    private int entityId; //触发载体 eg：评论了哪一个题目
    private int entityOwnerId; // 这个是触发载体相关的某个人

    //为了读取上面这些函数方便，所以定义一个单独的函数，
    private Map<String, String> exts = new HashMap<String, String>();

    public EventModel() {
    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public EventModel(EventType type) {
        this.type = type;
    }

    public String getExt(String key) {
        return exts.get(key);
    }


    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
