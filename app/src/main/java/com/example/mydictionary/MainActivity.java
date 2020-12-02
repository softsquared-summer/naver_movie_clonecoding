package com.example.mydictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<Movie> movieArrayList = new ArrayList<>();//무비 리스트
    ArrayList<Integer> years = new ArrayList<>();
    ArrayAdapter<Integer> spinnerArrayAdapter1, spinnerArrayAdapter2;

    MovieAdapter adapter;//어댑터
    GridView gvMovieList;//그리드뷰
    EditText searchText;
    Button btn_search, btn_logout;
    ImageView iv_profile;
    TextView tv_nickname, tv_id, tv_birthday, tv_email, tv_name;

    private String strNickname, strProfile, strId, strEmail, strName, strBirthday;

    private Spinner spin_yearfrom, spin_yearto;
    private String keyword;
    private int display = 10, yearfrom = 2019, yearto = 2019;//
    private int page = 0;                           // 페이징변수. 초기 값은 0 이다.
    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    private final int OFFSET = 10;                  // 한 페이지마다 로드할 데이터 갯수.
    private ProgressBar progressBar;
    private boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수


    Gson gson = new Gson();
    RequestManager requestManager;//글라이드 매니저
    InputMethodManager imm;//키보드 매니저

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAppKeyHash();// 키 해시 가져오는 부분

        //카카오 프로필 설정
        tv_nickname = findViewById(R.id.kakao_nickname);
        tv_id = findViewById(R.id.kakao_id);
        iv_profile = findViewById(R.id.kakao_profile);

//        페이스북 프로필 설정
        tv_email = findViewById(R.id.facebook_email);
        tv_birthday = findViewById(R.id.facebook_birth);
        tv_name = findViewById(R.id.facebook_name);

        //인텐트 받아오기
        Intent intent = getIntent();

        if (getIntent().getBooleanExtra("Login_kakao", false)) {

            strNickname = intent.getStringExtra("k_name");
            strProfile = intent.getStringExtra("k_profile");

            //받아온 값 설정
            tv_nickname.setText(strNickname);
            Glide.with(this).load(strProfile).apply(new RequestOptions().circleCrop().centerCrop()).into(iv_profile);


        } else {
        }

        System.out.println("LoginFacebook 밖"+getIntent().getBooleanExtra("Login_facebook",false));

        if (getIntent().getBooleanExtra("Login_facebook", false)) {
            strId = intent.getStringExtra("facebook_id");
            strEmail = intent.getStringExtra("facebook_email");
            strBirthday = intent.getStringExtra("facebook_birthday");
            strName = intent.getStringExtra("facebook_name");

            tv_id.setText(strId);
            tv_email.setText(strEmail);
            tv_birthday.setText(strBirthday);
            tv_name.setText(strName);

        } else {
        }



        searchText = (EditText) findViewById(R.id.searchText);//검색창
        btn_search = (Button) findViewById(R.id.searchBtn);//검색버튼
        btn_logout = findViewById(R.id.btn_logout);//로그아웃버튼
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);//키보드 조절


        //그리드뷰 연결과정
        requestManager = Glide.with(getApplicationContext());//glide사용
        gvMovieList = (GridView) findViewById(R.id.gridView);
        adapter = new MovieAdapter(movieArrayList, this, requestManager);
        gvMovieList.setAdapter(adapter);//리스트와 그리드뷰연결

        //스피너 연결
        spin_yearfrom = (Spinner) findViewById(R.id.yearfrom);
        spin_yearto = (Spinner) findViewById(R.id.yearto);

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            ListPopupWindow window1 = (ListPopupWindow) popup.get(spin_yearfrom);
            ListPopupWindow window2 = (ListPopupWindow) popup.get(spin_yearto);
            window1.setHeight(700); //pixel
            window2.setHeight(700); //pixel
        } catch (Exception e) {
            e.printStackTrace();
        }

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1900; i--) {
            years.add(i);
        }

        spinnerArrayAdapter1 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, years);
        spinnerArrayAdapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, years);

        //스피너 어뎁터 연결
        spin_yearfrom.setAdapter(spinnerArrayAdapter1);
        spin_yearto.setAdapter(spinnerArrayAdapter2);
        //스피너 리스너 설정
        spin_yearfrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                yearfrom = years.get(i).intValue();
                System.out.println("yearFrom" + yearfrom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("spin_yearfrom NothingSelected 호출" + yearfrom);
            }
        });

        spin_yearto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                yearto = years.get(i).intValue();
                System.out.println("yearTo" + yearto);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("spin_yearTo NothingSelected 호출" + yearto);

            }
        });


//spinner to enter this list to
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        gvMovieList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // 1. OnScrollListener.SCROLL_STATE_IDLE : 스크롤이 이동하지 않을때의 이벤트(즉 스크롤이 멈추었을때).
                // 2. lastItemVisibleFlag : 리스트뷰의 마지막 셀의 끝에 스크롤이 이동했을때.
                // 3. mLockListView == false : 데이터 리스트에 다음 데이터를 불러오는 작업이 끝났을때.
                // 1, 2, 3 모두가 true일때 다음 데이터를 불러온다.
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
                    // 화면이 바닦에 닿을때 처리
                    // 로딩중을 알리는 프로그레스바를 보인다.
                    progressBar.setVisibility(View.VISIBLE);

                    // 다음 데이터를 불러온다.
                    getItem();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // firstVisibleItem : 화면에 보이는 첫번째 리스트의 아이템 번호.
                // visibleItemCount : 화면에 보이는 리스트 아이템의 갯수
                // totalItemCount : 리스트 전체의 총 갯수
                // 리스트의 갯수가 0개 이상이고, 화면에 보이는 맨 하단까지의 아이템 갯수가 총 갯수보다 크거나 같을때.. 즉 리스트의 끝일때. true
                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }

            public void getItem() {

                // 리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 mLockListView 를 true로 설정한다.
                mLockListView = true;

                // display가 최대 100까지이므로 10씩 더한다. 그리고 100 이후엔 더이상 로딩할 필요가 없음
                if (display < 100) {
                    display += OFFSET;
                    if (yearfrom <= yearto) {
                        getUserInfo(getApplicationContext(), keyword, display, yearfrom, yearto);
                    } else {
                        Toast.makeText(MainActivity.this, "비정상적인 연도입니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }

            }
        });


        //그리드뷰 아이템 클릭 리스너
        gvMovieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("Actor", movieArrayList.get(position).getActor());
                intent.putExtra("Director", movieArrayList.get(position).getDirector());
                intent.putExtra("Image", movieArrayList.get(position).getImage());
                intent.putExtra("Link", movieArrayList.get(position).getLink());
                intent.putExtra("SubTitle", movieArrayList.get(position).getSubtitle());
                intent.putExtra("Title", movieArrayList.get(position).getTitle());
                intent.putExtra("Rating", movieArrayList.get(position).getUserRating());
                intent.putExtra("Date", movieArrayList.get(position).getPubDate());
                startActivity(intent);
            }
        });

        //검색 버튼 리스너
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = searchText.getText().toString();
                display = 10;// display 초기화
                if (keyword.getBytes().length <= 0) {
                    Toast.makeText(MainActivity.this, "제목을 입력하세요!", Toast.LENGTH_SHORT).show();
                } else {
                    if (yearfrom <= yearto) {
                        getUserInfo(getApplicationContext(), keyword, display, yearfrom, yearto);
                        gvMovieList.smoothScrollToPosition(0);
                    } else {
                        Toast.makeText(MainActivity.this, "비정상적인 연도입니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);


            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                onClickLogout();
            }
        });
    }//end onCreate

    public void getUserInfo(final Context context, final String _keyword, final int _display, final int _yearfrom, final int _yearto) {

        final String clientID = "HN1jYDid_SrM2hqpwpcP";
        final String clientSecret = "WPHRSIPotX";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    System.out.println("내가 보낸 쿼리 확인해보기"+"https://openapi.naver.com/v1/search/movie.json?query=" + _keyword + "&display=" + _display + "&yearfrom=" + _yearfrom + "&yearto=" + _yearto);
                    Request request = new Request.Builder()
                            .addHeader("X-Naver-Client-Id", clientID)
                            .addHeader("X-Naver-Client-Secret", clientSecret)
                            .url("https://openapi.naver.com/v1/search/movie.json?query=" + _keyword + "&display=" + _display + "&yearfrom=" + _yearfrom + "&yearto=" + _yearto)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // For the example, you can show an error dialog or a toast
                                    // on the main UI thread
                                    Toast.makeText(context, "통신실패", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String result = response.body().string();
                            JsonParser parser = new JsonParser();//jason paser
                            JsonObject jsonObject = (JsonObject) parser.parse(result);
                            JsonArray jsonArray = jsonObject.getAsJsonArray("items");
                            System.out.println("jsonArray :"+jsonArray);
                            movieArrayList.clear();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                Movie movies = gson.fromJson(jsonArray.get(i).toString(), Movie.class);
                                movies.setTitle(stripHtml(movies.getTitle()));// 태그 제거
                                movieArrayList.add(movies);
                            }
                            System.out.println("Response가 오고 movieArray에 입력완료");

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {

                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                    mLockListView = false;
                                }
                            });
                        }
                    });


                } catch (Exception e) {
                    System.out.println("Response가 오지 않은 경우 출력");
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    //키 해시 가져오는 함수
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    //카카오 로그아웃 메소드
    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
