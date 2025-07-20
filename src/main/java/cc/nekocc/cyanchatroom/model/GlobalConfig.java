package cc.nekocc.cyanchatroom.model;

import cc.nekocc.cyanchatroom.model.util.JsonUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GlobalConfig
{
    private int max_config_history_message_size = 1000;
    private boolean enable_shift_enter_hotkey = true;

    private int heartbeat_interval = 5000;

    private int reconnect_interval = 1000;

    private int max_reconnect_attempts = 4;

    public Path getSafePath(String server_address, String user_id)
    {
        if (server_address == null || user_id == null)
        {
            throw new IllegalArgumentException("服务器地址和用户ID不能为空。");
        }

        Path json_path = null;

        try
        {
            String safe_server_name = server_address.replaceAll("[:/\\\\?%*|\"<>]", "_");
            String json_filename = user_id.toString() + ".json";
            json_path = Paths.get(System.getProperty("user.home"), ".cyanchatroom", safe_server_name, json_filename);

            Files.createDirectories(json_path.getParent());
        }
        catch (Exception e)
        {
            throw new RuntimeException("创建安全路径失败，使用默认配置文件：", e);
        }

        return json_path;
    }

    public void save(String server_address, String user_id)
    {
        try
        {
            Path config_path = getSafePath(server_address, user_id);
            String json_content = JsonUtil.serialize(this);
            Files.writeString(config_path, json_content);
        }
        catch (Exception e)
        {
            throw new RuntimeException("保存配置失败：", e);
        }
    }

    public void load(String server_address, String user_id)
    {
        try
        {
            var config_path = getSafePath(server_address, user_id);
            if (Files.exists(config_path))
            {
                String json_content = Files.readString(config_path);
                GlobalConfig config = JsonUtil.deserialize(json_content, GlobalConfig.class);
                this.max_config_history_message_size = config.max_config_history_message_size;
                this.enable_shift_enter_hotkey = config.enable_shift_enter_hotkey;
                this.heartbeat_interval = config.heartbeat_interval;
                this.reconnect_interval = config.reconnect_interval;
                this.max_reconnect_attempts = config.max_reconnect_attempts;
            }
            else
            {
                System.out.println("配置文件不存在，使用默认配置。");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("加载配置失败：", e);
        }
    }

    public static GlobalConfig loadConfig(String server_address, String user_id)
    {
        GlobalConfig config = new GlobalConfig();
        config.load(server_address, user_id);
        return config;
    }

    public int getMaxConfigHistoryMessageSize()
    {
        return max_config_history_message_size;
    }
    public void setMaxConfigHistoryMessageSize(int max_config_history_message_size)
    {
        this.max_config_history_message_size = max_config_history_message_size;
    }
    public boolean isEnableShiftEnterHotkey()
    {
        return enable_shift_enter_hotkey;
    }
    public void setEnableShiftEnterHotkey(boolean enable_shift_enter_hotkey)
    {
        this.enable_shift_enter_hotkey = enable_shift_enter_hotkey;
    }
    public int getHeartbeatInterval()
    {
        return heartbeat_interval;
    }
    public void setHeartbeatInterval(int heartbeat_interval)
    {
        this.heartbeat_interval = heartbeat_interval;
    }
    public int getReconnectInterval()
    {
        return reconnect_interval;
    }
    public void setReconnectInterval(int reconnect_interval)
    {
        this.reconnect_interval = reconnect_interval;
    }
    public int getMaxReconnectAttempts()
    {
        return max_reconnect_attempts;
    }
    public void setMaxReconnectAttempts(int max_reconnect_attempts)
    {
        this.max_reconnect_attempts = max_reconnect_attempts;
    }

}
