package io.github.xxmd;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import org.apache.commons.io.FilenameUtils;
import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.List;

import io.github.xxmd.databinding.ActivityCastBinding;
import io.github.xxmd.entity.TransportState;
import io.github.xxmd.jetty.JettyServerManager;

public class CastActivity extends AppCompatActivity {
    private ActivityCastBinding binding;
    public static final String EXTRA_FILE_PATH_LIST = "EXTRA_FILE_PATH_LIST";
    private static Device selectedDevice;
    private CastViewModel castViewModel;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JettyServerManager.stopSingleton();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityCastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        bindEvent();
    }

    public void initView() {
        initData();
        binding.tvDeviceName.setText(castViewModel.selectedDevice.getDetails().getFriendlyName());
        String firstFilePath = castViewModel.filePathList.get(0);
        binding.positionControlView.setVisibility(isImageFile(firstFilePath) ? View.GONE : View.VISIBLE);
        binding.volumeControlView.setVisibility(isImageFile(firstFilePath) ? View.GONE : View.VISIBLE);
    }

    public void bindEvent() {
        binding.icBack.setOnClickListener(view -> finish());

        castViewModel.curFilePath.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.tvFileName.setText(FilenameUtils.getName(s));
            }
        });

        castViewModel.transportState.observe(this, new Observer<TransportState>() {
            @Override
            public void onChanged(TransportState transportState) {
                switch (transportState) {
                    case TRANSITIONING:
                        binding.tvTransportState.setText("加载中");
                        break;
                    case PLAYING:
                        binding.tvTransportState.setText("播放中");
                        break;
                    case PAUSED:
                        binding.tvTransportState.setText("已暂停");
                        break;
                    case STOPPED:
                        binding.tvTransportState.setText("已停止");
                        break;
                }
            }
        });
    }

    public static boolean isImageFile(String filePath) {
        String extension = FilenameUtils.getExtension(filePath);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return mimeType.contains("image");
    }

    private void initData() {
        castViewModel = new ViewModelProvider(this).get(CastViewModel.class);
        castViewModel.selectedDevice = selectedDevice;
        connectUpnpService();
        JettyServerManager.runSingleton();
        castViewModel.filePathList = getIntent().getStringArrayListExtra(EXTRA_FILE_PATH_LIST);;
    }

    private void connectUpnpService() {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                castViewModel.upnpService = (AndroidUpnpService) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent = new Intent(this, AndroidUpnpServiceImpl.class);
        getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public static final void startCastActivity(Context context, Device device, List<String> filePathList) {
        Intent intent = new Intent(context, CastActivity.class);
        // 这个 Device 类没实现序列化接口，不能通过 intent.putExtra() 传递, 暂时用静态变量保存着
        selectedDevice = device;
        intent.putStringArrayListExtra(EXTRA_FILE_PATH_LIST, (ArrayList<String>) filePathList);
        context.startActivity(intent);
    }
}
