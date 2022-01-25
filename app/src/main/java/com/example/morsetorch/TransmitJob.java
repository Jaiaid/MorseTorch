package com.example.morsetorch;

import android.app.Activity;

public class TransmitJob {
    private final Object lock = new Object();
    private String message;
    private Activity owner;
    private TransmissionController controller;

    public TransmitJob() {
    }

    public TransmitJob(String message, Activity owner, TransmissionController controller) {
        this.message = message;
        this.owner = owner;
        this.controller = controller;
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

    public Activity getOwner() {
        synchronized (this.lock) {
            Activity owner = this.owner;
        }
        return owner;
    }

    public String getMessage() {
        return message;
    }

    public TransmissionController getController() {
        return controller;
    }
}
