/**
 * 
 */
package com.plf.mybatis.binding;


import java.lang.reflect.Proxy;

import com.plf.mybatis.session.SqlSession;


/**
 * Mapper代理工厂
 * 
 * @author PLF
 * @date 2019年3月6日
 */
public class MapperProxyFactory<T>
{

    private final Class<T> mapperInterface;

    /**
     * 初始化方法
     * 
     * @param mapperInterface
     */
    public MapperProxyFactory(Class<T> mapperInterface)
    {
        this.mapperInterface = mapperInterface;
    }

    /**
     * 根据sqlSession创建一个代理
     * 
     * @param sqlSession
     * @return
     * @see
     */
    public T newInstance(SqlSession sqlSession)
    {
        MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, this.mapperInterface);
        return newInstance(mapperProxy);
    }

    /**
     * 根据mapper代理返回实例
     * 
     * @param mapperProxy
     * @return
     * @see
     */
    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy<T> mapperProxy)
    {
        return (T)Proxy.newProxyInstance(this.mapperInterface.getClassLoader(), new Class[] {this.mapperInterface},
            mapperProxy);
    }
}
