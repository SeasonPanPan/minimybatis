/**
 * 
 */
package com.plf.mybatis.session.defaults;


import java.util.List;

import com.plf.mybatis.executor.Executor;
import com.plf.mybatis.executor.SimpleExecutor;
import com.plf.mybatis.mapping.MappedStatement;
import com.plf.mybatis.session.Configuration;
import com.plf.mybatis.session.SqlSession;
import com.plf.mybatis.utils.CommonUtis;


/**
 * 默认SQL会话实现类
 * 
 * @author PLF
 * @date 2019年3月6日
 */
public class DefaultSqlSession implements SqlSession
{

    private final Configuration configuration;

    private final Executor executor;

    /**
     * 默认构造方法
     * 
     * @param configuration
     */
    public DefaultSqlSession(Configuration configuration)
    {
        this.configuration = configuration;
        this.executor = new SimpleExecutor(configuration);

    }

    /**
     * 查询带条记录
     *
     * @param paramString
     * @return
     */
    @Override
    public <T> T selectOne(String statementId, Object parameter)
    {
        List<T> results = this.<T> selectList(statementId, parameter);

        return CommonUtis.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * 查询多条记录
     *
     * @param statement ID为mapper类全名+方法名
     * @param parameter 参数列表
     * @return
     */
    @Override
    public <E> List<E> selectList(String statementId, Object parameter)
    {
        MappedStatement mappedStatement = this.configuration.getMappedStatement(statementId);
        return this.executor.<E> doQuery(mappedStatement, parameter);
    }

    /**
     * 更新
     *
     * @param statementId
     * @param parameter
     */
    @Override
    public void update(String statementId, Object parameter) 
    {
        MappedStatement mappedStatement = this.configuration.getMappedStatement(statementId);
        this.executor.doUpdate(mappedStatement, parameter);
    }
    
    @Override
    public void insert(String statementId, Object parameter) 
    {
        //TODO 待实现
    }
    
    /**
     * 获取Mapper
     *
     * @param paramClass
     * @return
     */
    @Override
    public <T> T getMapper(Class<T> type)
    {
        return configuration.<T> getMapper(type, this);
    }

    /**
     * getConfiguration
     *
     * @return
     */
    @Override
    public Configuration getConfiguration()
    {
        return this.configuration;
    }

}
