package com.view9.sockettest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    private TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[] {};
        }

        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }
    } };

    public static class RelaxedHostNameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        IO.setDefaultSSLContext(sc);
        HttpsURLConnection.setDefaultHostnameVerifier(new RelaxedHostNameVerifier());

        try {
            mSocket = IO.socket("https://socket.mypadlokt.com:8080");

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Log.d("ActivityName: ", "socket connected");
                // emit anything you want here to the server
                mSocket.emit("addusertoevent", "uat2466");
            }

        }).on("notify", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                        try {
                            JSONArray data = (JSONArray) args[0];
                            System.out.print("data:" + data.toString());
                        } catch (Exception e) {
                            //return;
                        }
                }

        }).on("playAds",  new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        // this argas[0] can have any type you send from the server
                            // do something
                            try {
                                JSONArray data = (JSONArray) args[0];
                                System.out.print("data:" + data.toString());
                            } catch (Exception e) {
                               // return;
                            }
                    }
        }).on(Socket.EVENT_DISCONNECT,  new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        Log.d("ActivityName: ", "socket disconnected");
                    }

                });
        mSocket.connect();

    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            mSocket.emit("addusertoevent", "uat2466");

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject data = (JSONObject) args[0];

                    } catch (Exception e) {
                        return;
                    }


                }
            });

        }


    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject data = (JSONObject) args[0];
                        System.out.println("data:"+data);
                    } catch (Exception e) {
                        return;
                    }


                }
            });

        }


    };
}
