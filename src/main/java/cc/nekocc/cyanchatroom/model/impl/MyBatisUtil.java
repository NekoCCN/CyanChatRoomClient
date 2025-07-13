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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public final class MyBatisUtil
{
    private static final Map<String, SqlSessionFactory> session_factories_ = new ConcurrentHashMap<>();

    private MyBatisUtil()
    {
    }

    public static SqlSessionFactory getSessionFactoryForServer(String server_address)
    {
        return session_factories_.computeIfAbsent(server_address, MyBatisUtil::createNewSessionFactory);
    }

    private static SqlSessionFactory createNewSessionFactory(String server_address)
    {
        try
        {
            String resource = "mybatis-config.xml";
            InputStream inputStream = MyBatisUtil.class.getClassLoader().getResourceAsStream(resource);
            XMLConfigBuilder xml_config_builder = new XMLConfigBuilder(inputStream, null, null);
            Configuration configuration = xml_config_builder.parse();

            DataSource data_source = createHikariDataSource(server_address);
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

    private static DataSource createHikariDataSource(String server_address)
    {
        try
        {
            Properties hikari_properties = new Properties();
            hikari_properties.load(MyBatisUtil.class.getClassLoader().getResourceAsStream("hikari.properties"));

            String safe_filename = server_address.replaceAll("[:/\\\\?%*|\"<>]", "_") + ".db";
            Path db_path = Paths.get(System.getProperty("user.home"), ".cyanchatroom", safe_filename);

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

    public static <R> R executeQuery(String server_address, Function<SqlSession, R> func)
    {
        try (SqlSession session = getSessionFactoryForServer(server_address).openSession())
        {
            return func.apply(session);
        }
    }

    public static void executeUpdate(String server_address, Consumer<SqlSession> action)
    {
        try (SqlSession session = getSessionFactoryForServer(server_address).openSession())
        {
            action.accept(session);
            session.commit();
        }
    }
}
