package feelings.helper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import feelings.helper.questions.QuestionService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QuestionService.init(this);
    }
}
