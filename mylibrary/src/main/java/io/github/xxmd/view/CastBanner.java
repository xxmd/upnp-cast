package io.github.xxmd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.Nullable;

import io.github.xxmd.CastViewModel;
import io.github.xxmd.adapter.CastBannerAdapter;
import io.github.xxmd.databinding.CastBannerBinding;
import io.github.xxmd.util.ContextUtil;


public class CastBanner extends LinearLayout {
    private CastViewModel castViewModel;
    private AppCompatActivity appCompatActivity;
    private CastBannerBinding binding;
    private CastBannerAdapter castBannerAdapter;

    public CastBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        appCompatActivity = ContextUtil.parseContextToAppCompatActivity(context);
        initView();
        initData();
    }

    private void bindEvent() {
        castViewModel.curFileIndex.observe(appCompatActivity, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.banner.setCurrentItem(integer + 1);
            }
        });
    }

    private void initData() {
        binding.getRoot().post(() -> {
            castViewModel = new ViewModelProvider(appCompatActivity).get(CastViewModel.class);
            initBanner();
            bindEvent();
        });
    }

    private void initBanner() {
        binding.banner.setUserInputEnabled(false);
        castBannerAdapter = new CastBannerAdapter(castViewModel.filePathList);
        binding.banner.setAdapter(castBannerAdapter);
    }

    private void initView() {
        binding = CastBannerBinding.inflate(LayoutInflater.from(appCompatActivity), this, true);
    }
}
