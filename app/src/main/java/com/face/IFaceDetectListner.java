package com.face;

/**
 * Created by wowo on 2017/9/8.
 */
public interface IFaceDetectListner {

    void DetectFaceSuccess(double score, String msg);

    void DetectFaceFailed(double core, String msg);

}
