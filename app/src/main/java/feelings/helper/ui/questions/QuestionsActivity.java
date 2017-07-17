package feelings.helper.ui.questions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import feelings.helper.R;

public class QuestionsActivity extends AppCompatActivity {

    public static final String QUESTION_ID_PARAM = "question_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_activity);

        RecyclerView rv = (RecyclerView) findViewById(R.id.questions_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setAdapter(new QuestionsAdapter(this));
    }
}
