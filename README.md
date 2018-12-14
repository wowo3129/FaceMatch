# FaceMatch
通过本地库检测人脸+百度（网络）人脸比对，来做身份证跟人脸比对，节省人脸检测的请求次数。
1: 人脸检测 (本地检测，调用tzutalin / dlib-android-app 库)
2：人证比对 (百度AI 人脸检测)
3：Todo 后面将本地的离线人脸识别使用策略模式改为：红软、百度、opencv、兼顾离线识别，方便切换  (tzutalin / dlib-android-app还待验证，项目久了，发现运行不了了)

