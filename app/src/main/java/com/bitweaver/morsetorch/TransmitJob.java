package com.bitweaver.morsetorch;

import android.app.Activity;

public class TransmitJob {
    private final Object lock = new Object();
    private String message;
    private Activity owner;
    private TransmissionController controller;
    private boolean isRunning;

    public TransmitJob() {
        this.isRunning = false;
    }

    public TransmitJob(String message, Activity owner, TransmissionController controller) {
        this.message = message;
        this.owner = owner;
        this.controller = controller;
        this.isRunning = false;
    }

    public void setController(TransmissionController controller) {
        this.controller = controller;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setOwner(Activity owner) {
        synchronized (this.lock) {
            this.owner = owner;
        }
    }

    public void setRunning() {
        this.isRunning = true;
    }

    public void setNotRunning() {
        this.isRunning = false;
    }

    public Activity getOwner() {
        synchronized (this.lock) {
            Activity owner = this.owner;
        }
        return owner;
    }

    public String getMessage() {
        return this.message;
    }

    public TransmissionController getController() {
        return this.controller;
    }

    public boolean isRunning() {
        return this.isRunning;
    }
}
