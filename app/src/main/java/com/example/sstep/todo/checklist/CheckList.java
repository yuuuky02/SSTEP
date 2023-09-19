package com.example.sstep.todo.checklist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.sstep.CalendarDialog;
import com.example.sstep.R;
import com.example.sstep.home.Home_Ceo;
import com.example.sstep.todo.checklist.checklist_api.CategoryApiService;
import com.example.sstep.todo.checklist.checklist_api.CategoryResponseDto;
import com.example.sstep.user.member.NullOnEmptyConverterFactory;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckList extends AppCompatActivity {

    Intent intent;
    private static final String TAG = "CheckList";
    private TabLayout tabs_layout;
    private ViewPager viewpager;
    private CheckListFragmentAdapter adapter;
    ImageButton backBtn, preDay, refreshBtn, calendarBtn, nextDay, plusBtn;
    static int changeDate = 0;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

    Calendar calendar = new GregorianCalendar(); //오늘날짜 받기

    String chkDate = mFormat.format(calendar.getTime());
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private String selectedItem;
    private CheckList_Spinner spinnerAdapter;
    String firstCategoryName;
    long firstCategoryId;
    String secondCategoryName;
    long secondCategoryId;
    String thirdCategoryName ;
    long thirdCategoryId;
    long storeId;

    //카테고리 저장및 프레그먼트로 보내기
    Map<String, Integer> dictionary = new HashMap<>();
    CheckListViewModel viewModel;
    CheckListViewModel2 viewModel2;



    Spinner spinner;

    private List<String> list = new ArrayList<>();

    TextView todayText, weekDay; //오늘 날짜 표시 텍스트뷰, 요일 텍스트뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);
        viewModel = new ViewModelProvider(CheckList.this).get(CheckListViewModel.class);
        viewModel2 = new ViewModelProvider(CheckList.this).get(CheckListViewModel2.class);
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
        backBtn = findViewById(R.id.checkList_backBtn);

        // 뒤로가기
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), Home_Ceo.class);
                startActivity(intent);
                finish();
            }
        });

        adapter = new CheckListFragmentAdapter(getSupportFragmentManager(),1);

        // AlarmFragmentAdapter 에 컬렉션 담기
        adapter.addFragment(new checkList1Fragment());
        adapter.addFragment(new checkList2Fragment());

        // ViewPager Fragment 연결
        viewpager.setAdapter(adapter);

        // ViewPager TabLayout 연결
        tabs_layout.setupWithViewPager(viewpager);

        tabs_layout.getTabAt(0).setText("미완료"); //db에서 리스트 갯수 받아서 표시 해야됨
        tabs_layout.getTabAt(1).setText("완료"); //db에서 리스트 갯수 받아서 표시 해야됨 or 프래그먼트에서 갯수 받아오기

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
                viewModel2.setDate(todayText.getText().toString());
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
                viewModel2.setDate(todayText.getText().toString());
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
                calendar.add(Calendar.DATE, refresh);
                chkDate = mFormat.format(calendar.getTime());
                todayText.setText(chkDate);
                viewModel2.setDate(todayText.getText().toString());

                try {
                    weekDay.setText(getDateDay());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                refresh = 0;
            }
        });

        //카테고리 스피너 내용 받기
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Retrofit 클라이언트 생성
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://ec2-3-35-10-138.ap-northeast-2.compute.amazonaws.com:3306/")
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                // Retrofit 인터페이스 사용
                CategoryApiService apiService = retrofit.create(CategoryApiService.class);

                storeId = 1;

                try {
                    Call<Set<CategoryResponseDto>> call = apiService.getCategories(storeId);
                    retrofit2.Response<Set<CategoryResponseDto>> response = call.execute();
                    if (response.isSuccessful()) {
                        final Set<CategoryResponseDto> categories1 = response.body();

                        // UI 업데이트를 메인 스레드에서 수행
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {


                                Set<CategoryResponseDto> categories = null;
                                categories = categories1;
                                List<String> items = new ArrayList<>();

                                Iterator<CategoryResponseDto> iterator = categories.iterator();
                                if (iterator.hasNext()) {
                                    CategoryResponseDto firstCategory = iterator.next();
                                    items.add(firstCategory.getName());
                                    firstCategoryId = firstCategory.getId();
                                    dictionary.put(firstCategory.getName(), (int)firstCategoryId);

                                }
                                // 두 번째 카테고리 가져오기
                                if (iterator.hasNext()) {
                                    CategoryResponseDto secondCategory = iterator.next();
                                    items.add(secondCategory.getName());
                                    secondCategoryId = secondCategory.getId();
                                    dictionary.put(secondCategory.getName(), (int)secondCategoryId);
                                }

                                // 세 번째 카테고리 가져오기
                                if (iterator.hasNext()) {
                                    CategoryResponseDto thirdCategory = iterator.next();
                                    items.add(thirdCategory.getName());
                                    thirdCategoryId = thirdCategory.getId();
                                    dictionary.put(thirdCategory.getName(), (int)thirdCategoryId);
                                }

                                for(int i =0; i<items.size(); i++) {
                                    list.add(items.get(i));
                                }

                                //카테고리 설정 스피너
                                spinnerAdapter = new CheckList_Spinner(CheckList.this, list);
                                spinner.setAdapter(spinnerAdapter);
                                String selectedItem = (String) spinner.getSelectedItem();

                                viewModel.setCategoryIdLiveData(dictionary.get(selectedItem));
                                viewModel2.setDate(todayText.getText().toString());


                            }
                        });
                    } else {
                        Log.e(TAG, "요청 실패: " + response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "요청 실패: " + e.getMessage());
                }
            }
        }).start();




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
                spinner.setSelection(position);
                viewModel.setCategoryIdLiveData(dictionary.get(selectedItem));
                viewModel2.setDate(todayText.getText().toString());
                //Toast.makeText(CheckList.this, otherItem, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "getItemAtPosition() - 선택한 아이템 : " + otherItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        //체크리스트 플러스 버튼
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CheckList_write.class);
                startActivity(intent);
                finish();
            }
        });

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendarDialog(calendarBtn);
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

    // 달력 다이얼로그 띄우기
    public void showCalendarDialog(final ImageButton imageButton) {
        CalendarDialog calendarDialog = new CalendarDialog(CheckList.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 사용자가 날짜를 선택하면 호출되는 콜백 메서드
                        // 여기에 선택한 날짜 처리 코드를 작성합니다.
                        String dateString = year + "년 " + (month + 1) + "월 " + dayOfMonth + "일";

                        try {
                            String dayOfWeek = getDateDay(year, month, dayOfMonth);
                            weekDay.setText(dayOfWeek);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        todayText.setText(dateString);
                    }
                });
        // 다이얼로그 띄우기
        calendarDialog.show();

    }

    // 선택한 날짜의 요일 가져오기
    public String getDateDay(int year, int month, int dayOfMonth) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        String[] weekDays = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekString = weekDays[dayOfWeek - 1];
        viewModel2.setDate(todayText.getText().toString());

        return dayOfWeekString;
    }


}