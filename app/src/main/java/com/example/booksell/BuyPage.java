package com.example.booksell;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BuyPage extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private String searchOption = "bookName";
    RecyclerView recyclerView;
    Button btn_my, btn_chat,btn_sell,btn_buy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_page);
        btn_my = findViewById(R.id.btn_my);
        btn_chat = findViewById(R.id.btn_chat);
        btn_sell = findViewById(R.id.btn_sell);
        btn_buy = findViewById(R.id.btn_buy);
        recyclerView = findViewById(R.id.recyclerview);
        // Firestore 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance();

        recyclerView.setAdapter(new RecyclerViewAdapter(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 스피너 초기화
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // "이름" 선택
                        searchOption = "bookName";
                        break;
                    case 1: // "번호" 선택
                        searchOption = "bookAuthor";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 검색 버튼 초기화
        Button searchBtn = findViewById(R.id.searchBtn);
        EditText searchWord = findViewById(R.id.searchWord);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchWord.getText().toString();
                RecyclerViewAdapter adapter = (RecyclerViewAdapter) recyclerView.getAdapter();
                adapter.search(keyword, searchOption);
            }
        });

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyPage.this,BuyPage.class);
                startActivity(intent);
            }
        });

        btn_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyPage.this,SellPage.class);
                startActivity(intent);
            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyPage.this,ChatListPage.class);
                startActivity(intent);
            }
        });

        btn_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyPage.this,MyPage.class);
                startActivity(intent);
            }
        });
    }
}
