package cc.nekocc.cyanchatroom.model.service;

import javafx.application.Platform;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class NetworkService
{
    private WebSocketClient client_;
    private final Consumer<String> on_message_callback_;
    private final Runnable on_open_callback_;
    private final Runnable on_close_callback_;
    private Timer heartbeat_timer_;

    public NetworkService(Consumer<String> on_message_callback, Runnable on_open_callback, Runnable on_close_callback)
    {
        this.on_message_callback_ = on_message_callback;
        this.on_open_callback_ = on_open_callback;
        this.on_close_callback_ = on_close_callback;
    }

    public void connect(String server_url)
    {
        if (client_ != null && client_.isOpen())
        {
            client_.close();
        }
        try
        {
            URI server_uri = new URI(server_url);
            client_ = new WebSocketClient(server_uri)
            {
                @Override
                public void onOpen(ServerHandshake d)
                {
                    Platform.runLater(on_open_callback_);
                    startHeartbeat();
                }

                @Override
                public void onMessage(String m)
                {
                    Platform.runLater(() -> on_message_callback_.accept(m));
                }

                @Override
                public void onClose(int c, String r, boolean re)
                {
                    stopHeartbeat();
                    Platform.runLater(on_close_callback_);
                }

                @Override
                public void onError(Exception e)
                {
                    e.printStackTrace();
                    Platform.runLater(on_close_callback_);
                }
            };
            client_.connect();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message)
    {
        if (client_ != null && client_.isOpen())
        {
            client_.send(message);
        }
    }

    public void disconnect()
    {
        if (client_ != null)
        {
            client_.close();
        }
        stopHeartbeat();
    }

    private void startHeartbeat()
    {
        heartbeat_timer_ = new Timer(true);
        heartbeat_timer_.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (client_ != null && client_.isOpen())
                {
                    client_.sendPing();
                }
            }
        }, 0, 45000);
    }

    private void stopHeartbeat()
    {
        if (heartbeat_timer_ != null)
        {
            heartbeat_timer_.cancel();
            heartbeat_timer_ = null;
        }
    }
}