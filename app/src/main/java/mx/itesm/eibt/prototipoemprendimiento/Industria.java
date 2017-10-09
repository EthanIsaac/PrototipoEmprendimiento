package mx.itesm.eibt.prototipoemprendimiento;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Pattern;

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
    int id_industry;
    String nombre, year, day, month;
    public static final int graphX = 70;
    public static final int graphY = 100;
    GraphView graph;
    DataPoint[] dataPoints;
    SQLConnect con;
    Spinner registers;
    ArrayList<String> singleRegister;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industria);
        con = new SQLConnect();
        container = (LinearLayout) findViewById(R.id.containerIndustria);
        graph = (GraphView) findViewById(R.id.graph);
        id_industry = Integer.parseInt(getIntent().getStringExtra("id_industry"));
        nombre = getIntent().getStringExtra("name_industry");
        year = getIntent().getStringExtra("year");
        if(getIntent().getStringExtra("month").length()==1)
        {
            month= "0"+getIntent().getStringExtra("month");
        }
        else
        {
            month = getIntent().getStringExtra("month");
        }
        if(getIntent().getStringExtra("day").length()==1)
        {
            day = "0"+getIntent().getStringExtra("day");
        }
        else
        {
            day = getIntent().getStringExtra("day");
        }
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        registers = (Spinner) findViewById(R.id.registers);
        registers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position)
                {
                    case 0:
                        cargarPromedioRegistros();
                        break;
                    default:
                        String[] parts = registers.getSelectedItem().toString().split(Pattern.quote("-"));
                        cargarRegistro(parts[0]);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        singleRegister =new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, singleRegister);
        registers.setAdapter(adapter);
        con.searchRegisters();
        cargarPromedioRegistros();
    }

    private void cargarPromedioRegistros() {
    }

    private void cargarRegistro(String part) {
        con.searchPollutant(Integer.parseInt(part));
    }

    private void setGraphSeries(BarGraphSeries<DataPoint> series) {
        graph.removeAllSeries();
        graph.addSeries(series);
        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });
        series.setSpacing(50);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.GRAY);
    }
    private void mensajeError(final String s) {
        AlertDialog alertDialog = new AlertDialog.Builder(Industria.this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(s);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(s.equals("No hay registros de ese día."))
                    finish();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    public class SQLConnect extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(final String... params) {
            return "";
        }
        void searchRegisters()
        {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    String sqlUrl = "jdbc:mysql://us-mm-auto-sl-dfw-01-a.cleardb.net:3306/ibmx_aef025c3fdc18cf";
                    String sqlUser = "b2c0322fec0d80";
                    String sqlPassword = "dcb20b1f";
                    try {
                        int contador=0;
                        Class.forName("com.mysql.jdbc.Driver").newInstance();
                        Connection con = DriverManager.getConnection(sqlUrl, sqlUser, sqlPassword);
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery("SELECT id_register, date FROM register WHERE id_industry=" + id_industry + " AND date LIKE '" + year + "-" + month + "-" + day + "%';");
                        singleRegister.clear();
                        singleRegister.add("Mostrar promedio del día");
                        while(rs.next())
                        {
                            String[] parts = rs.getString("date").split(Pattern.quote(" "));
                            singleRegister.add(rs.getInt("id_register")+"-" + parts[1]);
                            contador++;
                        }
                        if(contador==0)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mensajeError("No hay registros de ese día.");
                                }
                            });
                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                        con.close();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //mensajeError("Error en la conexión con la base de datos.");
                            }
                        });
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        void searchPollutant(final int id)
        {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    String sqlUrl = "jdbc:mysql://us-mm-auto-sl-dfw-01-a.cleardb.net:3306/ibmx_aef025c3fdc18cf";
                    String sqlUser = "b2c0322fec0d80";
                    String sqlPassword = "dcb20b1f";
                    try {
                        int contador=0;
                        Class.forName("com.mysql.jdbc.Driver").newInstance();
                        Connection con = DriverManager.getConnection(sqlUrl, sqlUser, sqlPassword);
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery("SELECT p.name, p.description, pr.quantity_liters FROM pollutant p, pollutant_register pr WHERE pr.id_register=" + id + " AND pr.id_pollutant=p.id_pollutant;");
                        ArrayList<DataPoint> data = new ArrayList<>();
                        while(rs.next())
                        {
                            data.add(new DataPoint(contador,rs.getDouble("pr.quantity_liters")));
                            contador++;
                        }
                        if(contador==0)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    singleRegister.clear();
                                    mensajeError("No hay registros de ese día.");
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                        else
                        {
                            dataPoints = new DataPoint[contador];
                            for(int i = 0; i<contador;i++)
                            {
                                dataPoints[i]= data.get(i);
                            }
                            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);
                            setGraphSeries(series);
                        }
                        con.close();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //mensajeError("Error en la conexión con la base de datos.");
                            }
                        });
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }
}
