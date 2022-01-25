package com.example.morsetorch;

import android.content.Context;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class TransmissionController {
    private final Executor executor;

    private static int timeunit_msec;

    private boolean transmissionStopFlagRaised;
    private FlashController flashController;
    private ScreenController screenController;

    private static void intraDotDashWait()
    {
        try {
            TimeUnit.MILLISECONDS.sleep(timeunit_msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static void interCharWait()
    {
        try {
            TimeUnit.MILLISECONDS.sleep(3 * timeunit_msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public TransmissionController(Executor executor, int timeunit_msec, Context context)
    {
        this.executor = executor;
        this.timeunit_msec = timeunit_msec;
        this.transmissionStopFlagRaised = true;
        flashController = new FlashController(context);
        screenController = new ScreenController();
    }

    public boolean isTransmissionStopFlagRaised() {
        return transmissionStopFlagRaised;
    }

    public void startTransmit(final TransmitJob job, final MessageOwnerActivityAndTransmitterConversationCallbacks callback)
    {
        this.transmissionStopFlagRaised = false;
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onJobStart(job);
                // do-while loop ensures at least one time transmission will be attempted
                // at loop end will check if owner wants repetition
                do {
                    try {
                        String processed_message = job.getMessage().toUpperCase(Locale.ROOT);

                        for (int i = 0; i < processed_message.length(); i++) {
                            char c = processed_message.charAt(i);
                            if (DotDashRepo.charToDotDashSeqMap.containsKey(c)) {
                                for (char c1 : DotDashRepo.charToDotDashSeqMap.get(c).toCharArray()) {
                                    if (c1 == '-') {
                                        flashController.dash(timeunit_msec);
                                    } else {
                                        flashController.dot(timeunit_msec);
                                    }

                                    // intra dot/dash wait
                                    TransmissionController.intraDotDashWait();
                                }
                                // after each character check if should continue still
                                if (callback.shouldStop(job.getController())) {
                                    callback.onMessageComplete(job.getOwner(), job.getMessage());
                                    callback.onJobComplete(job);
                                    // just get out bro
                                    return;
                                }
                                // for intra character time gap
                                TransmissionController.interCharWait();

                                callback.onCharComplete(job.getOwner(), i);
                            } else {
                                callback.onInvalidCharacter(job.getOwner());
                            }
                        }

                        callback.onMessageComplete(job.getOwner(), job.getMessage());
                    } catch (Exception e) {
                        callback.onFailure(job.getOwner());
                    }
                } while (callback.willRepeat(job.getOwner()));

                callback.onJobComplete(job);
            }
        });
    }
    
    public void stopTransmit() {
        this.transmissionStopFlagRaised = true;
    }
}
