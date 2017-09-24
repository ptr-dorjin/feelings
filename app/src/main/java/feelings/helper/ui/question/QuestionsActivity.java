package feelings.helper.ui.question;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import feelings.helper.R;
import feelings.helper.ui.log.AnswerLogActivity;

import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;

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
            return showAnswerLog(null);
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void showPopupMenu(final View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.questions_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.show_log) {
                    int questionId = (int) v.getTag();
                    return showAnswerLog(questionId);
                } else {
                    return false;
                }
            }
        });
        popup.show();
    }

    private boolean showAnswerLog(Integer questionId) {
        Intent intent = new Intent(this, AnswerLogActivity.class);
        if (questionId != null) {
            intent.putExtra(QUESTION_ID_PARAM, questionId);
        }
        startActivity(intent);
        return true;
    }
}
