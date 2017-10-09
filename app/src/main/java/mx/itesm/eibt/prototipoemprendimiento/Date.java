package mx.itesm.eibt.prototipoemprendimiento;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

public class Date extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        Button back = (Button) findViewById(R.id.backIndustry);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        TextView nameIndustry = (TextView)findViewById(R.id.nameIndustry);
        nameIndustry.setText(getIntent().getStringExtra("name_industry"));
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                cargarIndustria(getIntent().getStringExtra("user"),getIntent().getStringExtra("id_industry"),getIntent().getStringExtra("name_industry"), year, month+1, dayOfMonth);

            }
        });
    }
    private void cargarIndustria(String user, String id_industry, String name, int year, int month, int dayOfMonth) {
        Intent intent = new Intent(this, Industria.class);
        intent.putExtra("user",user);
        intent.putExtra("id_industry",id_industry);
        intent.putExtra("name_industry", name);
        intent.putExtra("year", ""+year);
        intent.putExtra("month", ""+month);
        intent.putExtra("day", ""+dayOfMonth);
        startActivity(intent);
    }
}