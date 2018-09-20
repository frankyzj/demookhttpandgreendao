package com.prient.okhttoandgreendao;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okio.BufferedSink;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView mTextMessage;
    private Button SynGet, AsynGet, AccessingHeaders, PostStr;
    private Button PostStream, PostMultiPartReq, PostFile, PostFormPara;
    private Button Caching;

    private final OkHttpClient client = new OkHttpClient();
    private String url = "https://publicobject.com/helloworld.txt";
    private String headUrl = "https://api.github.com/repos/square/okhttp/issues";
    private String postStrUrl = "https://api.github.com/markdown/raw";
    private String formUrl = "https://en.wikipedia.org/w/index.php";
    private String mutiUrl = "https://api.imgur.com/oauth2";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBtn();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void initBtn() {
        SynGet = findViewById(R.id.SynGet);
        AsynGet = findViewById(R.id.AsynGet);
        AccessingHeaders = findViewById(R.id.AccessingHeaders);
        PostStr = findViewById(R.id.PostStr);
        PostStream = findViewById(R.id.PostStream);
        PostMultiPartReq = findViewById(R.id.PostMultiPartReq);
        PostFile = findViewById(R.id.PostFile);
        PostFormPara = findViewById(R.id.PostFormPara);
        Caching = findViewById(R.id.Caching);

        SynGet.setOnClickListener(this);
        AsynGet.setOnClickListener(this);
        AccessingHeaders.setOnClickListener(this);
        PostStr.setOnClickListener(this);
        PostStream.setOnClickListener(this);
        PostMultiPartReq.setOnClickListener(this);
        PostFile.setOnClickListener(this);
        PostFormPara.setOnClickListener(this);
        Caching.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SynGet:
                new Thread() {
                    @Override
                    public void run() {

                        try {
                            final String str = synGet();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTextMessage.setText(str);

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                break;
            case R.id.AsynGet:
                asynGet();
                break;
            case R.id.AccessingHeaders:
                accessingHeaders();
                break;
            case R.id.PostStr:
                postStr();
                break;
            case R.id.PostStream:
                postStream();
                break;
            case R.id.PostMultiPartReq:
                postMultiPartReq();
                break;
            case R.id.PostFile:
                int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (PackageManager.PERMISSION_GRANTED == permission) {

                    postFile();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // TODO: 2018/9/20 在此处提示该权限的重要性
                    }
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                break;
            case R.id.PostFormPara:
                postFormPara();
                break;
            case R.id.Caching:
                caching();
                break;
        }
    }

    private void caching() {
        int cacheSize = 10 * 1024 * 1024; //10 MB
        Cache cache = new Cache(getExternalCacheDir(), cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        final Request request = new Request.Builder()
                .addHeader("Cache-Control", "max-age=9600")
                .cacheControl(CacheControl.FORCE_CACHE)
                .url("http://publicobject.com/helloworld.txt")
                .build();
        final String[] responseBody = new String[1];
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    responseBody[0] = response.body().string();
                    Log.d(TAG, "onResponse: " + responseBody[0]);
                }
            }
        });
        final String[] responseBody1 = new String[1];
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    responseBody1[0] = response.body().string();
                    Log.d(TAG, "onResponse: " + responseBody1[0]);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextMessage.setText(String.valueOf(responseBody[0].equals(responseBody1[0])));
                        }
                    });
                }
            }
        });
    }

    private void postFormPara() {
        RequestBody formBody = new FormBody.Builder()
                .add("search", "dalai")
                .build();
        final Request request = new Request.Builder()
                .url(formUrl)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Spanned spanned = Html.fromHtml(response.body().string());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextMessage.setText(spanned);
                    }
                });
            }
        });

    }

    private void postFile() {
        File file1 = Environment.getExternalStorageDirectory();
        Log.d(TAG, "postFile: " + file1.getPath());
        File file = new File("/storage/emulated/0/README.md");

        Request request = new Request.Builder()
                .url(postStrUrl)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Spanned spanned = Html.fromHtml(str);
                        mTextMessage.setText(spanned);
                    }
                });
            }
        });
    }

    /**
     * The imgur client ID for OkHttp recipes. If you're using imgur for anything other than running
     * these examples, please request your own client ID! https://api.imgur.com/oauth2
     */
    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private void postMultiPartReq() {
        // Use the imgur image upload API as documented at
        // https://api.imgur.com/endpoints/image
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                .addFormDataPart("image", "logo-square.png",
                        RequestBody.create(MEDIA_TYPE_PNG,
                                new File("/storage/emulated/0/taxIMP/1.PNG")))
                .build();
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID" + IMGUR_CLIENT_ID)
                .url(mutiUrl)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onResponse: 出错了");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: 进来了");

                if (response.isSuccessful()) {
                    final String str = response.body().string();
                    Log.d(TAG, "onResponse: " + str);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextMessage.setText(str);
                        }
                    });
                }
            }
        });

    }

    private void postStream() {
        RequestBody requestBody = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE_MARKDOWN;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("Number\n");
                sink.writeUtf8("------\n");
                for (int i = 2; i < 997; i++) {
                    sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
                }
            }

            private String factor(int n) {
                for (int i = 2; i < n; i++) {
                    int x = n / i;
                    if (x * i == n) return factor(x) + " x " + i;
                }
                return Integer.toString(n);
            }
        };
        final Request request = new Request.Builder()
                .url(postStrUrl)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String str = response.body().string();//在主线程执行会抛出 networkonuithread 异常
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Spanned spanned = Html.fromHtml(str);
                            mTextMessage.setText(spanned);
                        }
                    });
                } else {
                    throw new IOException("Unexpect code " + response);
                }
            }
        });
    }

    private int responseCount(Response response){
        int result = 1;
        while ((response = response.priorResponse()) != null){
            result++;
        }
        return result;
    }

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    private void postStr() {

        OkHttpClient client = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Nullable
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        if (response.request().header("Authorization") != null) {

                            return null;
                        }

                        String credential = Credentials.basic("jesse", "password1");
                        //防止多次重试
                        if (credential.equals(response.request().header("Authorization"))) {
                            return null; // If we already failed with these credentials, don't retry.
                        }
                        // 设置重试次数
                        if(responseCount(response) >= 3){
                            return null;
                        }
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .build();
                    }
                }).build();

        String postBody = ""
                + "Releases\n"
                + "--------\n"
                + "\n"
                + " * _1.0_ May 6, 2013\n"
                + " * _1.1_ June 15, 2013\n"
                + " * _1.2_ August 11, 2013\n";
        Request request = new Request.Builder()
                .url(postStrUrl)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String str = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Spanned spanned = Html.fromHtml(str);
                            mTextMessage.setText(spanned);
                        }
                    });
                } else {
                    throw new IOException("Unexpected code" + response);
                }
            }
        });

    }

    private void accessingHeaders() {
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS,
//                        ConnectionSpec.COMPATIBLE_TLS))
//                .build();
        final Request request = new Request.Builder()
                .url(headUrl)
                .header("User-Agent", "OkHttp Headers.java") //替换原有的Header
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextMessage.setText("Server:" + response.header("Server")
                                    + ";\n" + "Date:" + response.header("Date")
                                    + ";\n" + "Vary:" + response.header("Vary"));
                        }
                    });
                } else {
                    throw new IOException("Unexpected code" + response);
                }
            }
        });
    }

    private void asynGet() {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        Request request = new Request.Builder()
                .url(url)
                .build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mTextMessage.setText(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    throw new IOException("Unexpected code" + response);
                }
            }
        });
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                call.cancel();
            }
        }, 1, TimeUnit.SECONDS);
    }

    private String synGet() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    String postJson(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    postFile();
                } else {

                }
                break;
        }
    }
}
