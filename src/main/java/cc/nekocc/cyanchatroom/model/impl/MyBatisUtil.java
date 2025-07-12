package cc.nekocc.cyanchatroom.model.impl;

import cc.nekocc.cyanchatroom.model.mapper.SchemaMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;

public final class MyBatisUtil
{
    private static SqlSessionFactory sql_session_factory_;

    private MyBatisUtil()
    {  }

    public static void init()
    {
        if (sql_session_factory_ == null)
        {
            try
            {
                String resource = "mybatis-config.xml";
                InputStream inputStream = Resources.getResourceAsStream(resource);
                sql_session_factory_ = new SqlSessionFactoryBuilder().build(inputStream);
                initializeDatabaseSchema();
            } catch (IOException e)
            {
                throw new RuntimeException("Fatal Error: Failed to initialize MyBatis.", e);
            }
        }
    }

    private static void initializeDatabaseSchema()
    {
        try (SqlSession session = sql_session_factory_.openSession())
        {
            SchemaMapper schemaMapper = session.getMapper(SchemaMapper.class);

            schemaMapper.createConversationsTable();
            schemaMapper.createMessagesTable();
            schemaMapper.createMessagesIndex();

            session.commit();
            System.out.println("数据库表结构初始化成功。");
        } catch (Exception e)
        {
            throw new RuntimeException("Fatal Error: Failed to initialize database schema.", e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory()
    {
        if (sql_session_factory_ == null) init();
        return sql_session_factory_;
    }

    public static <R> R executeQuery(Function<SqlSession, R> func)
    {
        try (SqlSession session = getSqlSessionFactory().openSession())
        {
            return func.apply(session);
        }
    }

    public static void executeUpdate(Consumer<SqlSession> action)
    {
        try (SqlSession session = getSqlSessionFactory().openSession())
        {
            action.accept(session);
            session.commit();
        }
    }
}
