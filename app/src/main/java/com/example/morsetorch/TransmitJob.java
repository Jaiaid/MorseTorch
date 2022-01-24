package com.example.morsetorch;

import android.app.Activity;

public class TransmitJob {
    public String message;
    public Activity owner;
    public TransmissionController controller;

    public TransmitJob(String message, Activity owner, TransmissionController controller) {
        this.message = message;
        this.owner = owner;
        this.controller = controller;
    }
}
