package development.media.adji_ap.cmo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import development.media.adji_ap.cmo.Utils.CustomizableCameraView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;


public class HitungIkan extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "MainActivity";
    private CustomizableCameraView javaCameraView;
    private TextView tvAverage, tvFrameCount;
    private ArrayList<MatOfPoint> countours;
    private Float frameCount = 0f;
    private Float average = 0f;
    private Float allFish = 0f;
    private boolean runIdentification = false;
    private Button btnStart, btnStop;

    Mat mRgba;
    Mat mRgbaFiltered;
    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS: {
                    javaCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }

            }


        }

    };


    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "openCV not loader");
        } else {
            Log.d(TAG, "openCV loader");
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        tvAverage = (TextView) findViewById(R.id.tvAverage);
        tvFrameCount = (TextView) findViewById(R.id.tvFrameCount);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);

        javaCameraView = (CustomizableCameraView) findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

        tvAverage.setVisibility(View.INVISIBLE);
        tvFrameCount.setVisibility(View.INVISIBLE);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runIdentification = true;
                frameCount = 0f;
                average = 0f;
                allFish = 0f;
                tvAverage.setVisibility(View.VISIBLE);
                tvFrameCount.setVisibility(View.VISIBLE);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runIdentification = false;
                tvAverage.setVisibility(View.INVISIBLE);
                tvFrameCount.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) ;
        javaCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) ;
        javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV loaded Succes");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.i(TAG, "OpenCv gagal");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        }

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC1);
        mRgbaFiltered = new Mat(height, width, CvType.CV_8UC4);

    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();

    }



    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Imgproc.cvtColor(mRgba, mRgbaFiltered, Imgproc.COLOR_RGB2HSV);


        if(runIdentification){
            //        Scalar lower = new Scalar(120,100,100);
//        Scalar upper = new Scalar(179, 255, 255);

            int sensitivity = 20;

            //Untuk deteksi objek warna
            Scalar lower = new Scalar ( 20 - sensitivity, 100, 100);
            Scalar upper = new Scalar( 30 + sensitivity, 255, 255);


//            Scalar lower = new Scalar ( 0, 0, 0);
//            Scalar upper = new Scalar( 225, 255, 115);
//        Scalar upper = new Scalar(235, 135, 50);

            Core.inRange(mRgbaFiltered, lower, upper, mRgbaFiltered);;
            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(mRgbaFiltered, contours, hierarchy, Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
            for (int i = 0; i < contours.size(); i++) {
//            if(countours.get(i).size().area() > 100){
//                Imgproc.drawContours(mRgba, contours, i, new Scalar(0, 0, 255), -1);
//            }


                if(contours.get(i).total() > 25){
//                    Imgproc.drawContours(mRgba, contours, i, new Scalar(0, 0, 255), -1);
                    MatOfPoint2f approxCurve = new MatOfPoint2f();
                    MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
                    //Processing on mMOP2f1 which is in type MatOfPoint2f
                    double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
                    Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

                    //Convert back to MatOfPoint
                    MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

                    // Get bounding rect of contour
                    Rect rect = Imgproc.boundingRect(points);

                    Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0, 255), 3);


                    allFish ++;
                }
                Log.i(TAG, i+" = "+contours.get(i).total());
            }

            frameCount++;
            average = allFish/frameCount;

            HitungIkan.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    tvFrameCount.setText("Frame = "+String.valueOf(frameCount));
                    tvAverage.setText("average = "+String.valueOf(average));
                }
            });
        }

        return mRgba;
    }
}
