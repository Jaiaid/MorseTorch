package com.bitweaver.morsetorch;

import android.app.Activity;

public interface MessageOwnerActivityAndTransmitterConversationCallbacks {
    void onJobStart(TransmitJob job);
    void onJobComplete(TransmitJob job);
    void onMessageComplete(Activity owner, String message);
    void onCharComplete(Activity owner, int charIdx);
    void onInvalidCharacter(Activity owner);
    void onFailure(Activity owner);
    boolean willRepeat(Activity owner);

    boolean shouldStop(TransmissionController transmissionController);
}
