package feelings.helper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import feelings.helper.ui.questions.QuestionsAdapter;
import feelings.helper.util.ToastUtil;

public class QuestionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_activity);

        RecyclerView rv = (RecyclerView) findViewById(R.id.questions_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setAdapter(new QuestionsAdapter(this));
    }

    public void onAnswerNow(View view) {
        ToastUtil.showShortMessage("To answer activity", this);
    }

    public void onSetUp(View view) {
        ToastUtil.showShortMessage("To setup activity", this);
    }
}
