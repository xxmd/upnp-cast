package io.github.xxmd;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.tbruyelle.rxpermissions3.RxPermissions;

import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.xxmd.databinding.ActivityMainBinding;
import io.github.xxmd.util.GlideEngine;


public class MainActivity extends AppCompatActivity implements SearchDeviceDialog.SearchDeviceListener {
    private ActivityMainBinding binding;
    private RxPermissions rxPermissions;
    private Device selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        initView();
        bindEvent();
    }

    private void initView() {
        handleShowContainer();
    }

    private void initData() {
        rxPermissions = new RxPermissions(this);
    }

    private void bindEvent() {
        binding.btnSearch.setOnClickListener(v -> {
            requestPermission(() -> openSearchDeviceDialog());
        });

        binding.btnImage.setOnClickListener(v -> {
            doPermissionGranted(() -> chooseFileThenCast(SelectMimeType.ofImage()));
        });

        binding.btnVideo.setOnClickListener(v -> {
            doPermissionGranted(() -> chooseFileThenCast(SelectMimeType.ofVideo()));
        });

        binding.btnAudio.setOnClickListener(v -> {
            doPermissionGranted(() -> chooseFileThenCast(SelectMimeType.ofAudio()));
        });
    }

    private void requestPermission(Runnable runnable) {
        rxPermissions.request(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        runnable.run();
                    } else {
                    }
                });
    }

    private void openSearchDeviceDialog() {
        SearchDeviceDialog searchDeviceDialog = new SearchDeviceDialog(selectedDevice);
        searchDeviceDialog.setSearchDeviceListener(this);
        searchDeviceDialog.show(getSupportFragmentManager());
    }

    private void handleShowContainer() {
        binding.castContainer.setVisibility(selectedDevice == null ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onConfirm(Device device) {
        if (device != null) {
            selectedDevice = device;
        }
        handleShowContainer();
    }

    private void doPermissionGranted(Runnable runnable) {
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        runnable.run();
                    } else {
                    }
                });
    }

    private void chooseFileThenCast(int mediaType) {
        PictureSelector.create(this)
                .openGallery(mediaType)
                .setImageEngine(GlideEngine.createGlideEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            List<String> collect = result.stream().map(it -> it.getRealPath()).collect(Collectors.toList());
                            CastActivity.startCastActivity(MainActivity.this, selectedDevice, collect);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    @Override
    public void onCancel() {
    }
}