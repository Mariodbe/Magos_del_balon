package com.example.magosdelbalon.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FullscreenVideoView extends VideoView {

    public FullscreenVideoView(Context context) {
        super(context);
    }

    public FullscreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullscreenVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
    }
}
