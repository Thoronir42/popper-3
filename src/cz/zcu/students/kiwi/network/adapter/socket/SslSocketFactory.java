package cz.zcu.students.kiwi.network.adapter.socket;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.security.*;
import java.security.cert.CertificateException;

public class SslSocketFactory extends SocketFactory {

    private final SSLContext sc;

    public SslSocketFactory(SSLContext sc) {
        this.sc = sc;
    }

    @Override
    public SSLSocket create(SocketAddress server, int timeout) throws IOException {
        SSLSocketFactory ssf = sc.getSocketFactory();
        SSLSocket s = (SSLSocket) ssf.createSocket();
        s.connect(server, timeout);
//        s.startHandshake();

        return s;
    }

    public static SSLContext createSslContext(String file, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyManagementException {
        KeyStore ks = KeyStore.getInstance("JKS");


        ks.load(new FileInputStream(file), password.toCharArray());
//        ks.load(new FileInputStream(keyStoreFile), "keystorePassword".toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
        tmf.init(ks);

        SSLContext sc = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = tmf.getTrustManagers();
        sc.init(kmf.getKeyManagers(), trustManagers, null);

        return sc;
    }
}
