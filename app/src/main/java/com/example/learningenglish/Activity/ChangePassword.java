package com.example.learningenglish.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglish.R;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.model.ChangeUserPassword;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {
    private TextView txtName;
    private EditText edtPassword, edtNewPassword, edtConfirmPassword;
    private Button  btnConfirm;
    private ImageButton btnBack;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);

        control();
        event();

    }

    private void event() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        String name = intent.getStringExtra("name");
        txtName.setText(name);

        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> changePassword());
    }

    private void control() {
        txtName = findViewById(R.id.txtName);
        edtPassword = findViewById(R.id.edt_Password);
        edtNewPassword = findViewById(R.id.edt_NewPassword);
        edtConfirmPassword = findViewById(R.id.edt_ConfirmPassword);
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void changePassword() {
        String oldPassword = edtPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới không trùng khớp", Toast.LENGTH_LONG).show();
            return;
        }

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_LONG).show();
            return;
        }

        if (oldPassword.contains(" ") || newPassword.contains(" ") || confirmPassword.contains(" ")) {
            Toast.makeText(this, "Mật khẩu không được chứa dấu cách", Toast.LENGTH_LONG).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Void>> call = apiService.changePassword(new ChangeUserPassword(username, oldPassword, newPassword));

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
//                if (response.isSuccessful()) {
                ApiResponse<Void> apiResponse = response.body();
                if (apiResponse != null) {
                    if (apiResponse.getStatus() == 200) {
                        Toast.makeText(ChangePassword.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ChangePassword.this, apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChangePassword.this, "Response body is null", Toast.LENGTH_LONG).show();
                }
//                } else {
//                    Toast.makeText(ChangePassword.this, "Error: " + response.code() + response.message(), Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(ChangePassword.this, "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
