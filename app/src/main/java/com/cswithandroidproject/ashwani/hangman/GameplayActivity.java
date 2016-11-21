package com.cswithandroidproject.ashwani.hangman;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class GameplayActivity extends AppCompatActivity implements View.OnClickListener{

    private String LOG_TAG = this.getClass().getSimpleName();
    private String INITIAL_STATUS = "GIVE ME YOUR BEST GUESS";
    private ArrayList<String> dictionary;
    private char guess;
    private static final HashMap<Integer, Character> KEYBOARD = new HashMap<>();
    static {
        KEYBOARD.put(R.id.button1, 'a');
        KEYBOARD.put(R.id.button2, 'b');
        KEYBOARD.put(R.id.button3, 'c');
        KEYBOARD.put(R.id.button4, 'd');
        KEYBOARD.put(R.id.button5, 'e');
        KEYBOARD.put(R.id.button6, 'f');
        KEYBOARD.put(R.id.button7, 'g');
        KEYBOARD.put(R.id.button8, 'h');
        KEYBOARD.put(R.id.button9, 'i');
        KEYBOARD.put(R.id.button10, 'j');
        KEYBOARD.put(R.id.button11, 'k');
        KEYBOARD.put(R.id.button12, 'l');
        KEYBOARD.put(R.id.button13, 'm');
        KEYBOARD.put(R.id.button14, 'n');
        KEYBOARD.put(R.id.button15, 'o');
        KEYBOARD.put(R.id.button16, 'p');
        KEYBOARD.put(R.id.button17, 'q');
        KEYBOARD.put(R.id.button18, 'r');
        KEYBOARD.put(R.id.button19, 's');
        KEYBOARD.put(R.id.button20, 't');
        KEYBOARD.put(R.id.button21, 'u');
        KEYBOARD.put(R.id.button22, 'v');
        KEYBOARD.put(R.id.button23, 'w');
        KEYBOARD.put(R.id.button24, 'x');
        KEYBOARD.put(R.id.button25, 'y');
        KEYBOARD.put(R.id.button26, 'z');
    }
    private static final int alphabets = 26;
    private boolean evilMode, isSoundOn, isVibrateOn;
    EvilHangman evilHangman;
    private boolean flagGameOver = false;
    private String TOAST_TEXT = "The game is not over yet. You can search for the word once the game is over.";

    public GameplayActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        /*//get shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        evilMode = sharedPref.getBoolean("pref_title_evil_mode", true);
        isSoundOn = sharedPref.getBoolean("pref_title_sound", true);
        isVibrateOn = sharedPref.getBoolean("pref_title_vibrate", true);
        System.out.println(evilMode + " " + isSoundOn + " " + isVibrateOn);*/

        makeDictionary();

        SharedPreferences sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
        int guessesLeft = sharedPreferences.getInt("guesses", 15);
        int wordLength = sharedPreferences.getInt("wordLength", 5);
        evilMode = sharedPreferences.getBoolean("evilMode", true);
        isSoundOn = sharedPreferences.getBoolean("sound", true);
        isVibrateOn = sharedPreferences.getBoolean("vibration", true);
        evilHangman = new EvilHangman(dictionary, wordLength, guessesLeft);

        TextView statusBox = (TextView) findViewById(R.id.statusbox);
        TextView dashBox = (TextView) findViewById(R.id.dashbox);
        TextView guessLeftBox = (TextView) findViewById(R.id.guessleftbox);

        statusBox.setText(INITIAL_STATUS);
        dashBox.setText(evilHangman.getSecretWord(wordLength));
        guessLeftBox.setText("Guesses Left: " + guessesLeft);


        //Setting up the keyboard
        for(int id : KEYBOARD.keySet()) {
            Button button = (Button) findViewById(id);
            button.setEnabled(true);
            button.setSoundEffectsEnabled(false);
            button.setText(KEYBOARD.get(id).toString());
            button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            button.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gameplay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_search) {
            if(!flagGameOver) {
                Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_SHORT).show();
            }
            else {
                String query = "Define " + removeSpaces(evilHangman.secretWord);
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, query);
                startActivity(intent);
            }
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    public void makeDictionary() {
        try {
            InputStream wordStream = getAssets().open("dictionary.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(wordStream));
            String line = null;
            dictionary = new ArrayList<String>();
            while((line = in.readLine()) != null) {
                String word = line.trim();
                dictionary.add(word);
            }

        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException");
        }
    }

    /*public void initGame() {
        EvilHangman evilHangman = new EvilHangman(dictionary, letters);
    }*/

    public void updateGame(char guessedLetter) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(isVibrateOn) {
            vibe.vibrate(50);
        }
        MediaPlayer wrongAnswer = MediaPlayer.create(this, R.raw.buzzer);
        MediaPlayer rightAnswer = MediaPlayer.create(this, R.raw.sad);
        MediaPlayer surprised = MediaPlayer.create(this, R.raw.surprised);
        TextView statusBox = (TextView) findViewById(R.id.statusbox);
        TextView dashBox = (TextView) findViewById(R.id.dashbox);
        TextView guessLeftBox = (TextView) findViewById(R.id.guessleftbox);

        boolean bool = evilHangman.guessMade(guessedLetter);
        String status, secretWord = evilHangman.secretWord;
        int guessesLeft = evilHangman.guessesLeft;
        dashBox.setText(secretWord);
        guessLeftBox.setText("Guesses Left: " + guessesLeft);

        if(bool) {
            status = "YOU ARE BANG ON RIGHT";
            statusBox.setText(status);
            statusBox.setTextColor(getResources().getColor(R.color.Green));

            if(isUserWon()) {
                status = "YOU WON SIR! TAKE A BOW!";
                if(isSoundOn) {
                    surprised.start();
                }
                for(int btn : KEYBOARD.keySet()) {
                    Button myButton = (Button) findViewById(btn);
                    myButton.setEnabled(false);
                }
                statusBox.setText(status);
                statusBox.setTextColor(getResources().getColor(R.color.Green));
                flagGameOver = true;
            }
            else {
                if(isSoundOn) {
                    rightAnswer.start();
                }
            }
        }
        else {
            status = "YOU CAN'T BEAT ME BUDDY";
            if(isSoundOn) {
                wrongAnswer.start();
            }
            statusBox.setText(status);
            statusBox.setTextColor(getResources().getColor(R.color.Red));

            if(evilHangman.guessesLeft == 0) {
                status = "GAME OVER";
                for(int btn : KEYBOARD.keySet()) {
                    Button myButton = (Button) findViewById(btn);
                    myButton.setEnabled(false);
                }
                flagGameOver = true;
                evilHangman.secretWord = evilHangman.possibleWords.first();
                dashBox.setText("The word was: " + evilHangman.secretWord);
            }
            statusBox.setText(status);
        }
        /*if(isUserWon()) {
            status = "YOU WON SIR! TAKE A BOW!";
            for(int btn : KEYBOARD.keySet()) {
                Button myButton = (Button) findViewById(btn);
                myButton.setEnabled(false);
            }
            statusBox.setText(status);
            statusBox.setTextColor(getResources().getColor(R.color.Green));
        }
        else {
            status = "TRY HARDER. HAHA.";

            statusBox.setTextColor(getResources().getColor(R.color.Red));
            if(evilHangman.guessesLeft == 0) {
                status = "GAME OVER";
                for(int btn : KEYBOARD.keySet()) {
                    Button myButton = (Button) findViewById(btn);
                    myButton.setEnabled(false);
                }
            }
            statusBox.setText(status);
        }*/
    }

    public boolean isUserWon() {
        if(evilHangman.guessesLeft == 0) return false;
        String string = removeSpaces(evilHangman.secretWord);
        if(evilHangman.possibleWords.contains(string)) {
            return true;
        }
        else {
            return false;
        }
    }

    public String removeSpaces(String secretWord) {
        String string = "";
        for(int i = 0; i < secretWord.length(); i++) {
            if(secretWord.charAt(i) != ' ')
                string += secretWord.charAt(i);
        }
        return string;
    }

    public void onClick(View view) {
        int id = view.getId();
        if(KEYBOARD.keySet().contains(id)) {
            Button button = (Button) findViewById(id);
            button.setText("");
            button.setEnabled(false);
            char guessedLetter = KEYBOARD.get(id).charValue();
            updateGame(guessedLetter);

        }
    }

}


