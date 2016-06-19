package com.syncano.offlinesample;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

/**
 * This activity class only takes care of showing information about current internet connection
 */
public abstract class NoConnectionActivity extends AppCompatActivity {
    private ValueAnimator animation;
    private NetworkReceiver networkReceiver = new NetworkReceiver();

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, iFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkReceiver);
        super.onStop();
    }

    public void showNoConnectiontBar(boolean show) {
        final View v = findViewById(getNoConnectionViewId());
        if (v == null) return;
        if (animation != null) {
            animation.cancel();
        }
        boolean shown = v.getVisibility() == View.VISIBLE;
        if (shown == show) {
            return;
        }
        connectionStateChanged(!show);

        int viewHeight = getResources().getDimensionPixelSize(R.dimen.no_internet_bar_height);
        if (!show) {
            animation = makeAnimator(v, viewHeight, 0, new Runnable() {
                @Override
                public void run() {
                    v.setVisibility(View.GONE);
                }
            });
        } else {
            v.setVisibility(View.VISIBLE);
            animation = makeAnimator(v, 0, viewHeight, null);
        }

        animation.setDuration(300);
        animation.start();
    }

    private ValueAnimator makeAnimator(final View v, int from, int to, final Runnable doOnEnd) {
        ValueAnimator anim = ValueAnimator.ofInt(from, to);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.height = (int) animation.getAnimatedValue();
                v.setLayoutParams(params);
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (doOnEnd != null) doOnEnd.run();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        return anim;
    }

    public boolean doesConnectionWork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void checkConnectionState() {
        showNoConnectiontBar(!doesConnectionWork());
    }

    public abstract int getNoConnectionViewId();

    public void connectionStateChanged(boolean connected) {
    }

    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            checkConnectionState();
        }
    }
}
