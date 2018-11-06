package com.myrungo.rungo;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class CatView extends FrameLayout {
    private static final int VELOCITY = 300;
    private ImageView head;
    private ImageView body;
    private ImageView handRight;
    private ImageView handLeft;
    private ImageView legRight;
    private ImageView legLeft;
    private ObjectAnimator handRightAnimation1;
    private ObjectAnimator handRightAnimation2;
    private ObjectAnimator handLeftAnimation1;
    private ObjectAnimator handLeftAnimation2;
    private ObjectAnimator legRightAnimation1;
    private ObjectAnimator legRightAnimation2;
    private ObjectAnimator legLeftAnimation1;
    private ObjectAnimator legLeftAnimation2;

    public CatView(Context context) {
        super(context);
        init();
    }

    public CatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CatView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_cat, this);
        head = findViewById(R.id.cat_head_image_view);
        body = findViewById(R.id.cat_body_image_view);
        handRight = findViewById(R.id.cat_hand_left_mirror_image_view);
        handLeft = findViewById(R.id.cat_hand_right_mirror_image_view);
        legRight = findViewById(R.id.cat_leg_left_mirror_image_view);
        legLeft = findViewById(R.id.cat_leg_right_mirror_image_view);

        Glide.with(this).load(R.drawable.head).into(head);
        Glide.with(this).load(R.drawable.body).into(body);
        Glide.with(this).load(R.drawable.hand_left_mirror).into(handRight);
        Glide.with(this).load(R.drawable.hand_right_mirror).into(handLeft);
        Glide.with(this).load(R.drawable.leg_r).into(legRight);
        Glide.with(this).load(R.drawable.leg_l).into(legLeft);

        handRightAnimation1 = ObjectAnimator.ofFloat(handRight, "rotation", 10f, 30f);
        handRightAnimation1.setDuration(VELOCITY);
        handRightAnimation1.setRepeatMode(ValueAnimator.REVERSE);
        handRightAnimation1.setRepeatCount(Animation.INFINITE);

        handRightAnimation2 = ObjectAnimator.ofFloat(handRight, "rotation", -10f, -30f);
        handRightAnimation2.setDuration(VELOCITY);
        handRightAnimation2.setRepeatMode(ValueAnimator.REVERSE);
        handRightAnimation2.setRepeatCount(Animation.INFINITE);

        handLeftAnimation1 = ObjectAnimator.ofFloat(handLeft, "rotation", -30f, -10f);
        handLeftAnimation1.setDuration(VELOCITY);
        handLeftAnimation1.setRepeatMode(ValueAnimator.REVERSE);
        handLeftAnimation1.setRepeatCount(Animation.INFINITE);

        handLeftAnimation2 = ObjectAnimator.ofFloat(handLeft, "rotation", 30f, 10f);
        handLeftAnimation2.setDuration(VELOCITY);
        handLeftAnimation2.setRepeatMode(ValueAnimator.REVERSE);
        handLeftAnimation2.setRepeatCount(Animation.INFINITE);

        legRightAnimation1 = ObjectAnimator.ofFloat(legRight, "translationY", 20f);
        legRightAnimation1.setDuration(VELOCITY);
        legRightAnimation1.setRepeatMode(ValueAnimator.REVERSE);
        legRightAnimation1.setRepeatCount(Animation.INFINITE);

        legRightAnimation2 = ObjectAnimator.ofFloat(legLeft, "translationY", -20f);
        legRightAnimation2.setStartDelay(VELOCITY);
        legRightAnimation2.setDuration(VELOCITY);
        legRightAnimation2.setRepeatMode(ValueAnimator.REVERSE);
        legRightAnimation2.setRepeatCount(Animation.INFINITE);

        legLeftAnimation1 = ObjectAnimator.ofFloat(legLeft, "translationY", -20f);
        legLeftAnimation1.setDuration(VELOCITY);
        legLeftAnimation1.setRepeatMode(ValueAnimator.REVERSE);
        legLeftAnimation1.setRepeatCount(Animation.INFINITE);

        legLeftAnimation2 = ObjectAnimator.ofFloat(legRight, "translationY", -20f);
        legLeftAnimation2.setDuration(VELOCITY);
        legLeftAnimation2.setRepeatMode(ValueAnimator.REVERSE);
        legLeftAnimation2.setRepeatCount(Animation.INFINITE);
    }

    public void run() {
        handRightAnimation1.start();
        handRightAnimation2.start();
        handLeftAnimation1.start();
        handLeftAnimation2.start();
        legRightAnimation2.start();
        legLeftAnimation2.start();
    }

    public void stop() {
        handRightAnimation1.cancel();
        handRightAnimation2.cancel();
        handLeftAnimation1.cancel();
        handLeftAnimation2.cancel();
        legRightAnimation1.cancel();
        legRightAnimation2.cancel();
        legLeftAnimation1.cancel();
        legLeftAnimation2.cancel();
    }
}
