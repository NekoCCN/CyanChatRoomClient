package cc.nekocc.cyanchatroom.model.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class HikariDataSourceFactory extends UnpooledDataSourceFactory
{
    /**
     * HikariDataSourceFactory 构造函数。
     * 该构造函数会加载 hikari.properties 配置文件，并使用其中的配置创建 HikariDataSource 实例。
     * 数据库文件存储在用户主目录下的 .cyanchatroom 目录中，文件名为 database.db。
     * 因此！！！ hikari.properties 内的路径配置毫无意义！！！
     */
    public HikariDataSourceFactory()
    {
        Properties hikari_properties = new Properties();
        try
        {
            hikari_properties.load(getClass().getClassLoader().getResourceAsStream("hikari.properties"));
        }
        catch (Exception e)
        {
            throw new RuntimeException("无法加载 hikari.properties 配置文件", e);
        }

        Path db_path = Paths.get(System.getProperty("user.home"), ".cyanchatroom", "database.db");

        if (db_path.getParent() == null || !db_path.getParent().toFile().exists())
        {
            try
            {
                if (!db_path.getParent().toFile().mkdirs())
                {
                    throw new RuntimeException("无法创建数据库目录: " + db_path.getParent());
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("无法创建数据库目录: " + db_path.getParent(), e);
            }
        }

        hikari_properties.setProperty("jdbcUrl", "jdbc:sqlite:" + db_path.toAbsolutePath());

        HikariConfig config = new HikariConfig(hikari_properties);
        this.dataSource = new HikariDataSource(config);
    }
}