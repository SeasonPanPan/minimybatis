/*
 * 文件名：MapperRegistry.java
 * 版权：Copyright by www.huawei.com
 * 描述：
 * 修改人：ZTE
 * 修改时间：2019年3月8日
 * 跟踪单号：
 * 修改单号：
 * 修改内容：
 */

package com.plf.mybatis.binding;

import java.util.HashMap;
import java.util.Map;

import com.plf.mybatis.session.SqlSession;

/**
 * 〈一句话功能简述〉
 * 
 * @author PLF
 * @date 2019年3月8日
 * @version 1.0
 */

public class MapperRegistry
{
    /** the knownMappers */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    /**
     * 注册代理工厂
     * 
     * @param type 
     */
    public <T> void addMapper(Class<T> type)
    {
        this.knownMappers.put(type, new MapperProxyFactory<T>(type));
    }
    
    /**
     * 获取代理工厂实例
     * 
     * @param type
     * @param sqlSession
     * @return 
     */
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type, SqlSession sqlSession)
    {
      MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>)this.knownMappers.get(type);
      
      return mapperProxyFactory.newInstance(sqlSession);
    }
}
