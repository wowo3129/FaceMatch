package com.face;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
}
