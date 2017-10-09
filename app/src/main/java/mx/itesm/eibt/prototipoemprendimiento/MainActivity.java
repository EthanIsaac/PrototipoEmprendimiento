package mx.itesm.eibt.prototipoemprendimiento;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {
    SQLConnect con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        con = new SQLConnect();
        Button button = (Button) findViewById(R.id.ingresar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                revisarCredenciales();
            }
        });
    }

    public class SQLConnect extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(final String... params) {
            return "";
        }
        void searchUser(final String user, final String password)
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
                            ResultSet rs = st.executeQuery("SELECT user.username, user.password, user.id_industry, industry.name FROM user, industry WHERE user.username='" + user + "';");
                            if(rs.next())
                            {
                                if(password.equals(rs.getString("password")))
                                {
                                    iniciarSesion(user, rs.getInt("user.id_industry"), rs.getString("industry.name"));
                                }
                                else
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mensajeError("Contraseña incorrecta.");
                                        }
                                    });
                                }
                            }
                            else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mensajeError("El usuario no existe.");
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

                    (findViewById(R.id.ingresar)).setClickable(true);
                }
            });
            thread.start();
        }
    }

    private void iniciarSesion(String user, int id_industry, String name) {
        if(id_industry>0) // Industria
        {
            Intent intent = new Intent(this, Industria.class);
            intent.putExtra("user",user);
            intent.putExtra("id_industry",id_industry);
            intent.putExtra("name_industry", name);
            startActivity(intent);
        }
        else // Monitor
        {
            Intent intent = new Intent(this, Monitor.class);
            intent.putExtra("user",user);
            startActivity(intent);
        }
    }

    private void mensajeError(String s) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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

    public void revisarCredenciales()
    {
        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        if(!username.equals(""))
        {
            if(!password.equals(""))
            {
                (findViewById(R.id.ingresar)).setClickable(false);
                con.searchUser(username, password);
            }
            else
            {
                mensajeError("Ingresa una contraseña.");
            }
        }
        else
        {
            mensajeError("Ingresa un usuario.");
        }
    }
}
