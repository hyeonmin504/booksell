package com.example.booksell;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MyInfo extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private TextView tv_nicname,tv_email,tv_login;
    Button btn_back;
    boolean isLoggedIn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        tv_login = findViewById(R.id.tv_login);
        firestore = FirebaseFirestore.getInstance();
        tv_email = findViewById(R.id.tv_email);
        tv_nicname = findViewById(R.id.tv_nickname);
        btn_back = findViewById(R.id.btn_back);

        // SharedPreferences를 통해 로그인 상태와 사용자 정보 가져오기
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // 로그인한 경우, Firestore에서 사용자 정보 가져오기
            String email = preferences.getString("email", ""); // 이메일 가져오기
            String nickname = preferences.getString("nickname", ""); // 닉네임 가져오기

            // Firestore에서 책 가격(bookPrice) 가져오기
            firestore.collection("users")
                    .whereEqualTo("email", email)
                    .whereEqualTo("nickname", nickname)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                // Firestore에서 데이터가 있으면 해당 데이터를 사용하여 텍스트뷰에 설정
                                String emailFromFirestore = task.getResult().getDocuments().get(0).getString("email");
                                String nicknameFromFirestore = task.getResult().getDocuments().get(0).getString("nickname");

                                tv_email.setText("이메일: " + emailFromFirestore);
                                tv_nicname.setText("닉네임: " + nicknameFromFirestore);
                            }
                        }
                    });

            // 뷰 설정
            tv_login.setVisibility(View.INVISIBLE);
            tv_email.setVisibility(View.VISIBLE);
            tv_nicname.setVisibility(View.VISIBLE);
        } else {
            // 사용자가 로그인하지 않은 경우, 로그인 화면으로 이동
            tv_login.setVisibility(View.VISIBLE);
            tv_email.setVisibility(View.INVISIBLE);
            tv_nicname.setVisibility(View.INVISIBLE);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoggedIn) {
                    // 사용자가 로그인하지 않은 경우, 로그인 화면으로 이동
                    Intent intent = new Intent(MyInfo.this, loginActivity.class);
                    startActivity(intent);
                } else {
                    // 사용자가 로그인한 경우, 리스트뷰를 보이도록 설정
                    tv_login.setVisibility(View.INVISIBLE);
                    tv_email.setVisibility(View.VISIBLE);
                    tv_nicname.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}