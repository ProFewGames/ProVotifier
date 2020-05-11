package com.profewgames.provotifier.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.logging.Logger;

public class RSAKeygen
{
    private static final Logger LOG;
    
    static {
        LOG = Logger.getLogger("ProVotifier");
    }
    
    public static KeyPair generate(final int bits) throws Exception {
        RSAKeygen.LOG.info("ProVotifier is generating an RSA key pair...");
        final KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        final RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(bits, RSAKeyGenParameterSpec.F4);
        keygen.initialize(spec);
        return keygen.generateKeyPair();
    }
}
