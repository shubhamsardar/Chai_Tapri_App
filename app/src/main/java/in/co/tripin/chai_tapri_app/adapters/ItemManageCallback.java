package in.co.tripin.chai_tapri_app.adapters;

import in.co.tripin.chai_tapri_app.POJOs.Models.Item;
import in.co.tripin.chai_tapri_app.POJOs.Responces.HubItemsPojo;

public interface ItemManageCallback {

    public void onitemEdit(int position,Item item);

}
