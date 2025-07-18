package cc.nekocc.cyanchatroom.model;

public class GlobalConfig
{
    private int max_config_history_message_size = 1000;
    private boolean enable_shift_enter_hotkey = true;

    private int heartbeat_interval = 5000;

    private int reconnect_interval = 1000;

    private int max_reconnect_attempts = 4;
}
