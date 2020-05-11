package com.profewgames.provotifier.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.bind.DatatypeConverter;

public class RSAIO {
	public static void save(final File directory, final KeyPair keyPair) throws Exception {
		final PrivateKey privateKey = keyPair.getPrivate();
		final PublicKey publicKey = keyPair.getPublic();
		final X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey.getEncoded());
		FileOutputStream out = new FileOutputStream(directory + "/public.key");
		out.write(DatatypeConverter.printBase64Binary(publicSpec.getEncoded()).getBytes());
		out.close();
		final PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		out = new FileOutputStream(directory + "/private.key");
		out.write(DatatypeConverter.printBase64Binary(privateSpec.getEncoded()).getBytes());
		out.close();
	}

	public static KeyPair load(final File directory) throws Exception {
		final File publicKeyFile = new File(directory + "/public.key");
		FileInputStream in = new FileInputStream(directory + "/public.key");
		byte[] encodedPublicKey = new byte[(int) publicKeyFile.length()];
		in.read(encodedPublicKey);
		encodedPublicKey = DatatypeConverter.parseBase64Binary(new String(encodedPublicKey));
		in.close();
		final File privateKeyFile = new File(directory + "/private.key");
		in = new FileInputStream(directory + "/private.key");
		byte[] encodedPrivateKey = new byte[(int) privateKeyFile.length()];
		in.read(encodedPrivateKey);
		encodedPrivateKey = DatatypeConverter.parseBase64Binary(new String(encodedPrivateKey));
		in.close();
		final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		final X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		final PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		final PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		final PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		return new KeyPair(publicKey, privateKey);
	}
}
