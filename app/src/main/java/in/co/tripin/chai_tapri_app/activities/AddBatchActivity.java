package in.co.tripin.chai_tapri_app.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import in.co.tripin.chai_tapri_app.R;

public class AddBatchActivity extends AppCompatActivity {

    private EditText editTextBatchName,editTextBatchNo;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_batch);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add Batch");
        editTextBatchName= (EditText)findViewById(R.id.editTextBatchName);
        editTextBatchNo = (EditText)findViewById(R.id.editTextBatchNo);
        buttonSave = (Button)findViewById(R.id.buttonSave);
    }
}
