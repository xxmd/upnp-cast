package io.github.xxmd;

import android.app.Application;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.Device;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.xxmd.entity.TransportState;

public class CastViewModel extends AndroidViewModel {

    public CastViewModel(Application application) {
        super(application);
        initMediatorLiveData();
    }

    private void initMediatorLiveData() {
        curFilePath.addSource(curFileIndex, index -> {
            if (index <= filePathList.size() - 1) {
                curFilePath.setValue(filePathList.get(index));
            }
        });

        totalTime.addSource(curFilePath, filePath -> {
            if (CastActivity.isImageFile(filePath)) {
                totalTime.setValue(0L);
            } else {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                File file = new File(filePath);
                retriever.setDataSource(getApplication().getBaseContext(), Uri.fromFile(file));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                retriever.release();
                totalTime.setValue(Long.parseLong(time));
            }
        });
    }

    public AndroidUpnpService upnpService;
    public Device selectedDevice;
    // 投屏文件路径集合
    public List<String> filePathList = new ArrayList<>();

    // 当前投屏文件下标索引
    public MutableLiveData<Integer> curFileIndex = new MutableLiveData<>(0);

    // 当前投屏文件路径
    public MediatorLiveData<String> curFilePath = new MediatorLiveData<>();

    // 已经播放的时间(单位：毫秒)
    public MutableLiveData<Long> hasPlayedTime = new MutableLiveData<>(0L);

    // 投屏文件总时长(单位：毫秒)
    public MediatorLiveData<Long> totalTime = new MediatorLiveData<>();

    // 当前音量
    public MutableLiveData<Integer> curVolume = new MutableLiveData<>(50);

    // 投屏状态
    public MutableLiveData<TransportState> transportState = new MutableLiveData<>(TransportState.STOPPED);

    // 是否静音
    public MutableLiveData<Boolean> isMuted = new MutableLiveData<>(false);
}
