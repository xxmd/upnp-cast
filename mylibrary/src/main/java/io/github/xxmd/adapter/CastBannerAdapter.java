package io.github.xxmd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.youth.banner.adapter.BannerAdapter;

import org.apache.commons.io.FilenameUtils;

import java.util.List;

import io.github.xxmd.R;

public class CastBannerAdapter extends BannerAdapter<String, CastBannerAdapter.ViewHolder> {

    public CastBannerAdapter(List<String> filePathList) {
        super(filePathList);
    }

    @Override
    public ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cast_banner, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindView(ViewHolder holder, String filePath, int position, int size) {
        String extension = FilenameUtils.getExtension(filePath);
        String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (mimeTypeFromExtension.contains("audio")) {
            holder.ivCover.setImageResource(R.drawable.baseline_music_note_24);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(filePath)
                    .into(holder.ivCover);
        }

//        holder.tvFileName.setText(FilenameUtils.getName(data));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundedImageView ivCover;
//        private final TextView tvFileName;

        public ViewHolder(View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
//            tvFileName = itemView.findViewById(R.id.tv_file_name);
        }
    }
}
