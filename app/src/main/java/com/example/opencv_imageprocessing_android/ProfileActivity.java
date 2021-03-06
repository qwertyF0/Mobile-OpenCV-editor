package com.example.opencv_imageprocessing_android;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//****************************************************************************************************
public class ProfileActivity extends AppCompatActivity {
    //XML bileşen tanımlama
    private TextView userNameTxt;
    private Button changeEmailBttn;
    private Button changePasswordBttn;
    private Button signOutBttn;
    private FloatingActionButton GeriTusu;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authListener;
    private String str;

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
    //****************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Kullanıcıyı Çağırma
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    finish();
                }
            }
        };
        // Üyelik İşlemleri
        userNameTxt = (TextView) findViewById(R.id.userNameTxt);
        changeEmailBttn = (Button) findViewById(R.id.changeEmailBttn);
        changePasswordBttn = (Button) findViewById(R.id.changePasswordBttn);
        signOutBttn = (Button) findViewById(R.id.signOutBttn);
        GeriTusu =(FloatingActionButton) findViewById(R.id.Geritusu) ;


        GeriTusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,HomePage.class);
                startActivity(intent);
            }
        });

        userNameTxt.setText("Kullanıcı Adı:" + " " + auth.getCurrentUser().getEmail());

        signOutBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOutFunc(); // sign out
                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        changeEmailBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = "Lütfen yeni e-posta adresini giriniz.";
                changeEmailOrPasswordFunc(str, true);

            }
        });

        changePasswordBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = "Lütfen yeni şifreyi giriniz.";
                changeEmailOrPasswordFunc(str, false);
            }
        });

    }
    private void signOutFunc() {
        auth.signOut();
    }

    //E-mail ve Password'un Değişmesi
    private void changeEmailOrPasswordFunc(String title, final boolean option) {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                ProfileActivity.this);
        final EditText edit = new EditText(ProfileActivity.this);
        builder.setPositiveButton(getString(R.string.change_txt), null);
        builder.setNegativeButton(getString(R.string.close_txt), null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        edit.setLayoutParams(lp);
        if(!option){  // password type
            edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        builder.setTitle(title);
        builder.setView(edit);

        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if(edit.getText().toString().isEmpty()){

                            edit.setError("Lütfen ilgili alanı doldurunuz!");
                        }else{

                            if(option){

                                changeEmail();
                            }else{

                                changePassword();
                            }
                        }
                    }
                });
            }
            private void changePassword() {

                firebaseUser.updatePassword(edit.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Şifre değiştirildi.", Toast.LENGTH_LONG).show();
                                    signOutFunc();
                                    Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    edit.setText("");
                                    Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }

            private void changeEmail() {

                firebaseUser.updateEmail(edit.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "E-posta değiştirildi.", Toast.LENGTH_LONG).show();
                                    signOutFunc(); // sign out
                                    Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    edit.setText("");
                                    Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        mAlertDialog.show();
    }
}


