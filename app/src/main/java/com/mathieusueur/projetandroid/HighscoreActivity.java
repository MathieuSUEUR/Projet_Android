package com.mathieusueur.projetandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class HighscoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        ListView lvScores = findViewById(R.id.lv_scores);
        TextView tvEmpty = findViewById(R.id.tv_empty);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        List<Score> scores = new DatabaseHelper(this).getTopScores(10);

        if (scores.isEmpty()) {
            lvScores.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < scores.size(); i++) {
                Score s = scores.get(i);
                lines.add(getString(R.string.rank_format, i + 1, s.getName(), s.getPoints()));
            }
            lvScores.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, lines));
        }
    }
}
