package in.co.tripin.chai_tapri_app.Model;

public class QRRequestBody {

    String qrCode;

    public QRRequestBody(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
