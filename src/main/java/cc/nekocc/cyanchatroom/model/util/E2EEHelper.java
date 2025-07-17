package cc.nekocc.cyanchatroom.model.util;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;

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
     * 执行ECDH密钥交换, 并使用HKDF派生出共享密钥
     */
    public static SecretKey deriveSharedSecret(PrivateKey private_key, PublicKey public_key) throws Exception
    {
        KeyAgreement ka = KeyAgreement.getInstance("ECDH", "BC");
        ka.init(private_key);
        ka.doPhase(public_key, true);
        byte[] shared_secret = ka.generateSecret();

        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(new SHA256Digest());

        // 初始化HKDF
        hkdf.init(new HKDFParameters(shared_secret, null, "cyanchatroom-aes-gcm-key".getBytes(StandardCharsets.UTF_8)));

        byte[] derivedKey = new byte[32];
        hkdf.generateBytes(derivedKey, 0, 32);

        return new SecretKeySpec(derivedKey, "AES");
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

        byte[] cipherTextWithTag = cipher.doFinal(plain_text.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedPayload = new byte[iv.length + cipherTextWithTag.length];
        System.arraycopy(iv, 0, encryptedPayload, 0, iv.length);
        System.arraycopy(cipherTextWithTag, 0, encryptedPayload, iv.length, cipherTextWithTag.length);

        return Base64.getEncoder().encodeToString(encryptedPayload);
    }

    /**
     * 使用AES-GCM解密消息
     */
    public static String decrypt(String base64_encrypted, SecretKey key) throws Exception
    {
        byte[] encryptedPayload = Base64.getDecoder().decode(base64_encrypted);

        byte[] iv = new byte[12];
        System.arraycopy(encryptedPayload, 0, iv, 0, iv.length);

        byte[] cipherTextWithTag = new byte[encryptedPayload.length - iv.length];
        System.arraycopy(encryptedPayload, iv.length, cipherTextWithTag, 0, cipherTextWithTag.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        GCMParameterSpec gcm_spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcm_spec);

        byte[] decrypted_text = cipher.doFinal(cipherTextWithTag);
        return new String(decrypted_text, StandardCharsets.UTF_8);
    }

    public static PublicKey publicKeyFromBase64(String base64_public_key) throws Exception
    {
        byte[] public_key_bytes = Base64.getDecoder().decode(base64_public_key);
        KeyFactory key_factory = KeyFactory.getInstance("EC", "BC");
        return key_factory.generatePublic(new X509EncodedKeySpec(public_key_bytes));
    }
}
