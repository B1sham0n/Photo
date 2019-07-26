package android.example.photo.Retrofit;

import android.example.photo.Retrofit.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    //String SECRET_ACCESS_KEY = "7d770b5724deac228aadb9b9159ebe575c6c9db5dd3abb14a38b01a96e2523ac";

    //@GET("photos?client_id=7d770b5724deac228aadb9b9159ebe575c6c9db5dd3abb14a38b01a96e2523ac&per_page=50")
    @GET("photos")
    Call<List<Post>> getPosts(@Query("client_id") String client_id, @Query("per_page") Integer per_page);//убрать ключ в Query
    //Call<Post> getPosts();

    //@GET("photos/{id}?client_id=7d770b5724deac228aadb9b9159ebe575c6c9db5dd3abb14a38b01a96e2523ac")
    @GET("photos/{id}")
    Call<Post> getPostOnId(@Path("id") String id, @Query("client_id") String client_id);
}
