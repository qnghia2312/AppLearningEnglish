package com.example.learningenglish.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglish.R;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.model.User;
import com.example.learningenglish.ui.admin.AccountAdmin;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountViewHolder> {

    private List<User> users;
    private Context context;

    public AccountsAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        User user = users.get(position);
        holder.txtUsername.setText(user.getUsername());
        holder.txtName.setText(user.getName());
        holder.txtPermission.setText("Quyền: " + user.getPermission());

        holder.btnChangeStatus.setOnClickListener(v -> changeUserStatus(user, position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    private void changeUserStatus(User user, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Void>> call = apiService.changeUserStatus(user.getUsername());

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        users.remove(position);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Lỗi khi thay đổi trạng thái tài khoản: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Lỗi kết nối: ", t.getMessage() );
            }
        });
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtName, txtPermission;

        ImageButton btnChangeStatus;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtName = itemView.findViewById(R.id.txtName);
            txtPermission = itemView.findViewById(R.id.txtPermission);
            btnChangeStatus = itemView.findViewById(R.id.btnChangeStatus);
        }
    }
}