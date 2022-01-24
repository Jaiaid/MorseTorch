package com.example.morsetorch;

import static androidx.core.content.ContextCompat.getSystemService;

import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.content.Context;
import android.hardware.camera2.CameraManager;

import java.util.concurrent.TimeUnit;

public class FlashController implements DotDashControllerInterface {
    private CameraManager camManager;
    private String flashFeatureCameraID;
    private Context context;
    private Camera mCamera;

    private void flashOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (camManager != null) {
                    String cameraId = camManager.getCameraIdList()[1];
                    camManager.setTorchMode(cameraId, true);
                }
            } catch (Exception e) {
                System.out.println("FlashController::flashOn (28) : " + e);
            }
        }
        else {
            mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        }
    }

    private void flashOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (camManager != null) {
                    camManager.setTorchMode(flashFeatureCameraID, false);
                }
            } catch (CameraAccessException e) {
                System.out.println("FlashController::flashOff (45) : " + e);
            }
        }
        else {
            mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
    }

    public FlashController(Context context)
    {
        this.context = context;
        this.camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String camId: this.camManager.getCameraIdList()) {
                if (camManager.getCameraCharacteristics(camId).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true) {
                    flashFeatureCameraID = camId;
                    break;
                }
            }
        }
        catch (Exception e) {
            System.out.println("FlashController::FlashController (69) : " + e);
        }
    }

    @Override
    public void dot(int timeunit_msec) {
        flashOn();
        try {
            TimeUnit.MILLISECONDS.sleep(timeunit_msec);
        } catch (InterruptedException e) {
            System.out.println("FlashController::dot (79) : " + e);
            e.printStackTrace();
        }
        flashOff();
    }

    @Override
    public void dash(int timeunit_msec) {
        flashOn();
        try {
            TimeUnit.MILLISECONDS.sleep(3 * timeunit_msec);
        } catch (InterruptedException e) {
            System.out.println("FlashController::dash (91) : " + e);
            e.printStackTrace();
        }
        flashOff();
    }
}
