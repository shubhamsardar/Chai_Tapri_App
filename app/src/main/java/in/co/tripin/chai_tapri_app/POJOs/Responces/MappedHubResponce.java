package in.co.tripin.chai_tapri_app.POJOs.Responces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MappedHubResponce {

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

        @SerializedName("hubId")
        @Expose
        private String hubId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("address")
        @Expose
        private Address address;

        public String getHubId() {
            return hubId;
        }

        public void setHubId(String hubId) {
            this.hubId = hubId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public class Address {

            @SerializedName("landmark")
            @Expose
            private String landmark;
            @SerializedName("city")
            @Expose
            private String city;
            @SerializedName("state")
            @Expose
            private String state;
            @SerializedName("country")
            @Expose
            private String country;
            @SerializedName("addressLine2")
            @Expose
            private String addressLine2;
            @SerializedName("addressLine1")
            @Expose
            private String addressLine1;
            @SerializedName("flatSociety")
            @Expose
            private String flatSociety;
            @SerializedName("nickname")
            @Expose
            private String nickname;

            public String getLandmark() {
                return landmark;
            }

            public void setLandmark(String landmark) {
                this.landmark = landmark;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getAddressLine2() {
                return addressLine2;
            }

            public void setAddressLine2(String addressLine2) {
                this.addressLine2 = addressLine2;
            }

            public String getAddressLine1() {
                return addressLine1;
            }

            public void setAddressLine1(String addressLine1) {
                this.addressLine1 = addressLine1;
            }

            public String getFlatSociety() {
                return flatSociety;
            }

            public void setFlatSociety(String flatSociety) {
                this.flatSociety = flatSociety;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

        }

    }
}
