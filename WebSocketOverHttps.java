try {

    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    InputStream caInput = new BufferedInputStream(getAssets().open("xxx.crt"));
    Certificate ca;
    try {
        ca = cf.generateCertificate(caInput);
        Log.i(TAG, "ca=" + ((X509Certificate) ca).getSubjectDN());
        Log.i(TAG, "key=" + ((X509Certificate) ca).getPublicKey());
    } finally {
        caInput.close();
    }

    // Create a KeyStore containing our trusted CAs
    String keyStoreType = KeyStore.getDefaultType();
    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
    keyStore.load(null, null);
    keyStore.setCertificateEntry("ca", ca);

    // Create a TrustManager that trusts the CAs in our KeyStore
    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
    tmf.init(keyStore);

    // Create an SSLContext that uses our TrustManager
    SSLContext sslContext = SSLContext.getInstance("TLSv1","AndroidOpenSSL");
    sslContext.init(null, tmf.getTrustManagers(), null);
    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

    client.setSocket(sslSocketFactory.createSocket());
} catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | NoSuchProviderException | KeyManagementException e) {
    e.printStackTrace();
}