package cc.nekocc.cyanchatroom.model.impl;

import cc.nekocc.cyanchatroom.model.mapper.SchemaMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public final class MyBatisUtil
{
    private static final Map<String, SqlSessionFactory> session_factories_ = new ConcurrentHashMap<>();
    private static Configuration base_configuration_;

    private MyBatisUtil()
    {  }

    /**
     * 根据服务器地址和用户ID获取对应的SqlSessionFactory。
     * 这是与数据库交互的入口。
     */
    public static SqlSessionFactory getSessionFactory(String server_address, UUID user_id)
    {
        if (server_address == null || user_id == null)
        {
            throw new IllegalArgumentException("服务器地址和用户ID不能为空。");
        }
        String cache_key = server_address + ":" + user_id.toString();
        return session_factories_.computeIfAbsent(cache_key, key -> createNewSessionFactory(server_address, user_id));
    }

    private static SqlSessionFactory createNewSessionFactory(String server_address, UUID user_id)
    {
        try
        {
            String resource = "mybatis-config.xml";
            InputStream inputStream = MyBatisUtil.class.getClassLoader().getResourceAsStream(resource);
            XMLConfigBuilder xml_config_builder = new XMLConfigBuilder(inputStream, null, null);
            Configuration configuration = xml_config_builder.parse();

            DataSource data_source = createHikariDataSource(server_address, user_id);
            TransactionFactory transaction_factory = new JdbcTransactionFactory();
            Environment environment = new Environment("dynamic-" + server_address, transaction_factory, data_source);

            configuration.setEnvironment(environment);

            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);

            initializeDatabaseSchema(factory);

            System.out.println("已为服务器 " + server_address + " 成功初始化数据库。");
            return factory;

        } catch (Exception e)
        {
            throw new RuntimeException("为服务器 " + server_address + " 创建SqlSessionFactory失败", e);
        }
    }

    private static DataSource createHikariDataSource(String server_address, UUID user_id)
    {
        try
        {
            Properties hikari_properties = new Properties();
            hikari_properties.load(MyBatisUtil.class.getClassLoader().getResourceAsStream("hikari.properties"));

            String safe_server_name = server_address.replaceAll("[:/\\\\?%*|\"<>]", "_");
            String db_filename = user_id.toString() + ".db";
            Path db_path = Paths.get(System.getProperty("user.home"), ".cyanchatroom", safe_server_name, db_filename);

            Files.createDirectories(db_path.getParent());
            hikari_properties.setProperty("jdbcUrl", "jdbc:sqlite:" + db_path.toAbsolutePath());

            HikariConfig config = new HikariConfig(hikari_properties);
            return new HikariDataSource(config);
        } catch (Exception e)
        {
            throw new RuntimeException("创建HikariDataSource失败", e);
        }
    }

    private static void initializeDatabaseSchema(SqlSessionFactory sql_session_factory)
    {
        try (SqlSession session = sql_session_factory.openSession())
        {
            SchemaMapper schemaMapper = session.getMapper(SchemaMapper.class);
            schemaMapper.createConversationsTable();
            schemaMapper.createMessagesTable();
            schemaMapper.createMessagesIndex();
            session.commit();
        } catch (Exception e)
        {
            throw new RuntimeException("初始化数据库表结构失败", e);
        }
    }

    public static <R> R executeQuery(String server_address, UUID user_id, Function<SqlSession, R> func)
    {
        try (SqlSession session = getSessionFactory(server_address, user_id).openSession())
        {
            return func.apply(session);
        }
    }

    public static void executeUpdate(String server_address, UUID user_id, Consumer<SqlSession> action)
    {
        try (SqlSession session = getSessionFactory(server_address, user_id).openSession())
        {
            action.accept(session);
            session.commit();
        }
    }
}
