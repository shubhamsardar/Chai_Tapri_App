package in.co.tripin.chai_tapri_app.POJOs.Responces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TapriTypePojo {

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
        @SerializedName("isFixed")
        @Expose
        private Integer isFixed;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getIsFixed() {
            return isFixed;
        }

        public void setIsFixed(Integer isFixed) {
            this.isFixed = isFixed;
        }

    }
}
