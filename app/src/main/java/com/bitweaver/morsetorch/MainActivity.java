package com.bitweaver.morsetorch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int numOfBgThread = 1;
    private static final String TRANSMIT_CONTROL_STOPPED = "transmit_control_stopped";
    private static final String REPEAT_MODE_ACTIVATED = "repeat_mode_activated";
    private static final String TIMEUNIT_MSEC = "timeunit_ms";

    private static int timeunit_msec = 1000;
    private boolean isTransmitControlButtonStopped = true;
    private boolean isRepeatMode = false;

    // these are static as we need to keep reference within the whole application lifetime
    // irrespective of this activity lifetime
    // also we need to differentiate between application start and activity start
    private final static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            numOfBgThread, numOfBgThread, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private static TransmitJob job = null;
    private static TransmissionController transmissionController = null;

    private void changeColor(int idx, int colorCode) {
        this.runOnUiThread(() -> {
            EditText et = this.findViewById(R.id.messageTextMultiLine);
            Spannable wordtoSpan = new SpannableString(et.getText());
            wordtoSpan.setSpan(new BackgroundColorSpan(colorCode), 0,
                    idx + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            et.setText(wordtoSpan);
        });
    }

    private void transmitControlButtonAlting() {
        Button button = this.findViewById(R.id.transmitControlButton);

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
        Button button = this.findViewById(R.id.transmitControlButton);

        if (isTransmitControlButtonStopped) {
            button.setText(R.string.start_transmit);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.transmit_start_button_color));
        } else {
            button.setText(R.string.stop_transmit);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.transmit_stop_button_color));
        }
    }

    private void transmitModeRadioButtonGroupRestore() {
        RadioGroup transmitModeButtonGroup = this.findViewById(R.id.transmitModeRadioGroup);
        if (!isRepeatMode) {
            transmitModeButtonGroup.check(R.id.radioSingleTransmitButton);
        } else {
            transmitModeButtonGroup.check(R.id.radioRepeatTransmitButton);
        }
    }

    public void transmitControlButtonOnClick(View view) {
        transmitControlButtonAlting();

        // job start or stop transmit
        if (!isTransmitControlButtonStopped) {
            EditText editText = this.findViewById(R.id.messageTextMultiLine);
            SeekBar dotTimeUnitSeekBar = this.findViewById(R.id.dotTimeUnitSeekBar);
            // disable time unit set seekbar
            dotTimeUnitSeekBar.setEnabled(false);

            // prepare the job
            TransmissionController.setTimeunit_msec(MainActivity.timeunit_msec);
            MainActivity.job.setController(MainActivity.transmissionController);
            MainActivity.job.setMessage(editText.getText().toString());
            MainActivity.job.setOwner(this);

            if (this.transmissionController.isFaulty()) {
                Toast.makeText(this, this.getResources().getString(R.string.flash_unit_unavailability_message),
                        Toast.LENGTH_LONG).show();
            }
            transmissionController.startTransmit(
                    MainActivity.job,
                    MainActivity.messageOwnerActivityAndTransmitterConversationCallbacks
            );
        }
        else {
            MainActivity.transmissionController.stopTransmit();
            if (MainActivity.job.isRunning()) {
                Button transmitControlButton = this.findViewById(R.id.transmitControlButton);
                transmitControlButton.setClickable(false);
                transmitControlButton.setBackgroundColor(ContextCompat.getColor(this, R.color.transmit_button_disable_color));
            }
        }
    }

    public void updateProgressBar(int currentCharIdx) {
        ProgressBar transmitProgressBar = this.findViewById(R.id.transmitProgressBar);
        // set progress w.r.t to max value of bar
        transmitProgressBar.setProgress(((currentCharIdx + 1) * 100)/job.getMessage().length());
    }

    public void jobStartActivityExtTrigger() {
        EditText editText = this.findViewById(R.id.messageTextMultiLine);
        // as this method is supposed to call by external entity it may be on non UI thread
        // in that case we can not update UI, hence calling from UI thread
        this.runOnUiThread(() -> editText.setEnabled(false));
    }

    public void jobCompletionActivityExtTrigger() {
        EditText editText = this.findViewById(R.id.messageTextMultiLine);
        Button transmitControlButton = this.findViewById(R.id.transmitControlButton);
        SeekBar dotTimeUnitSeekBar = this.findViewById(R.id.dotTimeUnitSeekBar);
        ProgressBar transmitProgressBar = this.findViewById(R.id.transmitProgressBar);

        Context context = this;
        // as this method is supposed to call by external entity it may be on non UI thread
        // in that case we can not update UI, hence calling from UI thread
        this.runOnUiThread(() -> {
            // upon job completion, re enable the UI
            editText.setEnabled(true);

            transmitControlButton.setText(R.string.start_transmit);
            transmitControlButton.setBackgroundColor(ContextCompat.getColor(context, R.color.transmit_start_button_color));
            transmitControlButton.setClickable(true);

            // re enable seek bar
            dotTimeUnitSeekBar.setEnabled(true);
            // reset progress bar
            transmitProgressBar.setProgress(0);
        });

        // set the state variable to true as button will be stopped
        this.isTransmitControlButtonStopped = true;
    }

    private void jobFailureActivityExtTrigger() {
        this.runOnUiThread(() -> {;
            Toast.makeText(this, this.getResources().getString(R.string.job_failure_message),
                    Toast.LENGTH_LONG).show();
        });
        MainActivity.transmissionController.stopTransmit();
    }

    private final static MessageOwnerActivityAndTransmitterConversationCallbacks messageOwnerActivityAndTransmitterConversationCallbacks =
            new MessageOwnerActivityAndTransmitterConversationCallbacks() {
                @Override
                public void onJobStart(TransmitJob job) {
                    job.setRunning();
                    ((MainActivity) job.getOwner()).jobStartActivityExtTrigger();
                }

                @Override
                public void onJobComplete(TransmitJob job) {
                    job.setNotRunning();
                    ((MainActivity)job.getOwner()).jobCompletionActivityExtTrigger();
                }

                @Override
                public void onMessageComplete(Activity owner, String message) {
                    ((MainActivity)owner).changeColor(message.length() - 1, Color.TRANSPARENT);
                }

                @Override
                public void onCharComplete(Activity owner, int charIdx) {
                    ((MainActivity)owner).changeColor(charIdx, Color.GREEN);
                    ((MainActivity)owner).updateProgressBar(charIdx);
                }

                @Override
                public void onInvalidCharacter(Activity owner) {

                }

                @Override
                public void onFailure(Activity owner) {
                    ((MainActivity) job.getOwner()).jobFailureActivityExtTrigger();
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

        RadioGroup transmitModeButtonGroup = this.findViewById(R.id.transmitModeRadioGroup);
        transmitModeButtonGroup.setOnCheckedChangeListener((radioGroup, i) ->
                isRepeatMode = (i == R.id.radioRepeatTransmitButton));

        // seek bar on change callback
        SeekBar dotTimeUnitSeekBar = this.findViewById(R.id.dotTimeUnitSeekBar);
        TextView dotTimeUnitLabel = this.findViewById(R.id.seekbarValueTextView);
        dotTimeUnitSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                dotTimeUnitLabel.setText(
                        String.format(getResources().getString(R.string.seekbar_format_label), i + 500));
                MainActivity.timeunit_msec = i + 500;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // init the transmission controller if application is starting for first time
        if (MainActivity.transmissionController == null) {
            MainActivity.transmissionController = new TransmissionController(executor, timeunit_msec, this);
        }

        // init the job if application is starting for first time
        if (MainActivity.job == null) {
            MainActivity.job = new TransmitJob();
        }
        // update the existing job owner field with newly created activity
        else {
            MainActivity.job.setOwner(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(TRANSMIT_CONTROL_STOPPED, isTransmitControlButtonStopped);
        savedInstanceState.putBoolean(REPEAT_MODE_ACTIVATED, isRepeatMode);
        savedInstanceState.putInt(TIMEUNIT_MSEC, timeunit_msec);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.isTransmitControlButtonStopped = savedInstanceState.getBoolean(TRANSMIT_CONTROL_STOPPED);
        this.isRepeatMode = savedInstanceState.getBoolean(REPEAT_MODE_ACTIVATED);
        MainActivity.timeunit_msec = savedInstanceState.getInt(TIMEUNIT_MSEC);

        transmitControlButtonRestore();
        transmitModeRadioButtonGroupRestore();

        EditText editText = this.findViewById(R.id.messageTextMultiLine);
        editText.setEnabled(!MainActivity.job.isRunning());

        if (MainActivity.job.isRunning()) {
            Button transmitControlButton = this.findViewById(R.id.transmitControlButton);
            SeekBar dotTimeUnitSeekBar = this.findViewById(R.id.dotTimeUnitSeekBar);

            if (this.isTransmitControlButtonStopped) {
                transmitControlButton.setClickable(false);
                transmitControlButton.setBackgroundColor(ContextCompat.getColor(this, R.color.transmit_button_disable_color));
            }
            dotTimeUnitSeekBar.setProgress(MainActivity.timeunit_msec - 500);
            dotTimeUnitSeekBar.setEnabled(false);
        }
    }
}