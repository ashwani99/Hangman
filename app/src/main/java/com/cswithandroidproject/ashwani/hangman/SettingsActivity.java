package com.cswithandroidproject.ashwani.hangman;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    public static SeekBar wordLengthBar, guessesBar;
    public int wordLength, numberOfGuesses;
    private TextView guessesValTextView, wordLengthTextView;
    private Switch evilModeSwitch, soundSwitch, vibrationSwitch;
    private boolean evilMode, sound, vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        wordLengthBar = (SeekBar)findViewById(R.id.seekBarWordLength);
        guessesBar = (SeekBar)findViewById(R.id.seekBarGuesses);
        guessesValTextView = (TextView)findViewById(R.id.textViewGuesses);
        wordLengthTextView = (TextView)findViewById(R.id.textViewWordLength);
        //evilModeSwitch = (Switch)findViewById(R.id.evilmodeswitch);
        soundSwitch = (Switch)findViewById(R.id.soundswitch);
        vibrationSwitch = (Switch)findViewById(R.id.vibrationswitch);

        SharedPreferences sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
        numberOfGuesses = sharedPreferences.getInt("guesses", 15);
        wordLength = sharedPreferences.getInt("wordLength", 5);
        //evilMode = sharedPreferences.getBoolean("evilMode", true);
        sound = sharedPreferences.getBoolean("sound", true);
        vibration = sharedPreferences.getBoolean("vibration", true);

        wordLengthBar.setProgress(wordLength);
        guessesBar.setProgress(numberOfGuesses);
        wordLengthTextView.setText("Word length : " + wordLength);
        guessesValTextView.setText("Number of guesses : " + numberOfGuesses);
        wordLengthBar.setMax(29);
        guessesBar.setMax(18);
        //evilModeSwitch.setChecked(evilMode);
        soundSwitch.setChecked(sound);
        vibrationSwitch.setChecked(vibration);

        /*evilModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                evilMode = b;
            }
        });*/

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sound = b;
            }
        });

        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                vibration = b;
            }
        });

        wordLengthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 3) {
                    progress = 3;
                }
                String progressValString = String.valueOf(progress);
                wordLengthTextView.setText("Word length : " + progressValString);
                wordLength = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        guessesBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 3) {
                    progress = 3;
                }
                String progressValString = String.valueOf(progress);
                guessesValTextView.setText("Number of guesses : " + progressValString);
                numberOfGuesses = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }



    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("guesses", numberOfGuesses);
        editor.putInt("wordLength", wordLength);
        //editor.putBoolean("evilMode", evilMode);
        editor.putBoolean("sound", sound);
        editor.putBoolean("vibration", vibration);
        editor.commit();
        finish();
    }
}
