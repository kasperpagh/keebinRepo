package utils.security;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by kaspe on 27-02-2017.
 */

public class CertificateTrustHandler
{
    static SSLContext sslContext;

    public static void trustCertificate(InputStream stream, Context context) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException
    {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");


        InputStream is = new BufferedInputStream(stream);

        Certificate ca;
        try
        {

            ca = cf.generateCertificate(is);
            Log.d("done med parse af cert ", "ca=" + ((X509Certificate) ca).getSubjectDN());
        }
        finally
        {
            Log.d("kunne ikke parse", " certificat");
            is.close();
        }

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);


        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
    }

}
