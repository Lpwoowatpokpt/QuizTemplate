package com.lpwoowatpokpt.quiztemplate.UI.Fragments;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.Common.Utils;
import com.lpwoowatpokpt.quiztemplate.R;

import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    View myFragment;

    TinyDB tinyDB;

    int min = 30;
    int max = 90;
    long timeout;

    LinearLayout timeSelectionLayout;

    TextView soundTxt, nightModeTxt;
    SwitchCompat soundSwitch, nightModeSwitch, durationSwitch;


    SeekBar seekBar;
    TextView  durationTxt;

    public static SettingsFragment newinstance()
    {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tinyDB = new TinyDB(getContext());
    }

    @SuppressLint({"SetTextI18n", "NewApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_settings, container, false);

        timeSelectionLayout = myFragment.findViewById(R.id.time_selection_layout);


        soundTxt = myFragment.findViewById(R.id.sound_txt);
        soundSwitch = myFragment.findViewById(R.id.turn_on_sound);

        nightModeTxt = myFragment.findViewById(R.id.nightModeTxt);
        nightModeSwitch = myFragment.findViewById(R.id.nightModeSwitch);

        durationTxt = myFragment.findViewById(R.id.durationTxt);
        durationSwitch = myFragment.findViewById(R.id.durationSwitch);


        if (tinyDB.getBoolean(Common.IS_SOUND_ON, true)){
            SoundOn();
            soundSwitch.setChecked(true);
        } else SoundOff();

        if (tinyDB.getBoolean(Common.IS_DARK_MODE, true)){
            DarkModeOn();
            nightModeSwitch.setChecked(true);
        } else DarkModeOff();

        if (tinyDB.getBoolean(Common.IS_GAME_ON_TIME, true)){
            IsPlayOnTime();
            durationSwitch.setChecked(true);
        } else NotPlayOnTime();


        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                   SoundOn(); else SoundOff();
            }
        });

        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    DarkModeOn(); else  DarkModeOff();

                Utils.ChangeToTheme(Objects.requireNonNull(getActivity()));

            }
        });

        durationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    IsPlayOnTime(); else  NotPlayOnTime();
            }
        });


        seekBar = myFragment.findViewById(R.id.time_question_seekbar);
        seekBar.setMax(max);
        seekBar.setProgress(tinyDB.getInt(Common.PROGRESS) - min);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                progress +=min;

                timeout = (long) (progress * 1000);
                tinyDB.putInt(Common.PROGRESS, progress);
                tinyDB.putLong(Common.TIME, timeout);
                durationTxt.setText(getString(R.string.default_time) + " " + String.valueOf(tinyDB.getLong(Common.TIME, 0) / 1000) + " " + getString(R.string.seconds));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return myFragment;
    }

    @SuppressLint("SetTextI18n")
    private void IsPlayOnTime(){
        tinyDB.putBoolean(Common.IS_GAME_ON_TIME, true);
        timeSelectionLayout.setVisibility(View.VISIBLE);
        durationTxt.setText(getString(R.string.default_time) + " " + String.valueOf(tinyDB.getLong(Common.TIME, 0) / 1000) + " "  + getString(R.string.seconds));
    }

    private void NotPlayOnTime(){
        tinyDB.putBoolean(Common.IS_GAME_ON_TIME, false);
        timeSelectionLayout.setVisibility(View.GONE);
        durationTxt.setText(getString(R.string.play_on_time_off));
    }


    private void DarkModeOn(){
        nightModeTxt.setText(getString(R.string.nightModeOn));
        tinyDB.putBoolean(Common.IS_DARK_MODE, true);
        tinyDB.putInt(Common.THEME_ID,1);
    }


    private void DarkModeOff(){
        nightModeTxt.setText(getString(R.string.nightModeOff));
        tinyDB.putBoolean(Common.IS_DARK_MODE, false);
        tinyDB.putInt(Common.THEME_ID,0);
    }

    private void SoundOn(){
        soundTxt.setText(getString(R.string.sound_on));
        tinyDB.putBoolean(Common.IS_SOUND_ON, true);
    }

    private void SoundOff(){
        soundTxt.setText(getString(R.string.sound_off));
        tinyDB.putBoolean(Common.IS_SOUND_ON, false);
    }



}