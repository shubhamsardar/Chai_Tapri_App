package in.co.tripin.chai_tapri_app.adapters;

public interface ItemSelectionCallback {

    public void onitemAdded(Double cost, int quant);

    public void onItemRemoved(Double cost, int quant);
}
