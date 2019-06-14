package com.lpwoowatpokpt.quiztemplate.Common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.lpwoowatpokpt.quiztemplate.Model.Question;
import com.lpwoowatpokpt.quiztemplate.Model.User;
import com.lpwoowatpokpt.quiztemplate.UI.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class Common {

    public static String categoryId, categoryName;
    public static FirebaseUser currentUser;
    public static List<Question> questionsList = new ArrayList<>();
    public static List<User> onlineList = new ArrayList<>();

    public static final String EASY = "easy";
    public static final String MEDIUM = "medium";
    public static final String HARD = "hard";
    public static final String SUPERHARD = "super hard";

    public static final String USERS = "Users";
    public static final String CATEGORY = "Category";
    public static final String QUESTIONS = "Questions";
    public static final String QUESTION_SUGGEST = "Question_Suggest";
    public static final String QUESTION_SCORE = "Question_Score";
    public static final String RANKING = "Ranking";
    public static final String MULTIPLAYER = "Muliplayer";

    public static final String CATEGORY_ID = "CategoryID";
    public static final String BALANCE = "balance";

    public static final String FALSE = "false";
    public static final String TRUE = "true";


    //tinyDB
    public static final String TIME = "time";
    public static final String PROGRESS = "progress";
    public static final String THEME_ID = "theme_id";
    public static final String IS_SOUND_ON = "is_sound_on";
    public static final String IS_GAME_ON_TIME = "is_game_on_time";
    public static final String IS_DARK_MODE = "is_dark_mode";
    public static final String FONT = "font";

    //tinyDB create question
    public static final String IS_IMAGE_QUESTION = "is_image_question";
    public static final String TEXT_QUESTION = "text_question";
    public static final String TEXT_IMAGE_QUESTION = "text_image_question";
    public static final String IMAGE_PATH = "image_path";
    public static final String ANSWER_1 = "answer_1";
    public static final String ANSWER_2 = "answer_2";
    public static final String ANSWER_3 = "answer_3";
    public static final String ANSWER_4 = "answer_4";
    public static final String CORRECT_ANSWER = "correct_answer";
    public static final String HINT = "hint";

    private static final String PACKAGE_NAME = "com.lpwoowatpokpt.quiztemplate";


    public static final String IN_PROGRESS = "waiting for confirmation";

    public static final int PICK_IMAGE_REQUEST = 231;

    public static final String FONT_DEFAULT = "fonts/OptimusPrinceps.ttf";

    private static FirebaseDatabase mDatabase;

    private static FirebaseStorage mStorage;


    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance();
        return mDatabase;
    }

    public static FirebaseStorage getStorage() {
        if (mStorage == null)
            mStorage = FirebaseStorage.getInstance();
        return mStorage;
    }


    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo[]info = connectivityManager.getAllNetworkInfo();

            if (info != null)
            {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static void SaveEditText(final TinyDB tinyDB, final String key, final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
             tinyDB.putString(key, editText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                tinyDB.putString(key, editText.getText().toString());
            }
        });
    }

    public static void BackToMainMenu(Activity activity, FirebaseAuth auth){
        Intent intent = new Intent(activity, HomeActivity.class);
        currentUser = auth.getCurrentUser();
        activity.startActivity(intent);
    }


    public static void ShowToast(Context context, String message){
        Toast toast = Toast.makeText(context,
                message, Toast.LENGTH_LONG);
        toast.show();
    }
}
