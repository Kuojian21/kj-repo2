package com.kj.repo.bean.crypt;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class KjCryptFactory {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static GenericObjectPoolConfig config() {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(10);
		config.setMaxTotal(100);
		config.setMaxWaitMillis(30000);
		return config;
	}

	public static SecretKey loadKey(String keyAlgorithm, String keyValue)
			throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {
		return SecretKeyFactory.getInstance(keyAlgorithm)
				.generateSecret(new DESedeKeySpec(keyValue.getBytes(Charset.forName("UTF-8"))));
	}

	public static IvParameterSpec loadIvp(String padding) {
		return new IvParameterSpec(padding.getBytes(Charset.forName("UTF-8")));
	}

	public static PublicKey loadPublicKey(String algorithm, byte[] key)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
		return KeyFactory.getInstance(algorithm).generatePublic(keySpec);
	}

	public static PrivateKey loadPrivateKey(String algorithm, byte[] key)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
		return KeyFactory.getInstance(algorithm).generatePrivate(keySpec);
	}

	public static SecretKey generateKey(String algorithm, Integer keysize, AlgorithmParameterSpec params)
			throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
		kgen.init(new SecureRandom());
		if (keysize != null) {
			kgen.init(keysize);
		}
		if (params != null) {
			kgen.init(params);
		}
		return kgen.generateKey();
	}

	public static KeyPair generateKeyPair(String algorithm, Integer keysize, AlgorithmParameterSpec params)
			throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		KeyPairGenerator kgen = KeyPairGenerator.getInstance(algorithm);
		if (keysize != null) {
			kgen.initialize(keysize, new SecureRandom());
		} else if (params != null) {
			kgen.initialize(params, new SecureRandom());
		}
		return kgen.generateKeyPair();
	}

	public static KjDecrypt kjDecrypt(String algorithm, String padding, String keyAlgorithm, String keyValue)
			throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {
		return new KjDecrypt(algorithm, loadKey(keyAlgorithm, keyValue), loadIvp(padding));
	}

	public static KjEncrypt kjEncrypt(String algorithm, String padding, String keyAlgorithm, String keyValue)
			throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {
		return new KjEncrypt(algorithm, loadKey(keyAlgorithm, keyValue), loadIvp(padding));
	}

	public static KjDecrypt kjDecryptByPublic(String algorithm, String padding, String keyAlgorithm, String keyValue)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return new KjDecrypt(algorithm, loadPublicKey(keyAlgorithm, keyValue.getBytes(Charset.forName("UTF-8"))),
				loadIvp(padding));
	}

	public static KjEncrypt kjEncryptByPublic(String algorithm, String padding, String keyAlgorithm, String keyValue)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return new KjEncrypt(algorithm, loadPublicKey(keyAlgorithm, keyValue.getBytes(Charset.forName("UTF-8"))),
				loadIvp(padding));
	}

	public static KjDecrypt kjDecryptByPrivate(String algorithm, String padding, String keyAlgorithm, String keyValue)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return new KjDecrypt(algorithm, loadPrivateKey(keyAlgorithm, keyValue.getBytes(Charset.forName("UTF-8"))),
				loadIvp(padding));
	}

	public static KjEncrypt kjEncryptByPrivate(String algorithm, String padding, String keyAlgorithm, String keyValue)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return new KjEncrypt(algorithm, loadPrivateKey(keyAlgorithm, keyValue.getBytes(Charset.forName("UTF-8"))),
				loadIvp(padding));
	}

}
