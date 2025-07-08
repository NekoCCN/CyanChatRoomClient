package cc.nekocc.cyanchatroom.model.impl;

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
    {
    }

    /**
     * 在应用启动时调用, 用于初始化SqlSessionFactory并创建数据库表。
     */
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
                e.printStackTrace();
                throw new RuntimeException("Fatal Error: Failed to initialize MyBatis.", e);
            }
        }
    }

    /**
     * 初始化数据库表结构。
     * 为前端聊天记录存储进行优化。
     */
    private static void initializeDatabaseSchema()
    {
        try (SqlSession session = sql_session_factory_.openSession())
        {
            session.update("""
                    CREATE TABLE IF NOT EXISTS conversations (
                        id TEXT PRIMARY KEY,
                        type TEXT NOT NULL,
                        last_message_time TEXT NOT NULL
                    );""");

            session.update("""
                    CREATE TABLE IF NOT EXISTS messages (
                        id TEXT PRIMARY KEY,
                        conversation_id TEXT NOT NULL,
                        type TEXT NOT NULL,
                        content TEXT NOT NULL,
                        created_at TEXT NOT NULL,
                        FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE
                    );""");

            // 复合索引 优化按会话ID和时间查询消息的性能
            session.update("CREATE INDEX IF NOT EXISTS idx_messages_conv_time ON messages (conversation_id, created_at);");

            session.commit();
            System.out.println("Success: Database schema initialized successfully.");

        } catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Fatal Error: Failed to initialize database schema.", e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory()
    {
        if (sql_session_factory_ == null)
        {
            init();
        }
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