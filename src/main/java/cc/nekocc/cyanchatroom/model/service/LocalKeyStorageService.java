package cc.nekocc.cyanchatroom.model.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

/**
 * 负责在本地文件系统上安全存储和读取加密密钥的服务
 */
public class LocalKeyStorageService
{
    private final Path base_storage_path_;

    public LocalKeyStorageService()
    {
        this.base_storage_path_ = Paths.get(System.getProperty("user.home"), ".cyanchatroom", "keys");
        try
        {
            Files.createDirectories(base_storage_path_);
        } catch (IOException e)
        {
            throw new RuntimeException("无法创建密钥存储根目录", e);
        }
    }

    public void saveKeyPair(String server_address, UUID user_id, KeyPair key_pair) throws IOException
    {
        Path user_key_path = getUserKeyPath(server_address, user_id);
        Files.createDirectories(user_key_path);

        byte[] public_key_bytes = key_pair.getPublic().getEncoded();
        byte[] private_key_bytes = key_pair.getPrivate().getEncoded();

        Files.writeString(user_key_path.resolve("identity_pub.key"), Base64.getEncoder().encodeToString(public_key_bytes));
        Files.writeString(user_key_path.resolve("identity_priv.key"), Base64.getEncoder().encodeToString(private_key_bytes));
    }

    public KeyPair loadKeyPair(String server_address, UUID user_id) throws Exception
    {
        Path user_key_path = getUserKeyPath(server_address, user_id);
        Path public_key_file = user_key_path.resolve("identity_pub.key");
        Path private_key_file = user_key_path.resolve("identity_priv.key");

        if (!Files.exists(public_key_file) || !Files.exists(private_key_file))
        {
            return null;
        }

        byte[] public_key_bytes = Base64.getDecoder().decode(Files.readString(public_key_file));
        byte[] private_key_bytes = Base64.getDecoder().decode(Files.readString(private_key_file));

        KeyFactory key_factory = KeyFactory.getInstance("EC");
        PublicKey public_key = key_factory.generatePublic(new X509EncodedKeySpec(public_key_bytes));
        PrivateKey private_key = key_factory.generatePrivate(new PKCS8EncodedKeySpec(private_key_bytes));

        return new KeyPair(public_key, private_key);
    }

    private Path getUserKeyPath(String server_address, UUID user_id)
    {
        String safe_server_name = server_address.replaceAll("[:/\\\\?%*|\"<>]", "_");
        return base_storage_path_.resolve(safe_server_name).resolve(user_id.toString());
    }
}