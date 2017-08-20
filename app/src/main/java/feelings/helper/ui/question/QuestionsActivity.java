package feelings.helper.ui.question;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import feelings.helper.R;
import feelings.helper.schedule.Schedule;
import feelings.helper.ui.log.AnswerLogActivity;
import feelings.helper.util.ToastUtil;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static feelings.helper.ui.schedule.ScheduleActivity.SCHEDULE_PARCEL_KEY;

public class QuestionsActivity extends AppCompatActivity {

    private static final String PERMISSION_RATIONALE = "Write External Storage permission allows us to save application logs. Please allow this permission in App Settings.";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private QuestionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_activity);

        checkPermissions();

        RecyclerView rv = (RecyclerView) findViewById(R.id.questions_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        adapter = new QuestionsAdapter(this);
        rv.setAdapter(adapter);
    }

    /**
     * Called when User clicks Save on ScheduleActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode >= 0) {
            Schedule schedule = data.getParcelableExtra(SCHEDULE_PARCEL_KEY);
            // requestCode = item position from adapter
            adapter.notifyItemChanged(requestCode, schedule);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.questions_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Log button pressed
        if (item.getItemId() == R.id.show_log) {
            startActivity(new Intent(this, AnswerLogActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)) {
                ToastUtil.showLong(this, PERMISSION_RATIONALE);
            }
            ActivityCompat.requestPermissions(QuestionsActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.showLong(this, "Application logs won't be saved.");
            }
        }
    }
}
