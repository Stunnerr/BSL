package com.stunner.moderstars.signer.apksigner;

import android.util.Log;

import com.stunner.moderstars.UsefulThings;

import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

public class Main {

    public static void main(String... args) throws Exception {
        String keystorePath = args[0];
        String inputFile = args[1];
        String outputFile = args[2];

        char[] keyPassword = "android".toCharArray();

        File keystoreFile = new File(keystorePath);
        if (!keystoreFile.exists()) {
            String alias = "alias";
            System.out.println("Creating new keystore (using '" + new String(keyPassword) + "' as password and '"
                    + alias + "' as the key alias).");
            CertCreator.DistinguishedNameValues nameValues = new CertCreator.DistinguishedNameValues();
            nameValues.setCommonName("APK Signer");
            nameValues.setOrganization("Earth");
            nameValues.setOrganizationalUnit("Earth");
            CertCreator.createKeystoreAndKey(keystorePath, keyPassword, "RSA", 2048, alias, keyPassword, "SHA1withRSA",
                    30, nameValues);
        }

        KeyStore keyStore = KeyStoreFileManager.loadKeyStore(keystorePath, null);
        String alias = keyStore.aliases().nextElement();

        X509Certificate publicKey = (X509Certificate) keyStore.getCertificate(alias);
        try {
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPassword);
            ZipSigner.signZip(publicKey, privateKey, "SHA1withRSA", inputFile, outputFile, UsefulThings.checked);
        } catch (UnrecoverableKeyException e) {
            Log.e("BSL.Signing: ", e.getMessage());

        }
    }

}
