package com.example.networkandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.networkandroid.entity.Wrapper;
import com.example.networkandroid.utils.GsonUtil;
import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private Button networkButton;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkButton = findViewById(R.id.network_activity);
        networkButton.setOnClickListener((view -> {
            Observable<Wrapper> objectObservable = getWrapperObservable();
            showUserInfo(objectObservable);
        }));
    }

    private void showUserInfo(Observable<Wrapper> objectObservable) {
        objectObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Wrapper>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Wrapper wrapper) {
                        if (!disposable.isDisposed()) {
                            if (wrapper.getData().size() > 0) {
                                Toast.makeText(getApplicationContext(), wrapper.getData().get(0).getName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!disposable.isDisposed()) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private Observable<Wrapper> getWrapperObservable() {
        String url = "https://twc-android-bootcamp.github.io/fake-data/data/default.json";
        return Observable.create(item -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request
                    .Builder()
                    .url(url)
                    .build();
            Response response = okHttpClient.newCall(request)
                    .execute();
            String data = response.body().string();
            Wrapper wrapper = GsonUtil.fromToJson(data, Wrapper.class);
            item.onNext(wrapper);
            item.onComplete();
        });
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }
}