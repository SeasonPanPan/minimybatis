/**
 * 
 */
package com.plf.mybatis.executor.resultset;


import java.sql.ResultSet;
import java.util.List;


/**
 * ResultSetHandler.java
 * 
 * @author PLF
 * @date 2019年3月6日
 */
public interface ResultSetHandler
{

    /**
     * 处理查询结果
     * 
     * @param resultSet
     * @return 
     * @see 
     */
    <E> List<E> handleResultSets(ResultSet resultSet);

}
