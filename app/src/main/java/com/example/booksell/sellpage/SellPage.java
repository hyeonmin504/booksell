package com.example.booksell.sellpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.example.booksell.mypage.MyPage;
import com.example.booksell.R;
import com.example.booksell.buypage.BuyPage;
import com.example.booksell.chatpage.ChatListPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

//판매 페이지
public class SellPage extends AppCompatActivity {
    private FirebaseFirestore firestore;
    Button btn_my, btn_chat,btn_sell,btn_buy;
    private Button btn_sub;
    EditText edt_publisher,edt_publisher_date,edt_state;
    private EditText edt_bookname,edt_writer,edt_price;
    CheckBox CB_state1,CB_state2,CB_state3,CB_write1,CB_write2,CB_write3;
    private final String TAG = this.getClass().getSimpleName();
    ImageView imageview;
    private Uri selectedImageUri;
    boolean isLoggedIn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_page);
        firestore = FirebaseFirestore.getInstance();
        btn_my = findViewById(R.id.btn_my);
        btn_chat = findViewById(R.id.btn_chat);
        btn_sell = findViewById(R.id.btn_sell);
        btn_buy = findViewById(R.id.btn_buy);
        btn_sub = findViewById(R.id.btn_sub);
        edt_bookname = findViewById(R.id.edt_bookname);
        edt_writer = findViewById(R.id.edt_writer);
        edt_publisher = findViewById(R.id.publisher);
        edt_publisher_date = findViewById(R.id.publisher_date);
        edt_state = findViewById(R.id.state);
        edt_price = findViewById(R.id.edt_price);
        CB_state1 = findViewById(R.id.CB_state1);
        CB_state2 = findViewById(R.id.CB_state2);
        CB_state3 = findViewById(R.id.CB_state3);
        CB_write1 = findViewById(R.id.CB_write1);
        CB_write2 = findViewById(R.id.CB_write2);
        CB_write3 = findViewById(R.id.CB_write3);


        // 로그인한 사람 정보를 가져오기 db에 저장하기 위해
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        imageview = findViewById(R.id.iv_book);

        //제출 버튼을 누르면 현재 작성한 정보를 전부 bookInfo DB에 저장
        btn_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLoggedIn) {
                    String bookName = edt_bookname.getText().toString();
                    String bookAuthor = edt_writer.getText().toString();
                    double price = Double.parseDouble(edt_price.getText().toString());
                    String publisher = edt_publisher.getText().toString();
                    String publisher_date = edt_publisher_date.getText().toString();
                    String state = edt_state.getText().toString();
                    boolean state1 = CB_state1.isChecked();
                    boolean state2 = CB_state2.isChecked();
                    boolean state3 = CB_state3.isChecked();
                    boolean write1 = CB_write1.isChecked();
                    boolean write2 = CB_write2.isChecked();
                    boolean write3 = CB_write3.isChecked();
                    String email = preferences.getString("email","");
                    String imageUrl = null;
                    Log.d(email, "onClick: ");
                    // 데이터를 Firestore에 업로드

                    if (selectedImageUri != null) {
                        uploadImageToFirebaseStorage(selectedImageUri, bookName, bookAuthor, price, publisher, publisher_date, state,
                                state1, state2, state3, write1, write2, write3, email);
                    } else {
                        // 이미지가 선택되지 않은 경우, 기존 방식으로 데이터 업로드
                        uploadBookData(bookName, bookAuthor, price, publisher, publisher_date, state,
                                state1, state2, state3, write1, write2, write3, email, null);
                    }
                }else {
                    Toast.makeText(SellPage.this, "로그인을 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //이미지를 업로드 하기 위한 코드
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new ActivityResultCallback<ActivityResult>(){
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Log.e(TAG, "result : " + result);
                            Intent intent = result.getData();
                            Log.e(TAG, "intent : " + intent);
                            Uri uri = intent.getData();
                            Log.e(TAG, "uri : " + uri);
                            if(uri != null) {
                                selectedImageUri = uri;
                                Glide.with(SellPage.this)
                                        .load(uri)
                                        .into(imageview);
                            }
                        }
                    }
                });

        imageview.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            launcher.launch(intent);
        });

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellPage.this, BuyPage.class);
                startActivity(intent);
            }
        });

        btn_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellPage.this,SellPage.class);
                startActivity(intent);
            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellPage.this, ChatListPage.class);
                startActivity(intent);
            }
        });

        btn_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellPage.this, MyPage.class);
                startActivity(intent);
            }
        });
    }

    private void uploadBookData(String title, String author, double price, String publisher, String publisher_date, String state,
                                boolean state1, boolean state2, boolean state3, boolean write1, boolean write2, boolean write3, String email, String imageUrl) {
        // DB에 업로드할 책 데이터 생성
        Book book = new Book(title, author, price, publisher, publisher_date, state, state1, state2, state3, write1, write2, write3, email, imageUrl); // Book 클래스의 생성자에 title과 author 전달

        // DB에 데이터 업로드
        firestore.collection("bookInfo")
                .document() // Firestore에서 자동 생성된 고유 ID로 문서 추가
                .set(book, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "제출 완료되었습니다!", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "제출 중 오류가 발생했습니다!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // 이미지를 Firebase Storage에 업로드하고 URL을 얻는 함수
    private void uploadImageToFirebaseStorage(Uri imageUri, String title, String author, double price, String publisher, String publisher_date, String state,
                                              boolean state1, boolean state2, boolean state3, boolean write1, boolean write2, boolean write3, String email) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("book_images/" + UUID.randomUUID().toString());

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                uploadBookData(title, author, price, publisher, publisher_date, state, state1, state2, state3, write1, write2, write3, email, imageUrl);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(SellPage.this, "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}