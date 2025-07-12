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
    private String server_url_;
    private final Consumer<String> on_message_callback_;
    private final Runnable on_open_callback_;
    private final Runnable on_close_callback_;
    private final Runnable on_reconnecting_callback_;
    private Timer heartbeat_timer_;
    private Timer reconnect_timer_;

    private boolean auto_reconnect_ = false;
    private int reconnect_attempts_ = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 10;
    private static final long INITIAL_RECONNECT_INTERVAL = 5000;
    private static final long MAX_RECONNECT_INTERVAL = 60000;

    public NetworkService(Consumer<String> on_message_callback, Runnable on_open_callback, Runnable on_close_callback, Runnable on_reconnecting_callback)
    {
        this.on_message_callback_ = on_message_callback;
        this.on_open_callback_ = on_open_callback;
        this.on_close_callback_ = on_close_callback;
        this.on_reconnecting_callback_ = on_reconnecting_callback;
    }

    public void connect(String server_url)
    {
        this.server_url_ = server_url;
        this.auto_reconnect_ = true;
        this.reconnect_attempts_ = 0;
        if (client_ != null && client_.isOpen())
        {
            client_.close();
        }
        createAndConnectClient();
    }

    private void createAndConnectClient()
    {
        try
        {
            URI server_uri = new URI(server_url_);
            client_ = new WebSocketClient(server_uri)
            {
                @Override
                public void onOpen(ServerHandshake d)
                {
                    System.out.println("WebSocket connection opened.");
                    reconnect_attempts_ = 0; // Reset on successful connection
                    stopReconnectTimer();
                    on_open_callback_.run();
                    startHeartbeat();
                }

                @Override
                public void onMessage(String m)
                {
                    if ("pong".equalsIgnoreCase(m))
                        return;

                    on_message_callback_.accept(m);
                }

                @Override
                public void onClose(int code, String reason, boolean remote)
                {
                    System.out.println("WebSocket closed: " + reason + " (code: " + code + ")");
                    stopHeartbeat();
                    on_close_callback_.run();
                    if (auto_reconnect_)
                    {
                        scheduleReconnect();
                    }
                }

                @Override
                public void onError(Exception e)
                {
                    System.err.println("WebSocket error: " + e.getMessage());
                }
            };
            System.out.println("Attempting to connect to " + server_url_);
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
        } else
        {
            System.err.println("Cannot send message, client is not connected.");
        }
    }

    public void disconnect()
    {
        auto_reconnect_ = false;
        stopReconnectTimer();
        if (client_ != null)
        {
            client_.close();
        }
        stopHeartbeat();
    }

    private void scheduleReconnect()
    {
        if (reconnect_attempts_ >= MAX_RECONNECT_ATTEMPTS)
        {
            System.err.println("Max reconnect attempts reached. Giving up.");
            auto_reconnect_ = false;
            return;
        }

        if (reconnect_timer_ != null)
        {
            return;
        }

        // Exponential backoff
        long delay = (long) (INITIAL_RECONNECT_INTERVAL * Math.pow(2, reconnect_attempts_));
        delay = Math.min(delay, MAX_RECONNECT_INTERVAL);

        reconnect_attempts_++;
        System.out.println("Scheduling reconnect attempt " + reconnect_attempts_ + " in " + delay + "ms.");

        Platform.runLater(on_reconnecting_callback_);

        reconnect_timer_ = new Timer("ReconnectTimer", true);
        reconnect_timer_.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                createAndConnectClient();
                reconnect_timer_ = null;
            }
        }, delay);
    }

    private void stopReconnectTimer()
    {
        if (reconnect_timer_ != null)
        {
            reconnect_timer_.cancel();
            reconnect_timer_ = null;
        }
    }


    private void startHeartbeat()
    {
        stopHeartbeat();
        heartbeat_timer_ = new Timer("HeartbeatTimer", true);
        heartbeat_timer_.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (client_ != null && client_.isOpen())
                {
                    try
                    {
                        client_.sendPing();
                        System.out.println("Sent ping.");
                    } catch (Exception e)
                    {
                        System.err.println("Error sending ping: " + e.getMessage());
                    }
                }
            }
        }, 0, 30000);
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
