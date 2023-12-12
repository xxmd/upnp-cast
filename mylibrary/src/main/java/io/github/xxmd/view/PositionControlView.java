package io.github.xxmd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.TimeUnit;

import io.github.xxmd.CastViewModel;
import io.github.xxmd.databinding.ViewPositionControlBinding;
import io.github.xxmd.entity.TransportState;
import io.github.xxmd.service.UpnpServiceHelper;
import io.github.xxmd.util.ContextUtil;
import io.github.xxmd.util.TimeUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PositionControlView extends LinearLayout implements SeekBar.OnSeekBarChangeListener {
    private CastViewModel castViewModel;
    private AppCompatActivity appCompatActivity;
    private Disposable interval;
    private ViewPositionControlBinding binding;

    public PositionControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        appCompatActivity = ContextUtil.parseContextToAppCompatActivity(context);
        initView();
        initData();
    }

    private void bindEvent() {
        binding.seekBar.setOnSeekBarChangeListener(this);
        castViewModel.curFileIndex.observe(appCompatActivity, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                castViewModel.hasPlayedTime.setValue(0L);
            }
        });
        castViewModel.hasPlayedTime.observe(appCompatActivity, new Observer<Long>() {
            @Override
            public void onChanged(Long timeValue) {
                if (castViewModel.totalTime.getValue() != null) {
                    double curProgress = timeValue * 1.0 / castViewModel.totalTime.getValue() * 100;
                    binding.seekBar.setProgress((int) curProgress);
                    String timeStr = TimeUtil.getTimeStr(timeValue, true);
                    binding.tvHasPlayedTime.setText(timeStr);
                }
            }
        });

        castViewModel.totalTime.observe(appCompatActivity, new Observer<Long>() {
            @Override
            public void onChanged(Long timeValue) {
                String timeStr = TimeUtil.getTimeStr(timeValue, true);
                binding.tvTotalTime.setText(timeStr);
            }
        });

        castViewModel.transportState.observe(appCompatActivity, new Observer<TransportState>() {
            @Override
            public void onChanged(TransportState playState) {
                switch (playState) {
                    case PLAYING:
                        if (interval == null) {
                            monitorPositionInfo();
                        }
                        break;
                }
            }
        });
    }

    private void monitorPositionInfo() {
        interval = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        UpnpServiceHelper.getPositionInfo(castViewModel.upnpService, castViewModel.selectedDevice, realTime -> {
                            appCompatActivity.runOnUiThread(() -> {
                                castViewModel.hasPlayedTime.setValue(TimeUtil.parseTimeStr(realTime));
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
        binding = ViewPositionControlBinding.inflate(LayoutInflater.from(appCompatActivity), this, true);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.isPressed()) {
            Long totalTime = castViewModel.totalTime.getValue();
            long newHasPlayedTime = (long) (totalTime * seekBar.getProgress() / 100.0);
            String timeStr = TimeUtil.getTimeStr(newHasPlayedTime, false);
            UpnpServiceHelper.seek(castViewModel.upnpService, castViewModel.selectedDevice, timeStr);
        }
    }
}
