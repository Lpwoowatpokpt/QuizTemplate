package com.lpwoowatpokpt.quiztemplate.UI.Tabs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabQuestion extends Fragment {

    TinyDB tinyDB;

    SpotsDialog dialog;

    TextView textSwitch;

    RelativeLayout imageLayout, textLayout;

    SwitchCompat imageSwitch;

    EditText edtQuestion, edtImageQuestion;

    Button addImageBtn;
    ImageView imagePreviewFragment;
    private Uri filePath;
    private Bitmap bitmap;


    public static TabQuestion getInstance()
    {
        return new TabQuestion();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tinyDB = new TinyDB(this.getContext());
        //dialog = new SpotsDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_tab_question, container, false);

        imageSwitch = myView.findViewById(R.id.imageSwitch);

        textSwitch = myView.findViewById(R.id.imageSwitchTxt);

        imageLayout = myView.findViewById(R.id.imageQuestionLayout);
        textLayout = myView.findViewById(R.id.textQuestionLayout);

        imageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    ShowAddImageLayout();
                else
                    ShowAddTextLayout();
            }
        });

        if (tinyDB.getBoolean(Common.IS_IMAGE_QUESTION, true))
            ShowAddImageLayout();
            else
                ShowAddTextLayout();

            edtQuestion = myView.findViewById(R.id.edtQuestion);

            edtImageQuestion = myView.findViewById(R.id.edtImageQuestion);

            Common.SaveEditText(tinyDB, Common.TEXT_QUESTION, edtQuestion);
            Common.SaveEditText(tinyDB, Common.TEXT_IMAGE_QUESTION, edtImageQuestion);

            if(!tinyDB.getString(Common.TEXT_QUESTION).equals(""))
                edtQuestion.setText(tinyDB.getString(Common.TEXT_QUESTION));

            if(!tinyDB.getString(Common.TEXT_IMAGE_QUESTION).equals(""))
                edtImageQuestion.setText(tinyDB.getString(Common.TEXT_IMAGE_QUESTION));

        imagePreviewFragment = myView.findViewById(R.id.question_image);
        if (!tinyDB.getString(Common.IMAGE_PATH).equals(""))
            Picasso.get().load(tinyDB.getString(Common.IMAGE_PATH)).into(imagePreviewFragment);

        addImageBtn = myView.findViewById(R.id.imageUploadBtn);
        addImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   ShowFileChooser();
                }
            });
        return myView;
    }

    private void UploadFile() {
        if (filePath != null)
        {
            dialog.show();
            StorageReference storageReference = Common.getStorage().getReference();

            final StorageReference reference = storageReference.child("images/questions"+ GenerateRandomString() + ".jpeg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[]data = baos.toByteArray();
            UploadTask uploadTask = reference.putBytes(data);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    Picasso.get().load(uri).into(imagePreviewFragment);
                                    tinyDB.putString(Common.IMAGE_PATH, uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           dialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage(getString(R.string.progress) + " " + ((int) + progress) + "%...");
                        }
                    });
        }
    }

    private String GenerateRandomString() {
        return UUID.randomUUID().toString();
    }

    private void ShowFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }


    private void ShowAddTextLayout() {
        imageSwitch.setChecked(false);
        textSwitch.setText(getString(R.string.is_text_question));
        imageLayout.setVisibility(View.GONE);
        textLayout.setVisibility(View.VISIBLE);
        tinyDB.putBoolean(Common.IS_IMAGE_QUESTION, false);
    }

    private void ShowAddImageLayout() {
        imageSwitch.setChecked(true);
        textSwitch.setText(getString(R.string.is_image_question));
        imageLayout.setVisibility(View.VISIBLE);
        textLayout.setVisibility(View.GONE);
        tinyDB.putBoolean(Common.IS_IMAGE_QUESTION, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), filePath);
                UploadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
