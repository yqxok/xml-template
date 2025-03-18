package com.yonyou.jb.bip.controller.util.json;

import java.util.Map;

public interface JsonNode {
    /**
     * 获取map
     * @return
     */
    Map<Object,Object> getMap();

    /**
     * json序列化
     * @return
     */
    String toJsonString();

    /**
     * 使用表达式获取节点
     * @param key
     * @return
     */
    Object getNode(String key);
    /**
     * 使用表达式获取节点,类型自动转换
     * @param key
     * @return
     */
    <T> T getNode(String key,Class<T> clazz);
    /**
     * 使用表达式插入节点
     * @param key
     * @param value
     */
    void addMapNode(String key,Object value);
    /**
     * 使用索引直接插入节点
     * @param key
     * @param value
     */
    void addNode(Object key, Object node);

    /**
     * 生成键值对数据表
     * @return
     */
    Map<String,Object> toKeyValue();
}
