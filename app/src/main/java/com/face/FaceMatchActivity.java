package com.face;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class FaceMatchActivity extends Activity implements IFaceDetectListner {
    @BindView(R.id.face_camera)
    FaceCameraView face_camera;
    @BindView(R.id.face_match_tx)
    TextView face_match_tx;
    @BindView(R.id.face_match)
    Button face_match;

    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_match);
        bind = ButterKnife.bind(this);
        face_camera.setFaceDetectListner(this);
        initPermission();
    }

    @OnClick({R.id.face_match})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.face_match:
                FaceCameraView.isOpenFaceDetect = false;
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void DetectFaceSuccess(double score, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                face_match_tx.setText(msg);
            }
        });
    }

    @Override
    public void DetectFaceFailed(double core, String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }


    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO

        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

}
