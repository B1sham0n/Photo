package android.example.photo.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostDetail {
        @SerializedName("raw")
        @Expose
        private String raw;

        @SerializedName("full")
        @Expose
        private String full;

        @SerializedName("regular")
        @Expose
        private String regular;

        @SerializedName("small")
        @Expose
        private String small;

        @SerializedName("thumb")
        @Expose
        private String thumb;

        public String getRaw() {
            return raw;
        }

        public String getFull() {
            return full;
        }

        public String getRegular() {
            return regular;
        }

        public String getSmall() {
            return small;
        }

        public String getThumb() {
            return thumb;
        }

}
