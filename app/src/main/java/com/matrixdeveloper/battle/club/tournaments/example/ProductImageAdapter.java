package com.matrixdeveloper.battle.club.tournaments.example;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class ProductImageAdapter extends PagerAdapter {
    private List<Integer> ProductImage;

    public ProductImageAdapter(List<Integer> productImage) {
        ProductImage = productImage;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView productImage = new ImageView(container.getContext());
        productImage.setImageResource(ProductImage.get(position));
        productImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        container.addView(productImage, 0);
        return productImage;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }

    @Override
    public int getCount() {
        return ProductImage.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
