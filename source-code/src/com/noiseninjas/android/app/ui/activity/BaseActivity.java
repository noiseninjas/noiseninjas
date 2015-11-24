package com.noiseninjas.android.app.ui.activity;

import com.noiseninjas.android.app.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog = null;
    private AlertDialog mAlertDialog = null;
    protected void showProgressDialog(String progressMessage, boolean isCancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(BaseActivity.this);
        }
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(isCancelable);
        mProgressDialog.setMessage(progressMessage);
        mProgressDialog.show();
    }
    protected void showAlertDialog(String title,String progressMessage, boolean isCancelable) {
        if (mAlertDialog == null) {
            mAlertDialog = getNewAlertDialog();
        }
        mAlertDialog.setCancelable(isCancelable);
        mAlertDialog.setTitle(title);
        mAlertDialog.setMessage(progressMessage);
        mAlertDialog.show();
    }
    private AlertDialog getNewAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setPositiveButton(getString(R.string.ok), null);
        return builder.create();
    }
    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        dismissAlertDialog();
        super.onDestroy();
    }
    protected void dismissAlertDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = null;
    }
    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }
}
