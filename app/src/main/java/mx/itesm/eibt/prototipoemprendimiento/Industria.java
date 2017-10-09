package mx.itesm.eibt.prototipoemprendimiento;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Industria extends AppCompatActivity {

    /*
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
    */
    LinearLayout container;
    public static final int graphX = 100;
    public static final int graphY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industria);
        container = (LinearLayout) findViewById(R.id.container);
        container.setVerticalScrollBarEnabled(true);
        DatePicker dp = new DatePicker(this);
    }

    private void addNewGraph(LineGraphSeries<DataPoint> series) {
        GraphView graph = new GraphView(this);
        graph.setMinimumHeight(graphX);
        graph.setMinimumHeight(graphY);
        graph.addSeries(series);
        container.addView(graph);
    }
}