package com.profewgames.provotifier.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class RSA {
	public static byte[] encrypt(final byte[] data, final PublicKey key) throws Exception {
		final Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(1, key);
		return cipher.doFinal(data);
	}

	public static byte[] decrypt(final byte[] data, final PrivateKey key) throws Exception {
		final Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(2, key);
		return cipher.doFinal(data);
	}
}
