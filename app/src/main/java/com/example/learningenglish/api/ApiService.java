package com.example.learningenglish.api;


import com.example.learningenglish.model.Favorite;
import com.example.learningenglish.model.History;
import com.example.learningenglish.model.LoginRequest;
import com.example.learningenglish.model.Topic;
import com.example.learningenglish.model.UpdateTopicRequest;
import com.example.learningenglish.model.User;
import com.example.learningenglish.model.Vocabulary;
import com.example.learningenglish.model.ChangeUserPassword;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    //Lấy danh sách từ vựng
    @GET("api/vocabulary")
    Call<ApiResponse<List<Vocabulary>>> getVocabulary();

    //Lấy danh sách từ vựng theo tìm kiếm
    @GET("api/vocabulary/search")
    Call<ApiResponse<List<Vocabulary>>> searchVocabulary(@Query("word") String word);

    //Lấy danh sách từ vựng yêu thích theo username
    @GET("api/favorite/{username}")
    Call<ApiResponse<List<Vocabulary>>> getFavorites(@Path("username") String username);

    //Thêm từ vựng vào yêu thích
    @POST("api/favorite/add")
    Call<ApiResponse<Void>> addFavorite(@Body Favorite favorite);

    //Xóa từ vựng khỏi yêu thích
    @POST("api/favorite/delete")
    Call<ApiResponse<Void>> removeFavorite(@Body Favorite favorite);

    // Thêm từ vựng
    @POST("api/vocabulary")
    Call<ApiResponse<Vocabulary>> addVocabulary(@Body Vocabulary vocabulary);

    // Sửa từ vựng
    @PUT("api/vocabulary/{word}")
    Call<ApiResponse<Void>> updateVocabulary(@Path("word") String word, @Body Vocabulary vocabulary);

    // Xóa từ vựng
    @DELETE("api/vocabulary")
    Call<ApiResponse<Void>> deleteVocabulary(@Query("word") String word);

    //Lấy thông tin user theo username
    @GET("api/user/getByUsername/{username}")
    Call<ApiResponse<User>> getUserByUsername(@Path("username") String username);

    //Đăng nhập
    @POST("api/user/login")
    Call<ApiResponse<User>> userLogin(@Body LoginRequest loginRequest);

    //Tạo tài khoản
    @POST("/api/user")
    Call<ApiResponse<User>> registerUser(@Body User user);

    //Quên mật khẩu(gửi mã)
    @POST("api/user/forgotPassword")
    Call<ApiResponse<String>> forgotPassword(@Body User user);

    //Reset mật khẩu(khi quên mật khẩu)
    @PUT("api/user/resetPassword")
    Call<ApiResponse<Void>> resetPassword(@Body Map<String, String> body);

    //Thay đổi thông tin cá nhân
    @PUT("api/user/infor/{username}")
    Call<ApiResponse<Void>> updateUserInfo(@Path("username") String username, @Body User user);

    //Đổi mật khẩu
    @PUT("api/user/changePassword")
    Call<ApiResponse<Void>> changePassword(@Body ChangeUserPassword changeUserPassword);

    //Lấy danh sacis user hoạt động
    @GET("/api/user/active")
    Call<ApiResponse<List<User>>> getActiveUsers();

    //Lấy danh sách user không hoạt động
    @GET("/api/user/inactive")
    Call<ApiResponse<List<User>>> getInactiveUsers();

    //Đổi trạng thái user
    @PUT("/api/user/status/{username}")
    Call<ApiResponse<Void>> changeUserStatus(@Path("username") String username);

    //Lấy danh sách chủ đề
    @GET("api/topic")
    Call<ApiResponse<List<Topic>>> getTopics();

    //Lấy danh sách từ vựng theo chủ đề
    @POST("api/topic/getVocabulary")
    Call<ApiResponse<List<Vocabulary>>> getVocabularyByTopic(@Body Map<String, String> body);

    //Thêm chủ đề
    @POST("api/topic")
    Call<ApiResponse<Void>> addTopic(@Body Topic topic);

    //Sửa chủ đề
    @PUT("api/topic")
    Call<ApiResponse<Void>> updateTopic(@Query("name") String name, @Body UpdateTopicRequest request);

    //Xóa chủ đề
    @DELETE("api/topic")
    Call<ApiResponse<Void>> deleteTopic(@Query("name") String name);

    //Lấy lịch sử
    @GET("api/history")
    Call<ApiResponse<List<History>>> getHistory(@Query("username") String username, @Query("type") String type);

    //Lưu lịch sử
    @POST("api/history")
    Call<ApiResponse<Void>> saveHistory(@Body History history);
}
