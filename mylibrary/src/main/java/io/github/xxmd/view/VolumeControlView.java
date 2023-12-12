package io.github.xxmd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import java.util.concurrent.TimeUnit;

import io.github.xxmd.CastViewModel;
import io.github.xxmd.databinding.ViewVolumeControlBinding;
import io.github.xxmd.entity.TransportState;
import io.github.xxmd.service.UpnpServiceHelper;
import io.github.xxmd.util.ContextUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

public class VolumeControlView extends ConstraintLayout {
    private final AppCompatActivity appCompatActivity;
    private CastViewModel castViewModel;
    private ViewVolumeControlBinding binding;
    private Disposable volumeInterval;
    private Disposable muteInterval;

    public VolumeControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        appCompatActivity = ContextUtil.parseContextToAppCompatActivity(context);
        initView();
        initData();
    }

    private void bindEvent() {
        castViewModel.isMuted.observe(appCompatActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.icVolume.setVisibility(!aBoolean ? VISIBLE : GONE);
                binding.icMute.setVisibility(aBoolean ? VISIBLE : GONE);
            }
        });

        castViewModel.curVolume.observe(appCompatActivity, new Observer<Integer>() {
            @Override
            public void onChanged(Integer volumeValue) {
                binding.icVolumeAdd.setVisibility(volumeValue < 100 ? VISIBLE : GONE);
                binding.icVolumeAddDisable.setVisibility(volumeValue == 100 ? VISIBLE : GONE);
                binding.icVolumeMinus.setVisibility(volumeValue > 0 ? VISIBLE : GONE);
                binding.icVolumeMinusDisable.setVisibility(volumeValue == 0 ? VISIBLE : GONE);

                binding.progressBar.setProgress(volumeValue);
            }
        });

        castViewModel.transportState.observe(appCompatActivity, new Observer<TransportState>() {
            @Override
            public void onChanged(TransportState playState) {
                switch (playState) {
                    case PLAYING:
                        if (muteInterval == null) {
                            monitorMuteInfo();
                        }
                        if (volumeInterval == null) {
                            monitorVolumeInfo();
                        }
                        break;
                }
            }
        });

        binding.icVolume.setOnClickListener(view -> setMute(true));
        binding.icMute.setOnClickListener(view -> setMute(false));
        binding.icVolumeAdd.setOnClickListener(view -> addVolume());
        binding.icVolumeMinus.setOnClickListener(view -> minusVolume());
    }

    private void monitorMuteInfo() {
        muteInterval = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        UpnpServiceHelper.getMute(castViewModel.upnpService, castViewModel.selectedDevice, actionInvocation -> {
                            String currentMute = actionInvocation.getOutput("CurrentMute").toString();
                            appCompatActivity.runOnUiThread(() -> {
                                castViewModel.isMuted.setValue(currentMute.equals("1"));
                            });
                        });
                    }
                });
    }

    private void setMute(boolean isMuted) {
        UpnpServiceHelper.setMute(castViewModel.upnpService, castViewModel.selectedDevice, isMuted ? "1" : "0");
    }

    private void minusVolume() {
        int desiredValue = castViewModel.curVolume.getValue() - 10;
        if (desiredValue < 0) {
            desiredValue = 0;
        }
        UpnpServiceHelper.setVolume(castViewModel.upnpService, castViewModel.selectedDevice, String.valueOf(desiredValue));
    }

    private void addVolume() {
        int desiredValue = castViewModel.curVolume.getValue() + 10;
        if (desiredValue > 100) {
            desiredValue = 100;
        }
        UpnpServiceHelper.setVolume(castViewModel.upnpService, castViewModel.selectedDevice, String.valueOf(desiredValue));
    }

    private void monitorVolumeInfo() {
        volumeInterval = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        UpnpServiceHelper.getVolume(castViewModel.upnpService, castViewModel.selectedDevice, actionInvocation -> {
                            String currentVolume = actionInvocation.getOutput("CurrentVolume").toString();
                            int volumeValue = Integer.parseInt(currentVolume); // WindowsMediaPlayer 的最大音量是 100，最小是 0
                            appCompatActivity.runOnUiThread(() -> {
                                castViewModel.curVolume.setValue(volumeValue);
                            });
                        });
                    }
                });
    }

    private void initData() {
        binding.getRoot().post(() -> {
            castViewModel = new ViewModelProvider(appCompatActivity).get(CastViewModel.class);
            bindEvent();
        });
    }

    private void initView() {
        binding = ViewVolumeControlBinding.inflate(LayoutInflater.from(appCompatActivity), this, true);
    }
}
