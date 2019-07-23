package android.example.photo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    //String SECRET_ACCESS_KEY = "7d770b5724deac228aadb9b9159ebe575c6c9db5dd3abb14a38b01a96e2523ac";

    @GET("photos/random?client_id=7d770b5724deac228aadb9b9159ebe575c6c9db5dd3abb14a38b01a96e2523ac")
    Call<Post> getPosts();//убрать ключ в Query
    //Call<Post> getPosts();
}
