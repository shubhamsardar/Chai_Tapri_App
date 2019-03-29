package in.co.tripin.chai_tapri_app.POJOs.Responces;

public class WalletResponce extends CommonResponse {
    Wallet data;

    public Wallet getData() {
        return data;
    }

    public void setData(Wallet data) {
        this.data = data;
    }

    public class Wallet
    {
        String balance;

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }
    }
}
