package com.example.sstep.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.sstep.R;
import com.example.sstep.money.Money_Ceo;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CheckList extends AppCompatActivity {

    private static final String TAG = "CheckList";
    private TabLayout tabs_layout;
    private ViewPager viewpager;
    private CheckListFragmentAdapter adapter;
    ImageButton preDay, refreshBtn, calendarBtn, nextDay, plusBtn;
    static int changeDate = 0;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

    Calendar calendar = new GregorianCalendar(); //오늘날짜 받기

    Calendar todayCalendar = new GregorianCalendar(); //오늘날짜 저장

    String chkDate = mFormat.format(calendar.getTime());
    private String selectedItem;
    private CheckList_Spinner spinnerAdapter;

    Spinner spinner;

    private List<String> list = new ArrayList<>();

    TextView todayText, weekDay; //오늘 날짜 표시 텍스트뷰, 요일 텍스트뷰
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);
        preDay = findViewById(R.id.checkList_preDayBtn);
        refreshBtn = findViewById(R.id.checkList_refreshBtn);
        calendarBtn = findViewById(R.id.checkList_calendarBtn);
        nextDay = findViewById(R.id.checkList_nextDayBtn);
        tabs_layout = findViewById(R.id.checkList_tabs_layout);
        viewpager = findViewById(R.id.checkList_viewpager);
        todayText = findViewById(R.id.checkList_yearMonthDayText);
        weekDay = findViewById(R.id.checkList_weekofdayText);
        spinner = findViewById(R.id.checkList_selectCategory);
        plusBtn = findViewById(R.id.checkList_plusBtn);

        adapter = new CheckListFragmentAdapter(getSupportFragmentManager(),1);

        // AlarmFragmentAdapter 에 컬렉션 담기
        adapter.addFragment(new checkList1Fragment());
        adapter.addFragment(new checkList2Fragment());

        // ViewPager Fragment 연결
        viewpager.setAdapter(adapter);

        // ViewPager TabLayout 연결
        tabs_layout.setupWithViewPager(viewpager);

        tabs_layout.getTabAt(0).setText("미완료");
        tabs_layout.getTabAt(1).setText("완료");

        //오늘 날짜 표시
        todayText.setText(chkDate);

        //오늘 요일 표시
        try {
            weekDay.setText(getDateDay());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        //전날로 가기
        preDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate -=1;
                calendar.add(Calendar.DATE, -1);
                chkDate = mFormat.format(calendar.getTime());
                todayText.setText(chkDate);
                try {
                    weekDay.setText(getDateDay());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //다음날로 가기
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate +=1;
                calendar.add(Calendar.DATE, 1);
                chkDate = mFormat.format(calendar.getTime());
                todayText.setText(chkDate);
                try {
                    weekDay.setText(getDateDay());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //오늘로 날짜 변경
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int refresh = -(changeDate);
                changeDate =0;
                calendar = todayCalendar;
                calendar.add(Calendar.DATE, refresh);
                chkDate = mFormat.format(calendar.getTime());
                todayText.setText(chkDate);

                try {
                    weekDay.setText(getDateDay());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                refresh = 0;
            }
        });

        //카테고리 설정 스피너
        String[] items = {"홀", "커피머신", "오전"};
        for(int i =0; i<items.length; i++) {
            list.add(items[i]);
        }

        spinnerAdapter = new CheckList_Spinner(this, list);
        spinner.setAdapter(spinnerAdapter);

        // 스피너 클릭 리스너
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 어댑터에서 정의한 메서드를 통해 스피너에서 선택한 아이템의 이름을 받아온다
                selectedItem = spinnerAdapter.getItem();
                //Toast.makeText(CheckList.this, "선택한 아이템 : " + selectedItem, Toast.LENGTH_SHORT).show();
                // 어댑터에서 정의하는 게 귀찮다면 아래처럼 구할 수도 있다
                // getItemAtPosition()의 리턴형은 Object이므로 String 캐스팅이 필요하다
                String otherItem = (String) spinner.getItemAtPosition(position);
                Log.e(TAG, "getItemAtPosition() - 선택한 아이템 : " + otherItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
        //메인화면으로 가는 버튼

        //체크리스트 플러스 버튼
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CheckList_write.class);
                startActivity(intent);
                finish();
            }
        });
    }


    //날짜에 해당하는 요일 설정
    public static String getDateDay() throws Exception {

        String day = "";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, changeDate );

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day="일요일";
                break;
            case 2:
                day="월요일";
                break;
            case 3:
                day="화요일";
                break;
            case 4:
                day="수요일";
                break;
            case 5:
                day="목요일";
                break;
            case 6:
                day="금요일";
                break;
            case 7:
                day="토요일";
                break;

        }

        return day;
    }



}