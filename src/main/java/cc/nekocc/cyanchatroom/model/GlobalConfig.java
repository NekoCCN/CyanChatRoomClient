package cc.nekocc.cyanchatroom.model;

import cc.nekocc.cyanchatroom.model.util.JsonUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GlobalConfig
{
    private int max_config_history_message_size = 1000;
    private boolean enable_shift_enter_hotkey = true;
    private String theme = "LIGHT";

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
                this.theme = config.theme;
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
    public String getTheme()
    {
        return theme;
    }
    public void setTheme(String theme)
    {
        this.theme = theme;
    }
}
