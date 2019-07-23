package android.example.photo;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Post {
    //List<PostDetail> urls;
    @SerializedName("urls")
    @Expose
    private PostDetail urls;
    //JsonObject urls;

    public PostDetail getUrls() {
        return urls;
    }
}

