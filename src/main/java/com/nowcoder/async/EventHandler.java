package com.nowcoder.async;

import java.util.List;

/**
 * Created by zhanghe on 2017/6/20.
 */
public interface EventHandler {
    void doHandle(EventModel model); //当这些Event来的时候，使用这里进行处理

    List<EventType> getSupportEventTypes(); //这里面存储EventHandler关注哪些Event
}