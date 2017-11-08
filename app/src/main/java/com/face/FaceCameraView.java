package com.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.VisionDetRet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;


/**
 * Created by ydong on 17-9-6.
 */

public class FaceCameraView extends SurfaceView implements SurfaceHolder.Callback,
        Camera.PreviewCallback {
    private static final int CAMERA_SHOW_WIDTH = 640;
    private static final int CAMERA_SHOW_HEIGHT = 480;
    private static final int THUMBNAIL_WIDTH = 160;
    private static final int THUMBNAIL_HEIGHT = 128;
    private static final int FRAME = 15;
    private static final int QUALITY = 50;
    private final FaceDet mFaceDet;
    private SurfaceHolder holder;
    private Camera mCamera;
    private Context mContext;
    public static boolean isOpenFaceDetect = false;/*openFace库人脸检测 */
    public int OpenFaceLib = 1;/*openFace库*/
    public int FaceScene = OpenFaceLib;/* 默认人脸检测模式 */
    private IFaceDetectListner faceDetectlistner;
    private Bitmap targetBitmap = null;


    public FaceCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        holder = getHolder();
        holder.addCallback(this);
        //调用三方人脸检测的库
        long start = System.currentTimeMillis();
        mFaceDet = new FaceDet(Constants.getFaceShapeModelPath());
        long end = System.currentTimeMillis();
        long tee = (end - start) ;
        Log.d("main::", "时间 ydong：" + tee + "ms");
    }


    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(0);
                mCamera.setDisplayOrientation(90);// 手机的话需要旋转90度
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(CAMERA_SHOW_WIDTH, CAMERA_SHOW_HEIGHT); // 获得摄像区域的大小
                parameters.setPreviewFrameRate(FRAME);// 每秒3帧 每秒从摄像头里面获得3个画面
                parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片输出的格式
                parameters.set("jpeg-quality", QUALITY);// 设置照片质量
                parameters.set("jpeg-thumbnail-height", THUMBNAIL_HEIGHT);// 设置照片高度
                parameters.set("jpeg-thumbnail-width", THUMBNAIL_WIDTH);// 设置照片宽度
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦

                mCamera.setPreviewCallback(this);
                mCamera.setParameters(parameters);// 把上面的设置 赋给摄像头
                mCamera.setPreviewDisplay(holder);// 整个程序的核心，相机预览的内容放在 holder

                mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        if (mCamera != null) {
            mCamera.startPreview();// 该方法只有相机开启后才能调用
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();// 释放相机资源
            mCamera = null;
        }
    }


    public void saveBitmapFile(Bitmap bitmap, File file, final String path) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BaidumatchFace(path);
        }
    }


    public void BaidumatchFace(String path) {
        final long start = System.currentTimeMillis();
        // 人脸对比url
        final String matchUrl = "https://aip.baidubce.com/rest/2.0/face/v2/match";
        String filePath2 = "/sdcard/reeman/1.jpg"; // 本地放一张自己的身份证图片
        String filePath3 = path;
        try {
            byte[] imgData2 = FaceFileUtil.readFileByBytes(filePath2);
            byte[] imgData3 = FaceFileUtil.readFileByBytes("/sdcard/" + filePath3);
            String imgStr2 = FaceBase64Util.encode(imgData2);
            String imgStr3 = FaceBase64Util.encode(imgData3);
            final String params = URLEncoder.encode("images", "UTF-8") + "=" + URLEncoder.encode(imgStr2 + "," + imgStr3, "UTF-8");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String accessToken = FaceAuthService.getAuth();
                    String result = null;
                    try {
                        result = FaceHttpUtil.post(matchUrl, accessToken, params);
                        System.out.println("main:1111 ydong" + result);
                        long end = System.currentTimeMillis();
                        System.out.print("main::1111 ydong--->" + (start - end) / 1000);

                        faceDetectlistner.DetectFaceSuccess(12.2, result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Handler handler = new Handler();
    int faceMacthCount = 0;

    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera) {

        if (FaceScene == OpenFaceLib) {
            if (isOpenFaceDetect == false) {
                isOpenFaceDetect = true;

                File root = Environment.getExternalStorageDirectory();
                final String path = "/reeman/user/facematch.jpg";
                String pathDir = root.getAbsolutePath() + path;
                final File file = new File(pathDir);

                Rect rect = new Rect(0, 0, CAMERA_SHOW_WIDTH, CAMERA_SHOW_HEIGHT);
                YuvImage yuvImg = new YuvImage(bytes, mCamera.getParameters().getPreviewFormat(), CAMERA_SHOW_WIDTH, CAMERA_SHOW_HEIGHT, null);
                ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
                yuvImg.compressToJpeg(rect, 100, outputstream);
                targetBitmap = BitmapFactory.decodeByteArray(outputstream.toByteArray(), 0, outputstream.size()).copy(Bitmap.Config.ARGB_8888, true);
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        List<VisionDetRet> results;
                        synchronized (FaceCameraView.this) {
                            results = mFaceDet.detect(targetBitmap);
                            if (results.size() == 0) {

                                faceMacthCount++;
                                faceDetectlistner.DetectFaceSuccess(2.0, "脸移入框内" + faceMacthCount);
                                try {
                                    Thread.sleep(400);
                                    isOpenFaceDetect = false;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Log.d("main::", "ydong results:\t::没有识别到人脸[]" + faceMacthCount);
                            } else {
                                isOpenFaceDetect = true;
                                faceDetectlistner.DetectFaceSuccess(2.0, "第" + faceMacthCount + "次识别到人脸，正在人脸匹配");
                                faceMacthCount = 0;
                                Log.d("main::", "ydong results:\t::识别到人脸了，快看下面数据" + results + "\npath:" + path);
                                saveBitmapFile(targetBitmap, file, path);
                            }
                        }
                    }
                });
            }
        }
    }

    public void setFaceDetectListner(IFaceDetectListner listner) {
        this.faceDetectlistner = listner;
    }
}

