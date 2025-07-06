package cc.nekocc.cyanchatroom.model;

import cc.nekocc.cyanchatroom.domain.client.AbstractClient;
import cc.nekocc.cyanchatroom.domain.client.CorporationClient;
import cc.nekocc.cyanchatroom.domain.client.IndividualClient;
import cc.nekocc.cyanchatroom.domain.goods.AbstractGoods;
import cc.nekocc.cyanchatroom.domain.goods.DangerousGoods;
import cc.nekocc.cyanchatroom.domain.goods.ExpediteGoods;
import cc.nekocc.cyanchatroom.domain.goods.NormalGoods;
import cc.nekocc.cyanchatroom.domain.order.Order;
import cc.nekocc.cyanchatroom.util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class OrderRepository
{
    private final Path file_path_ = Paths.get(System.getProperty("user.home"), ".my-air-cargo-app", "orders.json");
    private List<Order> orders_;
    private final Gson gson_;

    public OrderRepository()
    {
        RuntimeTypeAdapterFactory<AbstractClient> client_adapter_factory = RuntimeTypeAdapterFactory
                .of(AbstractClient.class, "type_")
                .registerSubtype(IndividualClient.class)
                .registerSubtype(CorporationClient.class);

        RuntimeTypeAdapterFactory<AbstractGoods> goods_adapter_factory = RuntimeTypeAdapterFactory
                .of(AbstractGoods.class, "type_")
                .registerSubtype(NormalGoods.class)
                .registerSubtype(ExpediteGoods.class)
                .registerSubtype(DangerousGoods.class);

        gson_ = new GsonBuilder()
                .registerTypeAdapterFactory(client_adapter_factory)
                .registerTypeAdapterFactory(goods_adapter_factory)
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();

        load();
    }

    public void load()
    {
        if (Files.exists(file_path_))
        {
            try (Reader reader = Files.newBufferedReader(file_path_))
            {
                Type order_list_type = new TypeToken<ArrayList<Order>>(){}.getType();
                orders_ = gson_.fromJson(reader, order_list_type);
                if (orders_ == null)
                {
                    orders_ = new ArrayList<>();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                orders_ = new ArrayList<>();
            }
        }
        else
        {
            orders_ = new ArrayList<>();
        }
    }

    public synchronized void save()
    {
        try
        {
            Path parent_dir = file_path_.getParent();
            if (Files.notExists(parent_dir))
            {
                Files.createDirectories(parent_dir);
            }
            try (Writer writer = Files.newBufferedWriter(file_path_))
            {
                gson_.toJson(orders_, writer);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addOrder(Order order)
    {
        orders_.add(order);
        save();
    }

    public Optional<Order> findById(String orderId)
    {
        return orders_.stream().filter(o -> o.getOrderId().equals(orderId)).findFirst();
    }

    public Stream<Order> findByClient(String username)
    {
        return orders_.stream().filter(o -> o.getSender().getName().equals(username));
    }

    public List<Order> getAllOrders()
    {
        return orders_;
    }

    public void updateOrder(Order updatedOrder)
    {
        Optional<Order> existing_order_opt = findById(updatedOrder.getOrderId());
        if(existing_order_opt.isPresent())
        {
            int index = orders_.indexOf(existing_order_opt.get());
            orders_.set(index, updatedOrder);
            save();
        }
    }
}