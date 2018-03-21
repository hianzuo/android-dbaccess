package com.flyhand.core.app;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;

import com.flyhand.content.Intent;
import com.flyhand.core.activity.ExActivity;
import com.flyhand.core.utils.AlertUtil;
import com.flyhand.core.widget.ToastFlyhand;

/**
 * User: Ryan
 * Date: 14-5-15
 * Time: 上午9:43
 */
public class ExFragment extends Fragment {
    private ExActivity mActivity;
    private boolean isResumeFirst = false;

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof ExActivity)) {
            throw new IllegalStateException(getClass().getSimpleName() + " must be attached to a ExActivity.");
        }
        mActivity = (ExActivity) activity;

        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    public ExActivity getExActivity() {
        return mActivity;
    }

    public Intent getActivityIntent() {
        return getExActivity().getExIntent();
    }

    public void showToast(final String msg) {
        ExActivity activity = getExActivity();
        if (null != activity) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastFlyhand.makeText(getExActivity(), msg, ToastFlyhand.LENGTH_LONG).show();
                }
            });
        }
    }

    public void toastString(String s) {
        ToastFlyhand.makeText(getExActivity(), s, ToastFlyhand.LENGTH_LONG).show();
    }

    public void startActivity(Intent intent) {
        getExActivity().startActivity(intent);
    }

    public void finishActivity() {
        getExActivity().finish();
    }

    public PackageManager getPackageManager() {
        return getExActivity().getPackageManager();
    }


    public void onResumeFirst() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isResumeFirst) {
            isResumeFirst = true;
            onResumeFirst();
        }
    }

    protected void showProgressDialog(String msg) {
        ExActivity activity = getExActivity();
        if (null != activity) {
            activity.showProgressDialog(msg);
        } else {
            throw new RuntimeException("no ExActivity for show progress dialog");
        }
    }

    protected void closeProgressDialog() {
        ExActivity activity = getExActivity();
        if (null != activity) {
            activity.closeProgressDialog();
        }
    }

    protected void alert(String msg) {
        ExActivity activity = getExActivity();
        if (null != activity) {
            AlertUtil.alert(getExActivity(),msg);
        }
    }


    @Override
    public final void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        onActivityResult(requestCode, resultCode, Intent.create(data));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent.get());
    }
}
