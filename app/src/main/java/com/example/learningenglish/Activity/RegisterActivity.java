package com.example.learningenglish.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglish.R;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.api.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.learningenglish.R;
import com.example.learningenglish.model.User;

public class    RegisterActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword, edtConfirmPassword, edtEmail, edtName;
    private Button btnRegister;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        control();
        event();

    }

    private void event() {
        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> Register());
    }


    private void control() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        btnRegister = findViewById(R.id.btn_Register);
        btnBack = findViewById(R.id.btnBack);
    }

    private void Register() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String name = edtName.getText().toString().trim();

        //TextUtils.isEmpty(username) kiểm tra cả null, username.isEmpty() chỉ kiểm tra chuỗi rỗng
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.contains(" ") || password.contains(" ") || confirmPassword.contains(" ") || email.contains(" ")) {
            Toast.makeText(RegisterActivity.this, "Tên đăng nhập và mật khẩu không được chứa khoảng trắng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Xác nhận lại mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            return;
        }


        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        User user = new User(username, password, name, email);
        Call<ApiResponse<User>> call = apiService.registerUser(user);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}