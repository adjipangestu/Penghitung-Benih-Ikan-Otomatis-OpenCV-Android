package development.media.adji_ap.cmo.Utils;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;



public class CustomizableCameraView extends JavaCameraView {

    public CustomizableCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPreviewFPS(double min, double max){
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewFpsRange((int)(min*1), (int)(max*10));
        mCamera.setParameters(params);
    }
}
