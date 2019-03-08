/**
 * 
 */
package com.plf.mybatis.executor.statement;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.plf.mybatis.mapping.MappedStatement;
import com.plf.mybatis.utils.CommonUtis;


/**
 * Statement处理类
 * 
 * @author PLF
 * @date 2019年3月6日
 */
public class SimpleStatementHandler implements StatementHandler
{
    /** #{}正则匹配 */
    private static Pattern param_pattern = Pattern.compile("#\\{([^\\{\\}]*)\\}");

    private MappedStatement mappedStatement;

    /**
     * 默认构造方法
     * 
     * @param mappedStatement
     */
    public SimpleStatementHandler(MappedStatement mappedStatement)
    {
        this.mappedStatement = mappedStatement;
    }

    /**
     * prepare
     *
     * @param paramConnection
     * @param paramInteger
     * @return
     * @throws SQLException
     */
    @Override
    public PreparedStatement prepare(Connection paramConnection)
        throws SQLException
    {
        String originalSql = mappedStatement.getSql();

        if (CommonUtis.isNotEmpty(originalSql))
        {
            // 替换#{}，预处理，防止SQL注入
            return paramConnection.prepareStatement(parseSymbol(originalSql));
        }
        else
        {
            throw new SQLException("original sql is null.");
        }
    }

    /**
     * query
     *
     * @param preparedStatement
     * @return
     * @throws SQLException
     */
    @Override
    public ResultSet query(PreparedStatement preparedStatement)
        throws SQLException
    {
        return preparedStatement.executeQuery();
    }

    /**
     * update
     *
     * @param preparedStatement
     * @throws SQLException
     */
    @Override
    public void update(PreparedStatement preparedStatement)
        throws SQLException
    {
        preparedStatement.executeUpdate();
    }

    /**
     * 将SQL语句中的#{}替换为？，源码中是在SqlSourceBuilder类中解析的
     * 
     * @param source
     * @return
     */
    private static String parseSymbol(String source)
    {
        source = source.trim();
        Matcher matcher = param_pattern.matcher(source);
        return matcher.replaceAll("?");
    }

}
