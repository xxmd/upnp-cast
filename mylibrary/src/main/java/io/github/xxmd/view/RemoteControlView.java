package io.github.xxmd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.apache.commons.io.FilenameUtils;

import java.util.concurrent.TimeUnit;

import io.github.xxmd.CastViewModel;
import io.github.xxmd.databinding.ViewRemoteControlBinding;
import io.github.xxmd.entity.TransportState;
import io.github.xxmd.jetty.JettyServerManager;
import io.github.xxmd.service.UpnpServiceHelper;
import io.github.xxmd.util.ContextUtil;
import io.github.xxmd.util.TimeUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RemoteControlView extends ConstraintLayout {
    private final AppCompatActivity appCompatActivity;
    private CastViewModel castViewModel;
    private ViewRemoteControlBinding binding;
    private Disposable interval;

    public RemoteControlView( Context context,  AttributeSet attrs) {
        super(context, attrs);
        appCompatActivity = ContextUtil.parseContextToAppCompatActivity(context);
        initView();
        initData();
    }

    private void bindEvent() {
        castViewModel.hasPlayedTime.observe(appCompatActivity, new Observer<Long>() {
            @Override
            public void onChanged(Long timeValue) {

                Long totalTime = castViewModel.totalTime.getValue();
                TransportState value = castViewModel.transportState.getValue();
                boolean hasStopped = value.equals(TransportState.STOPPED);

                binding.icForward15.setVisibility(!hasStopped && timeValue < totalTime ? VISIBLE : GONE);
                binding.icForward15Disable.setVisibility(hasStopped || timeValue >= totalTime ? VISIBLE : GONE);

                binding.icBackward15.setVisibility(totalTime != 0 && !hasStopped && timeValue > 0 ? VISIBLE : GONE);
                binding.icBackward15Disable.setVisibility(totalTime == 0 || hasStopped || timeValue == 0 ? VISIBLE : GONE);
            }
        });

        castViewModel.curFileIndex.observe(appCompatActivity, new Observer<Integer>() {
            @Override
            public void onChanged(Integer index) {
                updateIconState();
                setUriThenPlay();
            }
        });

        castViewModel.transportState.observe(appCompatActivity, new Observer<TransportState>() {
            @Override
            public void onChanged(TransportState playState) {
                updateIconState();

                binding.icLoading.setVisibility(playState.equals(TransportState.TRANSITIONING) ? VISIBLE : GONE);
                binding.icPlaying.setVisibility(playState.equals(TransportState.PLAYING) ? VISIBLE : GONE);
                binding.icPaused.setVisibility(playState.equals(TransportState.PAUSED) || playState.equals(TransportState.STOPPED) ? VISIBLE : GONE);
            }
        });

        binding.icForward15.setOnClickListener(view -> {
            forward15Second();
        });

        binding.icBackward15.setOnClickListener(view -> {
            backward15Second();
        });

        binding.icNext.setOnClickListener(view -> next());
        binding.icPre.setOnClickListener(view -> previous());

        binding.icPlaying.setOnClickListener(view -> {
            pause();
        });

        binding.icPaused.setOnClickListener(view -> {
            if (castViewModel.hasPlayedTime.getValue() > 0) {
                play();
            } else {
                setUriThenPlay();
            }
        });
    }

    private void updateIconState() {
        Integer index = castViewModel.curFileIndex.getValue();
        int size = castViewModel.filePathList.size();
        TransportState value = castViewModel.transportState.getValue();
        boolean hasStopped = value.equals(TransportState.STOPPED);

        binding.icPre.setVisibility(!hasStopped && size > 0 && index > 0 ? VISIBLE : GONE);
        binding.icPreDisable.setVisibility(hasStopped || size == 0 || index == 0 ? VISIBLE : GONE);

        binding.icNext.setVisibility(!hasStopped && size > 0 && index < size - 1 ? VISIBLE : GONE);
        binding.icNextDisable.setVisibility(hasStopped || size == 0 || index == size - 1 ? VISIBLE : GONE);
    }

    private void previous() {
        castViewModel.curFileIndex.setValue(castViewModel.curFileIndex.getValue() - 1);
    }

    private void next() {
        castViewModel.curFileIndex.setValue(castViewModel.curFileIndex.getValue() + 1);
    }

    /**
     * 这个 setNextAVTransportURI 不好处理多个播放列表
     * @param curIndex
     */
    private void setNextAVTransportURI(int curIndex) {
        if (curIndex + 1 <= castViewModel.filePathList.size() - 1) {
            String nextFilePath = castViewModel.filePathList.get(curIndex + 1);
            String nextCastUri = JettyServerManager.getCastUri(appCompatActivity, nextFilePath);
            UpnpServiceHelper.setNextAVTransportURI(castViewModel.upnpService, castViewModel.selectedDevice, nextCastUri);
        }
    }

    private void backward15Second() {
        long backwardTime = castViewModel.hasPlayedTime.getValue() - 15 * 1000;
        if (backwardTime < 0) {
            backwardTime = 0;
        }
        String timeStr = TimeUtil.getTimeStr(backwardTime, false);
        UpnpServiceHelper.seek(castViewModel.upnpService, castViewModel.selectedDevice, timeStr);
    }

    private void forward15Second() {
        long forwardTime = castViewModel.hasPlayedTime.getValue() + 15 * 1000;
        if (forwardTime > castViewModel.totalTime.getValue()) {
            forwardTime = castViewModel.totalTime.getValue();
        }
        String timeStr = TimeUtil.getTimeStr(forwardTime, false);
        UpnpServiceHelper.seek(castViewModel.upnpService, castViewModel.selectedDevice, timeStr);
    }

    private void play() {
        UpnpServiceHelper.play(castViewModel.upnpService, castViewModel.selectedDevice, actionInvocation -> {
            castViewModel.transportState.setValue(TransportState.PLAYING);
        });
    }

    private void pause() {
        UpnpServiceHelper.pause(castViewModel.upnpService, castViewModel.selectedDevice);
    }

    private void setUriThenPlay() {
        String castUri = JettyServerManager.getCastUri(appCompatActivity, castViewModel.curFilePath.getValue());
        castViewModel.transportState.setValue(TransportState.TRANSITIONING);
        UpnpServiceHelper.setAVTransportURI(castViewModel.upnpService, castViewModel.selectedDevice, castUri, actionInvocation -> {
            play();
            monitorTransportInfo();
//            monitorMediaInfo();
        });
        deviceCheck();
    }

    private void deviceCheck() {
        String firstFilePath = castViewModel.filePathList.get(0);
        String extension = FilenameUtils.getExtension(firstFilePath);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (mimeType.contains("audio")) {
            Toast.makeText(getContext(), "警告：缺少音频输出的设备无法投屏音频文件", Toast.LENGTH_LONG).show();
        }
    }

    /**
     *
     */
    private void monitorMediaInfo() {
        Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Throwable {
                    UpnpServiceHelper.getMediaInfo(castViewModel.upnpService, castViewModel.selectedDevice, actionInvocation -> {
//                        String currentURI = actionInvocation.getOutput("CurrentURI").toString();
//                        String filePath = JettyServerManager.parseCastUri(currentURI);
//                        int curIndex = castViewModel.filePathList.indexOf(filePath);
//                        if (curIndex >= 0 && castViewModel.curFileIndex.getValue() != curIndex) {
//                            appCompatActivity.runOnUiThread(() -> {
//                                castViewModel.curFileIndex.setValue(curIndex);
//                            });
//                        }
//                        String nextURI = actionInvocation.getOutput("NextURI").toString();
//                        if (curIndex != castViewModel.filePathList.size() -1 && (StringUtils.isEmpty(nextURI) || !castViewModel.filePathList.contains(JettyServerManager.parseCastUri(nextURI)))) {
//                            setNextAVTransportURI(curIndex);
//                        }
                    });
                }
            });
    }

    @Override
    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        UpnpServiceHelper.stop(castViewModel.upnpService, castViewModel.selectedDevice);
    }

    private void monitorTransportInfo() {
        interval = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        UpnpServiceHelper.getTransportInfo(castViewModel.upnpService, castViewModel.selectedDevice, actionInvocation -> {
                            String transportState = actionInvocation.getOutput("CurrentTransportState").toString();
                            appCompatActivity.runOnUiThread(() -> {
                                switch (transportState) {
                                    case "TRANSITIONING":
                                        castViewModel.transportState.setValue(TransportState.TRANSITIONING);
                                        break;
                                    case "PLAYING":
                                        castViewModel.transportState.setValue(TransportState.PLAYING);
                                        break;
                                    case "PAUSED_PLAYBACK":
                                        castViewModel.transportState.setValue(TransportState.PAUSED);
                                        break;
                                    case "STOPPED":
                                        castViewModel.transportState.setValue(TransportState.STOPPED);
                                        break;

                                }
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
        binding = ViewRemoteControlBinding.inflate(LayoutInflater.from(appCompatActivity), this, true);
    }
}
