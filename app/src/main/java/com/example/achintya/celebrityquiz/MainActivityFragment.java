package com.example.achintya.celebrityquiz;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    private static final int NUMBER_OF_CELEBRITY_INCLUDED_IN_QUIZ = 20;

    private List<String> allCelebrityNamesList;
    private List<String> celebrityNameQuizList;
    private Set<String> celebrityTypesInQuiz;
    private String correctCelebrityAnswer;
    private int numberOfAllGuesses;
    private int numberOfRightAnswers;
    private int numberOfCelebrityGuessRows;
    private SecureRandom secureRandomNumber;
    private android.os.Handler handler;
    private Animation wrongAnswerAnimation;
    //private Animation rightAnswerAnimation;

    private LinearLayout celebrityQuizLinearLayout;
    private TextView txtQuestionNumber;
    private ImageView imgCelebrity;
    private LinearLayout[] rowsOfGuessButtonsInCelebrityQuiz;
    private TextView txtAnswer;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        allCelebrityNamesList = new ArrayList<>();
        celebrityNameQuizList = new ArrayList<>();
        secureRandomNumber = new SecureRandom();
        handler = new android.os.Handler();

        wrongAnswerAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.wrong_answer_animation);

        wrongAnswerAnimation.setRepeatCount(1);

        //rightAnswerAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.right_answer_animation);

        //rightAnswerAnimation.setRepeatCount(1);

        celebrityQuizLinearLayout = (LinearLayout) view.findViewById(R.id.celebrityQuizLinearLayout);
        txtQuestionNumber = (TextView) view.findViewById(R.id.txtQuestionNumber);
        imgCelebrity = (ImageView) view.findViewById(R.id.imgCelebrity);

        rowsOfGuessButtonsInCelebrityQuiz = new LinearLayout[3];

        rowsOfGuessButtonsInCelebrityQuiz[0] = (LinearLayout) view.findViewById(R.id.firstRowLinearLayout);
        rowsOfGuessButtonsInCelebrityQuiz[1] = (LinearLayout) view.findViewById(R.id.secondRowLinearLayout);
        rowsOfGuessButtonsInCelebrityQuiz[2] = (LinearLayout) view.findViewById(R.id.thirdRowLinearLayout);

        txtAnswer = (TextView) view.findViewById(R.id.txtAnswer);

        for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {
            for (int column = 0; column < row.getChildCount(); column++) {

                Button btnGuess = (Button) row.getChildAt(column);
                btnGuess.setOnClickListener(btnGuessListener);
                btnGuess.setTextSize(24);
            }
        }

        txtQuestionNumber.setText(getString(R.string.question_text, 1, NUMBER_OF_CELEBRITY_INCLUDED_IN_QUIZ));

        //txtQuestionNumber.setText("This is Animal {1} of {10}");

        return view;
    }

    private View.OnClickListener btnGuessListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Button btnGuess = ((Button) view);
            String guessValue = btnGuess.getText().toString();
            String answerValue = getTheExactCelebrityName(correctCelebrityAnswer);
            ++numberOfAllGuesses;

            if (guessValue.equals(answerValue)) {

                ++numberOfRightAnswers;

                txtAnswer.setText(answerValue + " is " + "RIGHT!!");

                disableQuizGuessButton();

                if (numberOfRightAnswers == NUMBER_OF_CELEBRITY_INCLUDED_IN_QUIZ) {

                    new AlertDialog.Builder(getContext())
                            .setMessage(getString(R.string.results_string_value, numberOfAllGuesses,
                                    2000 / (double) numberOfAllGuesses))
                            .setPositiveButton(R.string.reset_celebrity_quiz, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    resetCelebrityQuiz();


                                }
                            })
                            .setCancelable(false)
                            .show();

                } else {

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateCelebrityQuiz(true);
                        }
                    }, 1000);
                }
            } else {
                imgCelebrity.startAnimation(wrongAnswerAnimation);

                txtAnswer.setText("Wrong Answer!!");
                btnGuess.setEnabled(false);
            }

        }
    };

    private String getTheExactCelebrityName(String celebrityName) {

        return celebrityName.substring(celebrityName.indexOf('-') + 1).replace("_", " ");
    }

    private void disableQuizGuessButton() {

        for (int row = 0; row < numberOfCelebrityGuessRows; row++) {
            LinearLayout guessRowLinearLayout = rowsOfGuessButtonsInCelebrityQuiz[row];

            for (int buttonIndex = 0; buttonIndex < guessRowLinearLayout.getChildCount(); buttonIndex++) {
                guessRowLinearLayout.getChildAt(buttonIndex).setEnabled(false);
            }
        }
    }

    public void resetCelebrityQuiz() {

        AssetManager assets = getActivity().getAssets();
        allCelebrityNamesList.clear();

        try {
            for (String celebrityType : celebrityTypesInQuiz) {
                String[] celebrityImagePathsInQuiz = assets.list(celebrityType);

                for (String celebrityImagePathInQuiz : celebrityImagePathsInQuiz) {
                    allCelebrityNamesList.add(celebrityImagePathInQuiz.replace(".png", ""));
                }
            }

        } catch (IOException e) {
            Log.e("CelebrityQuiz", "Error", e);

        }
        numberOfRightAnswers = 0;
        numberOfAllGuesses = 0;
        celebrityNameQuizList.clear();

        int counter = 1;
        int numberOfAvailableCelebrity = allCelebrityNamesList.size();

        while (counter <= NUMBER_OF_CELEBRITY_INCLUDED_IN_QUIZ) {
            int randomIndex = secureRandomNumber.nextInt(numberOfAvailableCelebrity);

            String celebrityImageName = allCelebrityNamesList.get(randomIndex);

            if (!celebrityNameQuizList.contains(celebrityImageName)) {

                celebrityNameQuizList.add(celebrityImageName);
                ++counter;
            }
        }
        showNextCelebrity();

    }

    private void animateCelebrityQuiz(boolean animateOutCelebrityImage) {

        if (numberOfRightAnswers == 0) {
            return;
        }

        int xTopLeft = 0;
        int yTopLeft = 0;

        int xBottomRight = celebrityQuizLinearLayout.getLeft() + celebrityQuizLinearLayout.getRight();
        int yBottomRight = celebrityQuizLinearLayout.getTop() + celebrityQuizLinearLayout.getBottom();

        int radius = Math.max(celebrityQuizLinearLayout.getWidth(), celebrityQuizLinearLayout.getHeight());

        Animator animator;

        if (animateOutCelebrityImage) {

            animator = ViewAnimationUtils.createCircularReveal(celebrityQuizLinearLayout,
                    xBottomRight, yBottomRight, radius, 0);

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    showNextCelebrity();

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {

            animator = ViewAnimationUtils.createCircularReveal(celebrityQuizLinearLayout,
                    xTopLeft, yTopLeft, 0, radius);
        }
        animator.setDuration(700);
        animator.start();
    }

    private void showNextCelebrity() {

        String nextCelebrityImageName = celebrityNameQuizList.remove(0);
        correctCelebrityAnswer = nextCelebrityImageName;
        txtAnswer.setText("");

        txtQuestionNumber.setText(getString(R.string.question_text,
                (numberOfRightAnswers + 1), NUMBER_OF_CELEBRITY_INCLUDED_IN_QUIZ));

        String celebrityType = nextCelebrityImageName.substring(0, nextCelebrityImageName.indexOf("-"));

        AssetManager assets = getActivity().getAssets();

        try (InputStream stream = assets.open(celebrityType + "/" + nextCelebrityImageName + ".png")) {

            Drawable celebrityImage = Drawable.createFromStream(stream, nextCelebrityImageName);

            imgCelebrity.setImageDrawable(celebrityImage);

            animateCelebrityQuiz(false);
        } catch (IOException e) {

            Log.e("CelebrityQuiz", "There is an Error Getting" + nextCelebrityImageName, e);
        }

        Collections.shuffle(allCelebrityNamesList);

        int correctCelebrityNameIndex = allCelebrityNamesList.indexOf(correctCelebrityAnswer);
        String correctCelebrityName = allCelebrityNamesList.remove(correctCelebrityNameIndex);
        allCelebrityNamesList.add(correctCelebrityName);

        for (int row = 0; row < numberOfCelebrityGuessRows; row++) {
            for (int column = 0; column < rowsOfGuessButtonsInCelebrityQuiz[row].getChildCount(); column++) {

                Button btnGuess = (Button) rowsOfGuessButtonsInCelebrityQuiz[row].getChildAt(column);
                btnGuess.setEnabled(true);

                String celebrityImageName = allCelebrityNamesList.get((row * 2) + column);
                btnGuess.setText(getTheExactCelebrityName(celebrityImageName));
            }
        }

        int row = secureRandomNumber.nextInt(numberOfCelebrityGuessRows);
        int column = secureRandomNumber.nextInt(2);
        LinearLayout randomRow = rowsOfGuessButtonsInCelebrityQuiz[row];
        String correctCelebrityImageName = getTheExactCelebrityName(correctCelebrityAnswer);
        ((Button) randomRow.getChildAt(column)).setText(correctCelebrityImageName);

    }

    public void modifyCelebrityGuessRows(SharedPreferences sharedPreferences) {

        final String NUMBER_OF_GUESS_OPTIONS = sharedPreferences.getString(MainActivity.GUESSES, null);

        numberOfCelebrityGuessRows = Integer.parseInt(NUMBER_OF_GUESS_OPTIONS) / 2;

        for (LinearLayout horizontalLinearLayout : rowsOfGuessButtonsInCelebrityQuiz) {

            horizontalLinearLayout.setVisibility(View.GONE);

        }

        for (int row = 0; row < numberOfCelebrityGuessRows; row++) {

            rowsOfGuessButtonsInCelebrityQuiz[row].setVisibility(View.VISIBLE);

        }

    }

    public void modifyTypeOfCelebrityInQuiz(SharedPreferences sharedPreferences) {

        celebrityTypesInQuiz = sharedPreferences.getStringSet(MainActivity.CELEBRITY_TYPE, null);
    }

    public void modifyQuizFont(SharedPreferences sharedPreferences) {

        String fontStringValue = sharedPreferences.getString(MainActivity.QUIZ_FONT, null);

        switch (fontStringValue) {

            case "Chunkfive.otf":
                for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.chunkfive);

                    }

                }

                break;
            case "FontleroyBrown.ttf":

                for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.fontleroybrown);

                    }

                }

                break;
            case "Wonderbar Demo.otf":

                for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.wonderbardemo);

                    }

                }

                break;

            case "DivatDemo.ttf":

                for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.divatdemo);

                    }

                }

                break;

            case "Zen3Demo.ttf":

                for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.zen3demo);

                    }

                }

                break;
        }
    }

    public void modifyBackgroundColor(SharedPreferences sharedPreferences) {

        String backgroundColor = sharedPreferences.getString(MainActivity.QUIZ_BACKGROUND_COLOR, null);

        switch (backgroundColor) {

            case "White":

                celebrityQuizLinearLayout.setBackgroundColor(Color.WHITE);

                for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);

                    }

                }

                txtAnswer.setTextColor(Color.BLUE);
                txtQuestionNumber.setTextColor(Color.BLACK);

                break;

            case "Black":

                celebrityQuizLinearLayout.setBackgroundColor(Color.BLACK);

                for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.YELLOW);
                        button.setTextColor(Color.BLACK);

                    }

                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);

                break;

            case "Green":

                celebrityQuizLinearLayout.setBackgroundColor(Color.GREEN);

                for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);

                    }

                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.YELLOW);


                break;

            case "Blue":

                celebrityQuizLinearLayout.setBackgroundColor(Color.BLUE);

                for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.RED);
                        button.setTextColor(Color.WHITE);

                    }

                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);

                break;

            case "Red":

                celebrityQuizLinearLayout.setBackgroundColor(Color.RED);

                for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);

                    }

                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);


                break;

            case "Yellow":

                celebrityQuizLinearLayout.setBackgroundColor(Color.YELLOW);

                for (LinearLayout row : rowsOfGuessButtonsInCelebrityQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLACK);
                        button.setTextColor(Color.WHITE);

                    }

                }

                txtAnswer.setTextColor(Color.BLACK);
                txtQuestionNumber.setTextColor(Color.BLACK);

                break;

        }

    }
}


