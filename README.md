# minimybatis
手写迷你mybatis框架，里面使用了mybatis设计模式和框架
手写思路和框架图详细解释：https://blog.csdn.net/kuailebuzhidao/article/details/88355236

###################### 正文 #######################################
继上篇手写spring后（https://blog.csdn.net/kuailebuzhidao/article/details/87869279），感觉有必要继续把mybatis框架也手写出来，供深入理解。

网上已经有很多手写框架的博客，但是很多只是按照mybatis流程，面向过程地写：解析xml->代理反射mapper->调用JDBC获取结果。虽然这样理解是对的，但是失去了理解mybatis源码意义。我遵循mybatis源码整体框架和设计，使用源码包名和类名，配合工厂模式和代理模式，写了精简版的mybatis框架。能看懂我这个minmybatis，就能更加容易地理解庞大的源码体系。手写框架的源码已经上传到github上，欢迎下载点星，感谢！
github地址：https://github.com/SeasonPanPan/myspring

先看以下spring源码中的类关系图：(CSDN原地址查看)

这个图很清晰地画出了mybatis重要组件类和流程：

从MyBatis源码实现的角度来看，MyBatis的主要的核心部件有以下几个：

Configuration：MyBatis所有的配置信息都维持在Configuration对象之中，核心类；

SqlSession：作为MyBatis工作的主要顶层API，表示和数据库交互的会话，完成必要数据库增删改查功能；

Executor：MyBatis执行器，是MyBatis 调度的核心，负责SQL语句的生成和查询缓存的维护；

StatementHandler：封装了JDBC Statement操作，负责对JDBC statement 的操作，如设置参数、将Statement结果集转换成List集合。

ParameterHandler：负责对用户传递的参数转换成JDBC Statement 所需要的参数；

ResultSetHandler：负责将JDBC返回的ResultSet结果集对象转换成List类型的集合；

MappedStatement：MappedStatement维护了一条<select|update|delete|insert>节点的封装；

MapperProxy和MapperProxyFactory：Mapper代理，使用原生的Proxy执行mapper里的方法。


我从测试类main函数说说整体手写过程。

public static void main(String[] args)
{
 
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("conf.properties");
    SqlSession session = factory.openSession();
 
    UserMapper userMapper = session.getMapper(UserMapper.class);
    User user = userMapper.getUser("1");
    System.out.println("******* " + user);
    System.out.println("*******all " + userMapper.getAll());
    
}
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("conf.properties");

解释：在SqlSessionFactoryBuilder中加载properties文件配置返回一个默认SqlSessionFactory

public SqlSessionFactory build(InputStream inputStream)
{
    try
    {
        Configuration.PROPS.load(inputStream);
 
    }
    catch (IOException e)
    {
        e.printStackTrace();
    }
    return new DefaultSqlSessionFactory(new Configuration());
}
最后一句默认sqlSession工厂中扫描mapper.xml将解析后的节点信息存到configuration中。

下面的loadMappersInfo方法中使用dom4j解析xml。

public DefaultSqlSessionFactory(Configuration configuration)
{
    this.configuration = configuration;
    loadMappersInfo(Configuration.getProperty(Constant.MAPPER_LOCATION).replaceAll("\\.", "/"));
}
 
private void loadMappersInfo(String dirName)
{
    //...
    XmlUtil.readMapperXml(file, this.configuration);
    //...
}
 
/** XmlUtil类 **/
public static void readMapperXml(File fileName, Configuration configuration)
{
    //解析xml步骤省略
    //...
    //设置SQL的唯一ID
    String sqlId = namespace + "." + element.attributeValue(Constant.XML_ELEMENT_ID); 
    
    for (Iterator iterator = rootElement.elementIterator(); iterator.hasNext();)
    {
        //...
        statement.setSqlId(sqlId);
        statement.setNamespace(namespace);
        statement.setSql(CommonUtis.stringTrim(element.getStringValue()));
        statements.add(statement);
    
        System.out.println(statement);
        configuration.addMappedStatement(sqlId, statement);
    
        //这里其实是在MapperRegistry中生产一个mapper对应的代理工厂
        configuration.addMapper(Class.forName(namespace));
    }
}
 
/** MapperRegistry类 **/
public <T> void addMapper(Class<T> type)
{
    this.knownMappers.put(type, new MapperProxyFactory<T>(type)); //这里生成了代理工厂
}
记住上面代码的最后两句设置MappedStatement和Mapper，后面有用。

 

SqlSession session = factory.openSession();

解释：把上面解析的configuration放在DefaultSqlSession中备用。从下面代码中可以看出，configuration一直传递到执行器里。

public SqlSession openSession()
{
    SqlSession session = new DefaultSqlSession(this.configuration);
    return session;
}
 
public DefaultSqlSession(Configuration configuration)
{
    this.configuration = configuration;
    this.executor = new SimpleExecutor(configuration);
 
}
 
public SimpleExecutor(Configuration configuration)
{
    this.conf = configuration;
}
UserMapper userMapper = session.getMapper(UserMapper.class);

解释：重点来了，这里开始从session中获取mapper，真正的还是代理工厂返回了mapper实例，获取过程看下面的代码分析。

/** DefaultSqlSession类 **/
@Override
public <T> T getMapper(Class<T> type) 
{
    return configuration.<T> getMapper(type, this);
}
 
/** Configuration类 **/
public <T> T getMapper(Class<T> type, SqlSession sqlSession) 
{
  return this.mapperRegistry.getMapper(type, sqlSession);
}
 
/** MapperRegistry类 **/
public <T> T getMapper(Class<T> type, SqlSession sqlSession)
{
    //前面解析xml做了addMapper操作，这里通过mapper类获取代理工厂
    MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>)this.knownMappers.get(type);
  
    return mapperProxyFactory.newInstance(sqlSession);
}
 
/** MapperProxyFactory类 **/
public T newInstance(SqlSession sqlSession)
{
    MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, this.mapperInterface);
    return newInstance(mapperProxy);
}
 
/**
 * 根据mapper代理返回实例
 * 
 * @param mapperProxy
 * @return 代理实例
 */
protected T newInstance(MapperProxy<T> mapperProxy)
{
    return (T)Proxy.newProxyInstance(this.mapperInterface.getClassLoader(), new Class[] {this.mapperInterface}, mapperProxy);
}
User user = userMapper.getUser("1");

解释：mapper中具体方法的功能，都是代理的invoke完成的。MapperProxy都实现了InvocationHandler接口。

public class MapperProxy<T> implements InvocationHandler, Serializable
｛
    //...
    public Object invoke(Object proxy, Method method, Object[] args)
    {
        //...
        return  this.execute(method, args);
    }
 
    private Object execute(Method method,  Object[] args)
    {
        String statementId = this.mapperInterface.getName() + "." + method.getName();
        MappedStatement ms = this.sqlSession.getConfiguration().getMappedStatement(statementId);
        
        Object result = null;
        switch (ms.getSqlCommandType())
        {
            case SELECT:
            {
                Class<?> returnType = method.getReturnType();
                
                // 如果返回的是list，应该调用查询多个结果的方法，否则只要查单条记录
                if (Collection.class.isAssignableFrom(returnType))
                {
                    //ID为mapper类全名+方法名
                    result = sqlSession.selectList(statementId, args);
                }
                else
                {
                    result = sqlSession.selectOne(statementId, args);
                }
                break;
            }
            //...
        }   
        return result;
    }
}
我们查询一条记录，所以使用sqlSession.selectOne方法。代码实现如下

/**  DefaultSqlSession类 **/
public <T> T selectOne(String statementId, Object parameter)
{
    //这里跟源码保持一致，源码也是调用selectList，返回第一个结果的
    List<T> results = this.<T> selectList(statementId, parameter);
    return CommonUtis.isNotEmpty(results) ? results.get(0) : null;
}
 
public <E> List<E> selectList(String statementId, Object parameter)
{
    MappedStatement mappedStatement = this.configuration.getMappedStatement(statementId);
    return this.executor.<E> doQuery(mappedStatement, parameter); //调用执行器
}
执行器最终调用了原生的JDBC操作数据库

/** SimpleExecutor类 **/
public <E> List<E> doQuery(MappedStatement ms, Object parameter)
{
    try
    {
        //1.获取数据库连接
        Connection connection = getConnect();
        
        //2.获取MappedStatement信息，里面有sql信息
        MappedStatement mappedStatement = conf.getMappedStatement(ms.getSqlId());
        
        //3.实例化StatementHandler对象，
        StatementHandler statementHandler = new SimpleStatementHandler(mappedStatement);
        
        //4.通过StatementHandler和connection获取PreparedStatement
        PreparedStatement preparedStatement = statementHandler.prepare(connection);
        
        //5.实例化ParameterHandler，将SQL语句中？参数化
        ParameterHandler parameterHandler = new DefaultParameterHandler(parameter);
        parameterHandler.setParameters(preparedStatement);
        
        //6.执行SQL，得到结果集ResultSet
        ResultSet resultSet = statementHandler.query(preparedStatement);
        
        //7.实例化ResultSetHandler，通过反射将ResultSet中结果设置到目标resultType对象中
        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(mappedStatement);
        return resultSetHandler.handleResultSets(resultSet);
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    return null;
}
执行器中我注释的很详细，大家都能明白，这里就不继续粘贴里面的代码了。想看全部的代码，请到github上下载。

现在代码写完了，我们测试以下结果：



整个mybatis精简版框架的写作流程结束，希望您看完可以有所收获，谢谢！
--------------------- 
作者：kuailebuzhidao 
来源：CSDN 
原文：https://blog.csdn.net/kuailebuzhidao/article/details/88355236 
版权声明：本文为博主原创文章，转载请附上博文链接！


![欢迎大家关注公众号，有资料有技术文章](http://img.zqzhaopin.cn/java_qr.jpg)
