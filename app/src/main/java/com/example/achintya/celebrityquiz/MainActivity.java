package com.example.achintya.celebrityquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String GUESSES = "settings_numberOfGuesses";
    public static final String CELEBRITY_TYPE = "settings_celebrityType";
    public static final String QUIZ_BACKGROUND_COLOR = "settings_quiz_background_color";
    public static final String QUIZ_FONT = "settings_quiz_font";

    private boolean isSettingsChanged = false;

    static Typeface chunkfive;
    static Typeface fontleroybrown;
    static Typeface wonderbardemo;
    static Typeface divatdemo;
    static Typeface zen3demo;

    MainActivityFragment myCelebrityQuizFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chunkfive = Typeface.createFromAsset(getAssets(), "fonts/Chunkfive.otf");
        fontleroybrown = Typeface.createFromAsset(getAssets(), "fonts/FontleroyBrown.ttf");
        wonderbardemo = Typeface.createFromAsset(getAssets(), "fonts/Wonderbar Demo.otf");
        divatdemo = Typeface.createFromAsset(getAssets(), "fonts/DivatDemo.ttf");
        zen3demo = Typeface.createFromAsset(getAssets(), "fonts/Zen3Demo.ttf");

        PreferenceManager.setDefaultValues(MainActivity.this, R.xml.quiz_preferences, false);

        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).
                registerOnSharedPreferenceChangeListener(settingChangeListener);

        myCelebrityQuizFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.celebrityQuizFragment);

        myCelebrityQuizFragment.modifyCelebrityGuessRows(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myCelebrityQuizFragment.modifyTypeOfCelebrityInQuiz(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myCelebrityQuizFragment.modifyQuizFont(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myCelebrityQuizFragment.modifyBackgroundColor(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myCelebrityQuizFragment.resetCelebrityQuiz();
        isSettingsChanged = false;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent preferencesIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(preferencesIntent);

        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener settingChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


            isSettingsChanged = true;

            if (key.equals(GUESSES)) {

                myCelebrityQuizFragment.modifyCelebrityGuessRows(sharedPreferences);
                myCelebrityQuizFragment.resetCelebrityQuiz();

            } else if (key.equals(CELEBRITY_TYPE)) {

                Set<String> celebrityTypes = sharedPreferences.getStringSet(CELEBRITY_TYPE, null);

                if (celebrityTypes != null && celebrityTypes.size() > 0) {

                    myCelebrityQuizFragment.modifyTypeOfCelebrityInQuiz(sharedPreferences);
                    myCelebrityQuizFragment.resetCelebrityQuiz();

                } else {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    celebrityTypes.add(getString(R.string.default_celebrity_type));
                    editor.putStringSet(CELEBRITY_TYPE, celebrityTypes);
                    editor.apply();

                    Toast.makeText(MainActivity.this,
                            R.string.toast_message, Toast.LENGTH_SHORT).show();

                }

            } else if (key.equals(QUIZ_FONT)) {

                myCelebrityQuizFragment.modifyQuizFont(sharedPreferences);
                myCelebrityQuizFragment.resetCelebrityQuiz();
            } else if (key.equals(QUIZ_BACKGROUND_COLOR)) {

                myCelebrityQuizFragment.modifyBackgroundColor(sharedPreferences);
                myCelebrityQuizFragment.resetCelebrityQuiz();

            }

            Toast.makeText(MainActivity.this, R.string.change_message, Toast.LENGTH_SHORT).show();




        }
    };

}
