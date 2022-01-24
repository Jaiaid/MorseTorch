package com.example.morsetorch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int numOfBgThread = 1;
    private static final String TRANSMIT_CONTROL_STOPPED = "transmit_control_stopped";
    private static final String REPEAT_MODE_ACTIVATED = "repeat_mode_activated";

    private static int timeunit_msec = 500;
    private int highlightIndx = 0;
    private boolean isTransmitControlButtonStopped = true;
    private boolean isRepeatMode = false;

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            numOfBgThread, numOfBgThread, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private static TransmissionController transmissionController = null;

    private void changeColor(int idx, int colorCode) {
        EditText et = (EditText) this.findViewById(R.id.messageTextMultiLine);
        Spannable wordtoSpan = new SpannableString(et.getText());
        wordtoSpan.setSpan(new BackgroundColorSpan(colorCode), 0,
                    idx+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        et.setText(wordtoSpan);
    }

    private void transmitControlButtonAlting() {
        Button button = (Button) this.findViewById(R.id.transmitControlButton);

        if (!isTransmitControlButtonStopped) {
            button.setText(R.string.start_transmit);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.transmit_start_button_color));
        } else {
            button.setText(R.string.stop_transmit);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.transmit_stop_button_color));
        }

        // alt the state
        isTransmitControlButtonStopped = !isTransmitControlButtonStopped;
    }

    private void transmitControlButtonRestore() {
        Button button = (Button) this.findViewById(R.id.transmitControlButton);

        if (isTransmitControlButtonStopped) {
            button.setText(R.string.start_transmit);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.transmit_start_button_color));
        } else {
            button.setText(R.string.stop_transmit);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.transmit_stop_button_color));
        }
    }

    private void transmitModeRadioButtonGroupRestore() {
        RadioGroup transmitModeButtonGroup = (RadioGroup) this.findViewById(R.id.transmitModeRadioGroup);
        if (!isRepeatMode) {
            transmitModeButtonGroup.check(R.id.radioSingleTransmitButton);
        } else {
            transmitModeButtonGroup.check(R.id.radioRepeatTransmitButton);
        }
    }

    public void transmitControlButtonOnClick(View view) {
        transmitControlButtonAlting();
        // if after alting isTransmitControlButtonStopped is false, it means this press should start transmit
        EditText editText = (EditText) this.findViewById(R.id.messageTextMultiLine);
        // if currently transmit control is stopped, current pressing will start it
        // so edittext should be disabled
        editText.setEnabled(isTransmitControlButtonStopped);

        // job start or stop transmit
        if (!isTransmitControlButtonStopped) {
            transmissionController.startTransmit(
                    new TransmitJob(
                            editText.getText().toString(),
                            this,
                            transmissionController
                    ),
                    messageOwnerActivityAndTransmitterConversationCallbacks
            );
        }
        else {
            transmissionController.stopTransmit();
        }
    }

    public void jobCompletionActivityExtTrigger() {
        Button transmitButton = (Button) this.findViewById(R.id.transmitControlButton);
        // as this method is supposed to call by external entity it may be on non UI thread
        // in that case we can not update UI, hence calling from UI thread
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                transmitButton.callOnClick();
            }
        });
    }

    private static MessageOwnerActivityAndTransmitterConversationCallbacks messageOwnerActivityAndTransmitterConversationCallbacks =
            new MessageOwnerActivityAndTransmitterConversationCallbacks() {
                @Override
                public void onJobComplete(TransmitJob job) {
                    ((MainActivity)job.owner).jobCompletionActivityExtTrigger();
                }

                @Override
                public void onMessageComplete(Activity owner, String message) {
                    ((MainActivity)owner).changeColor(message.length() - 1, Color.TRANSPARENT);
                }

                @Override
                public void onCharComplete(Activity owner, int charIdx) {
                    ((MainActivity)owner).changeColor(charIdx, Color.GREEN);
                }

                @Override
                public void onInvalidCharacter(Activity owner) {

                }

                @Override
                public void onFailure(Activity owner) {

                }

                @Override
                public boolean willRepeat(Activity owner) {
                    return ((MainActivity)owner).isRepeatMode;
                }

                @Override
                public boolean shouldStop(TransmissionController transmissionController) {
                    return transmissionController.isTransmissionStopFlagRaised();
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioGroup transmitModeButtonGroup = (RadioGroup) this.findViewById(R.id.transmitModeRadioGroup);
        transmitModeButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                isRepeatMode = (i == R.id.radioRepeatTransmitButton);
            }
        });

        if (this.transmissionController == null) {
            this.transmissionController = new TransmissionController(executor, timeunit_msec, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(TRANSMIT_CONTROL_STOPPED, isTransmitControlButtonStopped);
        savedInstanceState.putBoolean(REPEAT_MODE_ACTIVATED, isRepeatMode);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isTransmitControlButtonStopped = savedInstanceState.getBoolean(TRANSMIT_CONTROL_STOPPED);
        isRepeatMode = savedInstanceState.getBoolean(REPEAT_MODE_ACTIVATED);

        EditText editText = (EditText) this.findViewById(R.id.messageTextMultiLine);
        // if transmit control was stopped on save it means edit text was enabled
        // so edittext enable status should be equal to isTransmitControlButtonStopped
        editText.setEnabled(isTransmitControlButtonStopped);

        transmitControlButtonRestore();
        transmitModeRadioButtonGroupRestore();
    }
}