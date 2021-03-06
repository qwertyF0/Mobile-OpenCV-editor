package com.example.opencv_imageprocessing_android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
//****************************************************************************************************
public class FirstImageProcess extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{
    static final String TAG = FirstImageProcess.class.getSimpleName();
    private JavaCameraView cameraView;
    private Mat source;
    private Button screenshoot;
    private BaseLoaderCallback openCVLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {

            switch (status) {

                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    cameraView.enableView();
                    cameraView.setVisibility(View.VISIBLE);
                    break;

                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };
    //****************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_image_process);

        cameraView = findViewById(R.id.camera);

        cameraView.setVisibility(View.VISIBLE);
        cameraView.setCvCameraViewListener(this);

        screenshoot = findViewById(R.id.screenshoot);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null) {
            cameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {

            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, openCVLoaderCallback);

        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            openCVLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {

        if (cameraView != null) {
            cameraView.disableView();
        }
        super.onDestroy();
    }
    //****************************************************************************************************
    @Override
    public void onCameraViewStarted(int width, int height) {

        source = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        source.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        source = inputFrame.rgba();

        Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(source, source, new Size(3., 3.));
        Imgproc.Canny(source, source, 200, 20);

        return source;
    }

}
