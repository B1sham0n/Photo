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

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("created_at")
    @Expose
    private String created_at;
    //JsonObject urls;

    public PostDetail getUrls() {
        return urls;
    }
    public String getId() {
        return id;
    }

    public String getCreated_at() {
        return created_at;
    }
}

