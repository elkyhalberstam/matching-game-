package com.example.matchinggame;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.example.matchinggame.Matching.getGameFromJSON;
import static com.example.matchinggame.Matching.getJSONFromGame;
import static com.example.matchinggame.Utils.showInfoDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {


    // Game (current game) object
    private Matching mCurrentGame;

    // Adapter (current board) int[]
    private ImageButton [] cardBoard = new ImageButton[8];
    private int[] mImages;


    // Status Bar and SnackBar View references
    private TextView numMatches, numLivesRemaining;

    //private ImageButton cardOne, cardTwo, cardBack;

    private final String mKEY_GAME = "Game";

    private boolean mUseAutoSave;
    private String mKEY_AUTO_SAVE;

    protected void onStop() {
        super.onStop();
        saveOrDeleteGameInSharedPrefs();
    }

    private void saveOrDeleteGameInSharedPrefs() {
        SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        // Save current game or remove any prior game to/from default shared preferences
        if (mUseAutoSave)
            editor.putString(mKEY_GAME, mCurrentGame.getJSONFromCurrentGame());
        else
            editor.remove(mKEY_GAME);

        editor.apply();
    }

    protected void onStart(){
        super.onStart();
        restoreFromPreferences_SavedGameIfAutoSaveWasSetOn();
        restoreOrSetFromPreferences_AllAppAndGameSettings();
    }

    private void restoreFromPreferences_SavedGameIfAutoSaveWasSetOn() {
        SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(this);
        if (defaultSharedPreferences.getBoolean(mKEY_AUTO_SAVE,true)) {
            String gameString = defaultSharedPreferences.getString(mKEY_GAME, null);
            if (gameString!=null) {
                mCurrentGame = Matching.getGameFromJSON(gameString);
                updateScore();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(mKEY_GAME, getJSONFromGame(mCurrentGame));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentGame = getGameFromJSON(savedInstanceState.getString(mKEY_GAME));
        updateScore();
        updateBoard();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateBoard() {
        for (int i = 0; i < mCurrentGame.getFlipped().length; i++) {
            if(mCurrentGame.getFlipped()[i]){
                int num = mCurrentGame.getBoard()[i];
                cardBoard[i].setImageDrawable(getDrawable(mImages[num - 1]));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setUpDrawables();
        setUpViews();
        setupFAB();
        buttonListenerSetUp();
        startNewGame();
    }

    private void setupFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "You've played " +mCurrentGame.getGamesPlayed(),Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private void buttonListenerSetUp() {
        // Listener that responds to all buttons
        View.OnClickListener listener = new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (!mCurrentGame.gameOver()) {
                    int tag;
                    if (mCurrentGame.getCardOne() == 9) {
                        tag = Integer.parseInt(v.getTag().toString());
                        int numCard = mCurrentGame.getBoard()[tag];
                        cardBoard[tag].setImageDrawable(getDrawable(mImages[numCard - 1]));
                        mCurrentGame.setCardOne(mCurrentGame.getBoard()[tag]);
                        mCurrentGame.setCardOneTag(tag);
                        mCurrentGame.setFlippedSlot(tag, true);
                    } else if (mCurrentGame.getCardTwo() == 9) {
                        tag = Integer.parseInt(v.getTag().toString());
                        int numCard = mCurrentGame.getBoard()[tag];
                        if(tag != mCurrentGame.getCardOneTag()) {
                            cardBoard[tag].setImageDrawable(getDrawable(mImages[numCard - 1]));
                            mCurrentGame.setCardTwo(mCurrentGame.getBoard()[tag]);
                            mCurrentGame.setCardTwoTag(tag);
                            mCurrentGame.setFlippedSlot(tag, true);
                        }
                    } else {
                        isMatch();
                        updateScore();
                    }
                }
            }
        };

        // set listener on all buttons using array above
        for (ImageButton current : cardBoard)
            current.setOnClickListener(listener);
    }

    private void setUpDrawables(){
        mImages = new int[]{R.drawable.purple, R.drawable.yellow, R.drawable.blue, R.drawable.red, R.drawable.matchinggame};
    }

    private void setUpViews() {
        numMatches = findViewById(R.id.matches);
        numLivesRemaining = findViewById(R.id.remaining);
        cardBoard = new ImageButton[]{findViewById(R.id.card1), findViewById(R.id.card2), findViewById(R.id.card3), findViewById(R.id.card4), findViewById(R.id.card5), findViewById(R.id.card6), findViewById(R.id.card7), findViewById(R.id.card8)};

    }


    private void startNewGame() {
        mCurrentGame = new Matching();
        updateScore();

    }

    private void updateScore(){
        String remaining_text = "Number of Matches:" + mCurrentGame.getMatches();
        String matches_text = "Number of Attempts Remaining: " + mCurrentGame.getLifeCount();
        numMatches.setText(matches_text);
        numLivesRemaining.setText(remaining_text);
        checkForGameOver();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void startNextGame(){
        for (ImageButton current : cardBoard)
            current.setImageDrawable(getDrawable(mImages[4]));
        mCurrentGame.startGame();
        updateScore();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void isMatch() {
        //if (mCurrentGame.getCardOne() == 9 && mCurrentGame.getCardTwo() ==9 && click == 3){
        if (mCurrentGame.isMatch(mCurrentGame.getCardOne(),  mCurrentGame.getCardTwo())) {
            mCurrentGame.setMatches(mCurrentGame.getMatches() + 1);
        } else {
            mCurrentGame.setLifeCount(mCurrentGame.getLifeCount() - 1);
            cardBoard[mCurrentGame.getCardOneTag()].setImageDrawable(getDrawable(mImages[4]));
            cardBoard[mCurrentGame.getCardTwoTag()].setImageDrawable(getDrawable(mImages[4]));
            mCurrentGame.setFlippedSlot(mCurrentGame.getCardOneTag(), false);
            mCurrentGame.setFlippedSlot(mCurrentGame.getCardTwoTag(), false);
        }
        mCurrentGame.setCardOne(9);
        mCurrentGame.setCardTwo(9);
    }


    /*private void dismissSnackBarIfShown() {
        if (mSnackBar.isShown()) {
            mSnackBar.dismiss();
        }
    }*/

    private void checkForGameOver() {
        if(mCurrentGame.gameOver()){
            if(mCurrentGame.getLifeCount() == 0) {
                showInfoDialog(this, R.string.lost, R.string.lost_message);
            }else{
                showInfoDialog(this, R.string.win, R.string.win_message);
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newgame: {
                startNextGame();
                return true;
            }
            case R.id.info: {
                showInfoDialog(MainActivity.this, R.string.info_, R.string.info_text);
                return true;
            }
            case R.id.settings: {
                showSettings();
                return true;
            }
            case R.id.about: {
                showInfoDialog(MainActivity.this, R.string.about, R.string.about_text);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showSettings() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        settingsLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> restoreOrSetFromPreferences_AllAppAndGameSettings());

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            restoreOrSetFromPreferences_AllAppAndGameSettings();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void restoreOrSetFromPreferences_AllAppAndGameSettings() {
        SharedPreferences sp = getDefaultSharedPreferences(this);
        mUseAutoSave = sp.getBoolean(mKEY_AUTO_SAVE, true);
    }

    public void startNewGame(MenuItem item) {
        startNextGame();
    }
}
    
