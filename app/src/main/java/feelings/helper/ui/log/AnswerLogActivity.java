package feelings.helper.ui.log;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import feelings.helper.R;

public class AnswerLogActivity extends AppCompatActivity {

    private AnswerLogAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_log_activity);

        RecyclerView rv = (RecyclerView) findViewById(R.id.answer_log_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnswerLogAdapter(this);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.changeCursor(null);
    }
}
