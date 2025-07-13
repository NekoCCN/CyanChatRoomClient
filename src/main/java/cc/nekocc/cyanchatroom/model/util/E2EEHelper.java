package cc.nekocc.cyanchatroom.model.util;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class E2EEHelper
{
    static
    {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 生成一对基于Curve25519的密钥对
     */
    public static KeyPair generateKeyPair() throws Exception
    {
        KeyPairGenerator key_gen = KeyPairGenerator.getInstance("EC", "BC");
        key_gen.initialize(new ECGenParameterSpec("curve25519"));
        return key_gen.generateKeyPair();
    }

    /**
     * 执行ECDH密钥交换, 计算共享密钥
     */
    public static SecretKey deriveSharedSecret(PrivateKey private_key, PublicKey public_key) throws Exception
    {
        KeyAgreement ka = KeyAgreement.getInstance("ECDH", "BC");
        ka.init(private_key);
        ka.doPhase(public_key, true);
        byte[] shared_secret = ka.generateSecret();
        // 使用共享密钥的前32字节作为AES-256的密钥
        return new SecretKeySpec(shared_secret, 0, 32, "AES");
    }

    /**
     * 使用AES-GCM加密消息
     */
    public static String encrypt(String plain_text, SecretKey key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec gcm_spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcm_spec);

        byte[] cipher_text = cipher.doFinal(plain_text.getBytes(StandardCharsets.UTF_8));
        byte[] encrypted_payload = new byte[iv.length + cipher_text.length];

        System.arraycopy(iv, 0, encrypted_payload, 0, iv.length);
        System.arraycopy(cipher_text, 0, encrypted_payload, iv.length, cipher_text.length);
        return Base64.getEncoder().encodeToString(encrypted_payload);
    }

    /**
     * 使用AES-GCM解密消息
     */
    public static String decrypt(String base64_encrypted, SecretKey key) throws Exception
    {
        byte[] encrypted_payload = Base64.getDecoder().decode(base64_encrypted);

        byte[] iv = new byte[12];
        byte[] cipher_text = new byte[encrypted_payload.length - 12];

        System.arraycopy(encrypted_payload, 0, iv, 0, iv.length);
        System.arraycopy(encrypted_payload, iv.length, cipher_text, 0, cipher_text.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        GCMParameterSpec gcm_spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcm_spec);
        byte[] decrypted_text = cipher.doFinal(cipher_text);
        return new String(decrypted_text, "UTF-8");
    }

    public static PublicKey publicKeyFromBase64(String base64_public_key) throws Exception
    {
        byte[] public_key_bytes = Base64.getDecoder().decode(base64_public_key);
        KeyFactory key_factory = KeyFactory.getInstance("EC", "BC");
        return key_factory.generatePublic(new X509EncodedKeySpec(public_key_bytes));
    }
}
