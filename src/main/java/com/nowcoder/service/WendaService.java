package com.nowcoder.service;

import org.springframework.stereotype.Service;

/**
 * Created by zhanghe on 2016/7/14.
 * Ioc依赖注入示例
 *  思想：所有的变量初始化，不是自己负责的，而是使用依赖注入的概念
 *  优点：无需关注变量初始化
 */
@Service
public class WendaService {
    public String getMessage(int userId){
        return "Hello Message:" + String.valueOf(userId);
    }
}
