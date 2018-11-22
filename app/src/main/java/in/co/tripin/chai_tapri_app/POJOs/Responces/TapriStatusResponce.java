package in.co.tripin.chai_tapri_app.POJOs.Responces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TapriStatusResponce {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("flag")
        @Expose
        private Integer flag;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getFlag() {
            return flag;
        }

        public void setFlag(Integer flag) {
            this.flag = flag;
        }

    }
}
