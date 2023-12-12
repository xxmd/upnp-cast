package io.github.xxmd;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.luck.picture.lib.utils.ToastUtils;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.xxmd.adapter.DeviceAdapter;
import me.shaohui.bottomdialog.BaseBottomDialog;

public class SearchDeviceDialog extends BaseBottomDialog implements RegistryListener {

    private DeviceAdapter deviceAdapter;

    private View rootView;
    private TextView tvTitle;
    private TextView tvWifiName;
    private RecyclerView recyclerView;
    private Button cancelBtn;
    private Button restartBtn;
    private Button confirmBtn;
    private TextView emptyView;
    private Device selectedDevice;
    private AndroidUpnpService upnpService;
    private List<Device> availableDevices = new ArrayList<>();

    public SearchDeviceDialog(Device curConnectedDevice) {
        selectedDevice = curConnectedDevice;
    }

    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {

    }


    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        if (!isValidDevice(device)) {
            return;
        }
        // 避免重复添加
        if (!availableDevices.contains(device)) {
            availableDevices.add(device);
            getActivity().runOnUiThread(() -> {
                deviceAdapter.notifyDataSetChanged();
                handleEmpty();
            });
        }
    }

    private void handleEmpty() {
        boolean isEmpty = availableDevices == null || availableDevices.size() == 0;
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
    }

    private boolean isValidDevice(Device device) {
        String type = device.getType().getType();
        Service avTransportService = device.findService(new UDAServiceId("AVTransport"));
        return type.equals("MediaRenderer") && avTransportService != null;
    }

    @Override
    public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {

    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        if (!isValidDevice(device)) {
            return;
        }
        availableDevices.remove(device);
        getActivity().runOnUiThread(() -> {
            deviceAdapter.notifyDataSetChanged();
            handleEmpty();
        });
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device) {

    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice device) {

    }

    @Override
    public void beforeShutdown(Registry registry) {

    }

    @Override
    public void afterShutdown() {
        upnpService.getRegistry().removeListener(this);
    }

    public interface SearchDeviceListener {
        void onConfirm(Device device);

        void onCancel();
    }

    private SearchDeviceListener searchDeviceListener;

    public void setSearchDeviceListener(SearchDeviceListener searchDeviceListener) {
        this.searchDeviceListener = searchDeviceListener;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.dialog_choose_device;
    }

    @Override
    public void bindView(View rootView) {
        this.rootView = rootView;
        initData();
        initView();
        bindEvent();
    }

    private void initData() {
        tvTitle = rootView.findViewById(R.id.tv_title);
        tvWifiName = rootView.findViewById(R.id.tv_wifi_name);
        recyclerView = rootView.findViewById(R.id.recycler);
        cancelBtn = rootView.findViewById(R.id.btn_cancel);
        emptyView = rootView.findViewById(R.id.empty_view);
        restartBtn = rootView.findViewById(R.id.btn_restart_search);
        confirmBtn = rootView.findViewById(R.id.btn_confirm);
    }

    private void initView() {
        initWifiInfo();
        initRecycler();
        startSearch();
        handleEmpty();
        setCancelable(false);
    }

    private void initRecycler() {
        recyclerView.setAnimation(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new LinearGapDecoration(20));
        availableDevices = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(availableDevices);
        deviceAdapter.setEditable(true);
        recyclerView.setAdapter(deviceAdapter);
        if (selectedDevice != null) {
            availableDevices.add(selectedDevice);
            deviceAdapter.selectByObject(selectedDevice);
        }
    }

    private void bindEvent() {
        cancelBtn.setOnClickListener(v -> {
            stopSearch();
            closeDialog();
            if (searchDeviceListener != null) {
                searchDeviceListener.onCancel();
            }
        });

        restartBtn.setOnClickListener(v -> {
            reStartSearch();
            ToastUtils.showToast(getContext(), "重启成功");
        });

        confirmBtn.setOnClickListener(v -> {
            stopSearch();
            closeDialog();
            if (searchDeviceListener != null) {
                searchDeviceListener.onConfirm(selectedDevice);
            }
        });

        deviceAdapter.setSelectedItemChangeListener(new SingleSelectAdapter.SelectedItemChangeListener<Device>() {
            @Override
            public void onSelectedItemChange(Device selectedItem) {
                selectedDevice = selectedItem;
            }
        });
    }

    private void closeDialog() {
        getDialog().dismiss();
    }

    private void startSearch() {
        if (upnpService == null) {
            connectUpnpService(() -> startSearch());
        } else {
            upnpService.getRegistry().addListener(this);
            upnpService.getControlPoint().search();
        }
    }

    private void reStartSearch() {
        availableDevices.clear();
        deviceAdapter.setSelectItem(null);
        deviceAdapter.notifyDataSetChanged();
        stopSearch();
        startSearch();
    }

    /**
     * 连接 UpnpService
     */
    private void connectUpnpService(Runnable onSuccess) {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                upnpService = (AndroidUpnpService) service;
                onSuccess.run();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(getContext(), "UpnpService连接失败", Toast.LENGTH_LONG).show();
            }
        };
        Intent intent = new Intent(getActivity(), AndroidUpnpServiceImpl.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopSearch() {
        if (upnpService != null) {
            upnpService.getControlPoint().getRegistry().shutdown();
        }
    }

    private void initWifiInfo() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        Pattern pattern = Pattern.compile("^\"(.*)\"$");
        Matcher matcher = pattern.matcher(ssid);
        if (matcher.find()) {
            tvWifiName.setText(String.format("手机网络: %s", matcher.group(1)));
        } else {

        }
    }
}
