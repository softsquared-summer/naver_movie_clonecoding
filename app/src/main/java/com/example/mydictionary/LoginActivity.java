package com.example.mydictionary;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.ApiErrorCode;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private SessionCallback sessionCallback;//카카오톡
    private CallbackManager callbackManager;//facebook
    LoginButton facebook_loginButton;//페이스북
    private String facebook_id;
    private String facebook_email;
    private String facebook_birthday;
    private String facebook_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        facebook_loginButton = (LoginButton) findViewById(R.id.login_button);//페이스북
        facebook_loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday"));
        callbackManager = CallbackManager.Factory.create();//facebook 콜백매니저 생성

        //로그인 버튼 콜백 레지스터 할당
        facebook_loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = new GraphRequest().newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            facebook_id = object.getString("id");
                            facebook_email = object.getString("email");
                            facebook_birthday = object.getString("birthday"); // 01/31/1980 format
                            facebook_name = object.getString("name");

                            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                            intent1.putExtra("facebook_id", facebook_id);
                            intent1.putExtra("facebook_email", facebook_email);
                            intent1.putExtra("facebook_birthday", facebook_birthday);
                            intent1.putExtra("facebook_name", facebook_name);
                            intent1.putExtra("Login_facebook", true);
                            startActivity(intent1);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                System.out.println("페이스북 로그인 취소");

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                System.out.println("페이스북 로그인 에러");
            }
        });

        sessionCallback = new SessionCallback();//kakao
        Session.getCurrentSession().addCallback(sessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();//이 함수가 있다면 자동 로그인이 된다. 이걸 주석처리하면 매번 지워줘야한다.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }//현재 액티비티 제거시 콜백도 같이 제거

    //카카오 세션
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(), "세션이 닫혔습니다. 다시 시도해 주세요: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    //사용자 정보 값 넘기기
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("k_name", result.getNickname());
                    intent.putExtra("k_profile", result.getProfileImagePath());
                    intent.putExtra("k_id", result.getId());
                    intent.putExtra("Login_kakao", true);
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


}