package mx.itesm.eibt.prototipoemprendimiento;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Monitor extends AppCompatActivity {
    SQLConnect con;
    Spinner results;
    ArrayList<String> industries;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        con = new SQLConnect();
        results = (Spinner) findViewById(R.id.results);
        results.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position!=0)
                {
                    String[] parts = results.getSelectedItem().toString().split(Pattern.quote("-"));
                    industries.clear();
                    adapter.notifyDataSetChanged();
                    cargarIndustria(parts[0],parts[1]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        industries=new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, industries);
        results.setAdapter(adapter);
        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(results.getWindowToken(), 0);
                if(((RadioButton)findViewById(R.id.searchById)).isChecked())
                {
                    EditText id = (EditText)findViewById(R.id.id);
                    buscarPorId(id.getText().toString());
                }
                else if(((RadioButton)findViewById(R.id.searchByName)).isChecked())
                {
                    EditText name = (EditText)findViewById(R.id.name);
                    buscarPorNombre(name.getText().toString());
                }
                else
                {
                    mensajeError("Selecciona una opción de búsqueda.");
                }
            }
        });
        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(v.getId()==R.id.id)
                {
                    ((RadioButton) findViewById(R.id.searchById)).setChecked(true);
                    ((RadioButton) findViewById(R.id.searchByName)).setChecked(false);
                }
                else
                {
                    ((RadioButton) findViewById(R.id.searchByName)).setChecked(true);
                    ((RadioButton) findViewById(R.id.searchById)).setChecked(false);
                }
            }
        };
        EditText searchById = (EditText)findViewById(R.id.id);
        EditText searchByName = (EditText)findViewById(R.id.name);
        searchById.setOnFocusChangeListener(listener);
        searchByName.setOnFocusChangeListener(listener);
        RadioButton radioButtonId = (RadioButton) findViewById(R.id.searchById);
        RadioButton radioButtonName = (RadioButton) findViewById(R.id.searchByName);
        radioButtonId.setClickable(false);
        radioButtonName.setClickable(false);
    }

    private void mensajeError(String s) {
        AlertDialog alertDialog = new AlertDialog.Builder(Monitor.this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(s);
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
        void searchIndustryByName(final String industryName)
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
                        ResultSet rs = st.executeQuery("SELECT id_industry, name FROM industry WHERE name LIKE '%" + industryName + "%';");
                        industries.clear();
                        industries.add("Selecciona una opción");
                        while(rs.next())
                        {
                            industries.add(rs.getInt("id_industry")+"-" + rs.getString("name"));
                            contador++;
                        }
                        if(contador==0)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    industries.clear();
                                    mensajeError("No existe ninguna industria con ese nombre.");
                                    adapter.notifyDataSetChanged();
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
                                mensajeError("Error en la conexión con la base de datos.");
                            }
                        });
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    (findViewById(R.id.search)).setClickable(true);
                }
            });
            thread.start();
        }
        void searchIndustryById(final String industryID)
        {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    String sqlUrl = "jdbc:mysql://us-mm-auto-sl-dfw-01-a.cleardb.net:3306/ibmx_aef025c3fdc18cf";
                    String sqlUser = "b2c0322fec0d80";
                    String sqlPassword = "dcb20b1f";
                    try {
                        Class.forName("com.mysql.jdbc.Driver").newInstance();
                        Connection con = DriverManager.getConnection(sqlUrl, sqlUser, sqlPassword);
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery("SELECT id_industry, name FROM industry WHERE id_industry=" + industryID + ";");
                        if(rs.next())
                        {
                            cargarIndustria(industryID, rs.getString("name"));
                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mensajeError("La industria con ese ID no existe.");
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
                                mensajeError("Error en la conexión con la base de datos.");
                            }
                        });
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    (findViewById(R.id.search)).setClickable(true);
                }
            });
            thread.start();
        }
    }

    private void cargarIndustria(String id_industry, String name) {
        Intent intent = new Intent(this, Date.class);
        intent.putExtra("user", getIntent().getStringExtra("user"));
        intent.putExtra("id_industry", id_industry);
        intent.putExtra("name_industry", name);
        startActivity(intent);
    }

    private void buscarPorNombre(String name) {
        if(!name.equals(""))
        {
            (findViewById(R.id.search)).setClickable(false);
            con.searchIndustryByName(name);
        }
        else
        {
            mensajeError("Ingresa el nombre de la industria.");
        }
    }

    private void buscarPorId(String id) {
        if(!id.equals(""))
        {
            if(android.text.TextUtils.isDigitsOnly(id))
            {
                (findViewById(R.id.search)).setClickable(false);
                con.searchIndustryById(id);
            }
            else
            {
                mensajeError("El id debe ser numérico");
            }
        }
        else
        {
            mensajeError("Ingresa el ID de la industria.");
        }
    }
}
