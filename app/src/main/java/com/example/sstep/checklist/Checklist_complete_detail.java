package com.example.sstep.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sstep.R;

public class Checklist_complete_detail extends AppCompatActivity {

    TextView title, staffName;
    ImageButton menuBtn;
    ImageView picture, backBtn;
    EditText comment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_complete_detail);
        title = findViewById(R.id.checkComDetail_checkListNameText);
        menuBtn = findViewById(R.id.checkComDetail_menuBtn);
        staffName = findViewById(R.id.checkComDetail_checkListStaffNameText);
        picture = findViewById(R.id.checkComDetail_picture);
        comment = findViewById(R.id.checkComDetail_commentET);
        backBtn = findViewById(R.id.checkComDetail_backBtn);

        //메뉴 버튼
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.checklist_detail_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.checklist_detail_delete) {
                            //db 삭제및 페이지 닫기?
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        //뒤로가기 버튼
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CheckList.class);
                startActivity(intent);
                finish();
            }
        });
        //제목
        title.setText("마감시 에어컨 끄기");// db에서 받은 제목 표시

        //해당 스태프 이름 표시
        staffName.setText("000");

        //사진 적용
        //picture.setImageResource();

        //댓글 작성 작성자, 내용 받기
        comment.getText();


    }

}