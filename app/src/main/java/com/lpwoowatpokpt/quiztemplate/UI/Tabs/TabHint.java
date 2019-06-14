package com.lpwoowatpokpt.quiztemplate.UI.Tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabHint extends Fragment {

    TinyDB tinyDB;

    EditText edtHint;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tinyDB = new TinyDB(this.getContext());
    }

    public static TabHint getInstance()
    {
        return new TabHint();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_tab_hint, container, false);

        edtHint = myView.findViewById(R.id.edtHint);

        Common.SaveEditText(tinyDB, Common.HINT, edtHint);

        if(tinyDB.getString(Common.HINT).equals(""))
            edtHint.setText("");
        else
            edtHint.setText(tinyDB.getString(Common.HINT));

        return myView;
    }
}
