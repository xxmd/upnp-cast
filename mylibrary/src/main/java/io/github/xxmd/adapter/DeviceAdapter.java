package io.github.xxmd.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.fourthline.cling.model.meta.Device;

import java.util.List;

import io.github.xxmd.R;
import io.github.xxmd.SingleSelectAdapter;

public class DeviceAdapter extends SingleSelectAdapter<Device, DeviceAdapter.ViewHolder> {
    public DeviceAdapter(List<Device> itemList) {
        super(itemList);
    }

    @Override
    public void onItemSelected(List<Device> itemList, int position, Device item, ViewHolder viewHolder) {
        viewHolder.tvConnected.setText("已连接");
        viewHolder.tvConnected.setTextColor(Color.parseColor("#3D62DE"));
    }

    @Override
    public void onItemUnSelected(List<Device> itemList, int position, Device item, ViewHolder viewHolder) {
        viewHolder.tvConnected.setText("未连接");
        viewHolder.tvConnected.setTextColor(Color.parseColor("#999999"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(createViewByLayoutId(parent, R.layout.item_device));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Device device = itemList.get(position);
        holder.tvDeviceName.setText(device.getDetails().getFriendlyName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvDeviceName;
        public final TextView tvConnected;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
            tvConnected = itemView.findViewById(R.id.tv_connected);
        }
    }
}
