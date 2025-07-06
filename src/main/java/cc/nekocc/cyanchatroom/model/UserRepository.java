package cc.nekocc.cyanchatroom.model;

import cc.nekocc.cyanchatroom.domain.User;
import cc.nekocc.cyanchatroom.domain.client.AbstractClient;
import cc.nekocc.cyanchatroom.domain.client.CorporationClient;
import cc.nekocc.cyanchatroom.domain.client.IndividualClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.mindrot.jbcrypt.BCrypt;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserRepository
{
    private final Path file_path_ = Paths.get(System.getProperty("user.home"), ".my-air-cargo-app", "users.json");
    private List<User> users_;
    private final Gson gson_;

    public UserRepository()
    {
        RuntimeTypeAdapterFactory<AbstractClient> client_adapter_factory = RuntimeTypeAdapterFactory
                .of(AbstractClient.class, "type_")
                .registerSubtype(IndividualClient.class)
                .registerSubtype(CorporationClient.class);

        gson_ = new GsonBuilder()
                .registerTypeAdapterFactory(client_adapter_factory)
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
                Type userListType = new TypeToken<ArrayList<User>>()
                {  }.getType();
                users_ = gson_.fromJson(reader, userListType);
                if (users_ == null)
                {
                    users_ = new ArrayList<>();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                users_ = new ArrayList<>();
            }
        }
        else
        {
            users_ = new ArrayList<>();
        }
    }

    public boolean checkUserValidity(User user)
    {
        return user.getUsername() != null && user.getHashedPassword() != null && user.getClientData() != null
                && user.getClientData().getName() != null && user.getClientData().getClientId() != null && !Objects.equals(user.getUsername(), "");
    }

    public boolean login(String username, String password)
    {
        return findByUsername(username).isPresent() && verifyPassword(password, findByUsername(username).get().getHashedPassword());
    }

    public void registerUser(User user)
    {
        if (findByUsername(user.getUsername()).isPresent())
        {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (!checkUserValidity(user))
        {
            throw new IllegalArgumentException("用户信息不完整");
        }
        users_.add(user);
        save();
    }

    public void registerUser(String username, String password, AbstractClient client)
    {
        if (username == null || password == null || client == null || username.isEmpty() || password.isEmpty())
        {
            throw new IllegalArgumentException("用户信息不完整");
        }
        registerUser(new User(username, BCrypt.hashpw(password, BCrypt.gensalt()), client));
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
                gson_.toJson(users_, writer);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public Optional<User> findByUsername(String username)
    {
        return users_.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }

    public static boolean verifyPassword(String raw_password, String hashed_password)
    {
        if (raw_password == null || hashed_password == null)
        {
            return false;
        }
        return BCrypt.checkpw(raw_password, hashed_password);
    }
}