package com.killzone.testapp.network

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Deferred
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*

private const val BASE_URL = "https://demo.bankplus.ru/"

/*
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
*/

/*private val client = OkHttpClient.Builder()
    .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS))
    .build()*/

private val gson = GsonBuilder().create()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(BASE_URL)
    //.client(UnsafeOkHttpClient.getUnsafeClient().build())
    .build()
interface NetworkApiService {

    @FormUrlEncoded
    @POST("mobws/json/pointList")
    suspend fun getProperties(
        @Query("count") c: Int = 3,
        @Query("version") v: Double = 1.1,
        @Field("version") version: Double = 1.1
    ): Response<Coordinates>
}


object NetworkApi {
    val retrofitService : NetworkApiService by lazy { retrofit.create(NetworkApiService::class.java) }
}

class UnsafeOkHttpClient {
    companion object {
        fun getUnsafeClient(): OkHttpClient.Builder {
            try {
                val trustAllCerts = arrayOf<TrustManager>(object: X509TrustManager {

                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?,
                        authType: String?) {}

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?,
                        authType: String?) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                })

                val sslContext: SSLContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
                val builder: OkHttpClient.Builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier {_, _ -> true}
                return builder

            } catch (e: Exception) {
                throw RuntimeException(e)

            }

        }
    }
}


/*
private val unsafeOkHttpClient: OkHttpClient
    // Install the all-trusting trust manager
    // Create an ssl socket factory with our all-trusting manager
    private get() = try { // Create a trust manager that does not validate certificate chains
        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    //return arrayOf(X509Certificate(""))
                    return CertificateFactory.getInstance("X.509").

                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }


            }
        )
        // Install the all-trusting trust manager
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext.getSocketFactory()
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory)
        builder.hostnameVerifier(object : HostnameVerifier() {
            override fun verify(hostname: String?, session: SSLSession?): Boolean {
                return true
            }
        })
        builder.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
*/


/*

public class NetworkHandler {

    public static Retrofit getRetrofit() {

        return new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient())
                .build();
    }


    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

The syntax has changed a little since Hitesh Sahu's answer was posted. Now you can use lambdas for some of the methods, remove some throw clauses and chain builder method invocations.

private static OkHttpClient createOkHttpClient() {
    try {
        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier((hostname, session) -> true)
                .build();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}


@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    try{
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(getSSLSocketFactory())
                .hostnameVerifier(getHostnameVerifier())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        WebAPIService service = retrofit.create(WebAPIService.class);

        Call<JsonObject> jsonObjectCall = service.getData(...);
        ...
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// for SSL...    
// Read more at https://developer.android.com/training/articles/security-ssl.html#CommonHostnameProbs
private HostnameVerifier getHostnameVerifier() {
    return new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
            //HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
            //return hv.verify("localhost", session);
        }
    };
}        

private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
    final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
    return new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return originalTrustManager.getAcceptedIssuers();
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    try {
                        if (certs != null && certs.length > 0){
                            certs[0].checkValidity();
                        } else {
                            originalTrustManager.checkClientTrusted(certs, authType);
                        }
                    } catch (CertificateException e) {
                        Log.w("checkClientTrusted", e.toString());
                    }
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    try {
                        if (certs != null && certs.length > 0){
                            certs[0].checkValidity();
                        } else {
                            originalTrustManager.checkServerTrusted(certs, authType);
                        }
                    } catch (CertificateException e) {
                        Log.w("checkServerTrusted", e.toString());
                    }
                }
            }
    };
}

private SSLSocketFactory getSSLSocketFactory()
        throws CertificateException, KeyStoreException, IOException,
        NoSuchAlgorithmException, KeyManagementException {
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    InputStream caInput = getResources().openRawResource(R.raw.your_cert); // File path: app\src\main\res\raw\your_cert.cer
    Certificate ca = cf.generateCertificate(caInput);
    caInput.close();
    KeyStore keyStore = KeyStore.getInstance("BKS");
    keyStore.load(null, null);
    keyStore.setCertificateEntry("ca", ca);
    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
    tmf.init(keyStore);
    TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, wrappedTrustManagers, null);
    return sslContext.getSocketFactory();
}



final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                   }
                }
        };

        // Install the all-trusting trust manager

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.interceptors().add(httpLoggingInterceptor);
        client.readTimeout(180, TimeUnit.SECONDS);
        client.connectTimeout(180, TimeUnit.SECONDS);

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
       keyStore.load(null, null);

        SSLContext sslContext = SSLContext.getInstance("TLS");

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
       trustManagerFactory.init(keyStore);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "keystore_pass".toCharArray());
        sslContext.init(null, trustAllCerts, new SecureRandom());
        client.sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
               });

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Common.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.build())
                .build();

        serviceApi = retrofit.create(Api.class);


onnectionSpec spec = new 
ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(      
CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,                    
CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();

OkHttpClient client = new OkHttpClient.Builder()
            .connectionSpecs(Collections.singletonList(spec))
            .build();


*/
















