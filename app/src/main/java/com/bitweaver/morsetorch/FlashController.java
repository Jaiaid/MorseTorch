package com.bitweaver.morsetorch;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class FlashController implements DotDashControllerInterface {
    private final CameraManager camManager;
    private String flashFeatureCameraID;
    private Context context;
    private boolean faulty;

    private void flashOn() {
        try {
            if (camManager != null) {
                camManager.setTorchMode(this.flashFeatureCameraID, true);
            }
        } catch (Exception e) {
            //System.out.println("FlashController::flashOn (28) : " + e);
        }
    }

    private void flashOff() {
        try {
            if (camManager != null) {
                camManager.setTorchMode(flashFeatureCameraID, false);
            }
        } catch (CameraAccessException e) {
        } catch (IllegalArgumentException e) {;
        } catch (Exception e) {
        }
    }

    public FlashController(Context context)
    {
        this.camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        this.context = context;
        this.faulty = false;
        try {
            for (String camId: this.camManager.getCameraIdList()) {
                if (camManager.getCameraCharacteristics(camId).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                    this.flashFeatureCameraID = camId;
                    break;
                }
            }
            if (this.flashFeatureCameraID == null) {
                this.faulty = true;
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
            TimeUnit.MILLISECONDS.sleep(3L * timeunit_msec);
        } catch (InterruptedException e) {
            System.out.println("FlashController::dash (91) : " + e);
            e.printStackTrace();
        }
        flashOff();
    }

    public boolean isFaulty() {
        return this.faulty;
    }
}
