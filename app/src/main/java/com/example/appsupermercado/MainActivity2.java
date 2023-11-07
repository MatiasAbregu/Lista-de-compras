package com.example.appsupermercado;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.CompoundButtonCompat;

import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity2 extends AppCompatActivity {

    private TableLayout tbUsu;
    private ResultSet rs;
    private Conexion cn;
    private Statement st;
    private PreparedStatement pst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        tbUsu = findViewById(R.id.tablaUsu);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        cargarDatos(4, "");
    }

    public void cargarDatos(int opc, String categoria) {
        try {
            cn = new Conexion();
            st = cn.conectar().createStatement();
            tbUsu.removeAllViews();

            if (opc == 1) { //Cargar por necesarios
                rs = st.executeQuery("SELECT * FROM productos WHERE Necesario = 'Si'");
                cargarTabla(rs, 1, "");
            } else if (opc == 2) { //Cargar otra opción
                rs = st.executeQuery("SELECT * FROM productos WHERE Categoria = '" + categoria + "' ORDER BY Necesario DESC");
                cargarTabla(rs, 2, categoria);
            } else if (opc == 3) { //Buscar por nombre
                rs = st.executeQuery("SELECT * FROM productos WHERE NomProd = '" + categoria + "'");
                cargarTabla(rs, 3, categoria);
            } else { //Cargar todo
                rs = st.executeQuery("SELECT * FROM productos ORDER BY Necesario DESC");
                cargarTabla(rs, 4, "");
            }
        } catch (SQLException e) {
        } finally {
            cn.cerrarConexion();
        }
    }

    private void cargarTabla(ResultSet rs, int opcionMarcada, String parametroCondicion) {
        try {
            while (rs.next()) {
                View registro = LayoutInflater.from(this).inflate(R.layout.filas_layout, null, false);
                TextView colCodigo = registro.findViewById(R.id.colCodigo);
                TextView colNombre = registro.findViewById(R.id.colNombre);
                TextView colCategoria = registro.findViewById(R.id.colCategoria);
                CheckBox colNecesario = registro.findViewById(R.id.colNecesario);

                CompoundButtonCompat.setButtonTintList(colNecesario, ColorStateList.valueOf(Color.rgb(255, 255, 255)));
                colCodigo.setText(String.valueOf(rs.getInt(1)));
                colNombre.setText(rs.getString(2));
                colCategoria.setText(rs.getString(3));
                if (rs.getString(4).equals("Si")) colNecesario.setChecked(true);

                colNecesario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        int cod = Integer.parseInt(String.valueOf(colCodigo.getText()));
                        if (cn == null) cn = new Conexion();
                        try {
                            pst = cn.conectar().prepareStatement("UPDATE productos SET Necesario = ? WHERE Codigo = ?");
                            if (b) pst.setString(1, "Si");
                            else pst.setString(1, "No");
                            pst.setInt(2, cod);
                            pst.executeUpdate();
                            pst.close();
                            cn.cerrarConexion();
                            tbUsu.removeAllViews();
                            if (opcionMarcada == 1) cargarDatos(1, "");
                            else if (opcionMarcada == 2) cargarDatos(2, parametroCondicion);
                            else if (opcionMarcada == 3) cargarDatos(3, parametroCondicion);
                            else cargarDatos(4, "");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                tbUsu.addView(registro);
            }
        } catch (SQLException e) {
        }
    }

    public void buscarProducto(View v) {
        AlertDialog.Builder buscar = new AlertDialog.Builder(this);
        View layoutB = getLayoutInflater().inflate(R.layout.buscar_layout, null);
        Spinner sp = layoutB.findViewById(R.id.spinner);
        ArrayList<String> categorias = new ArrayList<>();
        categorias.clear();

        categorias.add("Todos");
        categorias.add("Necesario");
        try {
            if (cn == null) cn = new Conexion();
            st = cn.conectar().createStatement();
            rs = st.executeQuery("SELECT Categoria FROM productos");

            while (rs.next()) {
                boolean repetido = false;
                for (int i = 0; i < categorias.size(); i++) {
                    if (categorias.get(i).equals(rs.getString(1))) {
                        repetido = true;
                        break;
                    }
                }

                if (!repetido) categorias.add(rs.getString(1));
            }

        } catch (SQLException e) {
        } finally {
            cn.cerrarConexion();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categorias);
        sp.setAdapter(adapter);

        AlertDialog buscarD = buscar.setView(layoutB).create();
        Button btn = layoutB.findViewById(R.id.button4);
        //sp (Ya creado)
        EditText et = layoutB.findViewById(R.id.editTextText);

        btn.setOnClickListener(view -> {
            if (!et.getText().toString().equals("")) {
                cargarDatos(3, et.getText().toString());
            } else {
                if (sp.getSelectedItem().toString().equals("Necesario")) {
                    cargarDatos(1, "");
                } else if (sp.getSelectedItem().toString().equals("Todos")) {
                    cargarDatos(4, "");
                } else {
                    cargarDatos(2, sp.getSelectedItem().toString());
                }
            }
            Toast.makeText(this, "¡Se realizo la búsqueda! Si no aparecen resultados es porque no existe.",
                    Toast.LENGTH_SHORT).show();
            buscarD.dismiss();
        });

        buscarD.show();
    }

    public void crearProducto(View v) {
        AlertDialog.Builder crear = new AlertDialog.Builder(this);
        View layoutC = getLayoutInflater().inflate(R.layout.crear_layout, null);

        Button btn = layoutC.findViewById(R.id.button4);
        EditText et1 = layoutC.findViewById(R.id.editTextText),
                et2 = layoutC.findViewById(R.id.editTextText2);
        RadioButton rb1 = layoutC.findViewById(R.id.radioButton2),
                rb2 = layoutC.findViewById(R.id.radioButton);

        AlertDialog ventana = crear.setView(layoutC).create();

        btn.setOnClickListener(view -> {
            if (et1.getText().equals("") || et2.getText().equals("") || (!rb1.isChecked() && !rb2.isChecked()))
                Toast.makeText(this, "Rellena todos los campos antes de continuar.", Toast.LENGTH_SHORT).show();
            else {
                if (cn == null) cn = new Conexion();
                try {
                    pst = cn.conectar().prepareStatement("INSERT INTO productos (NomProd, Categoria, Necesario) " +
                            "VALUES (?,?,?)");
                    pst.setString(1, et1.getText().toString());
                    pst.setString(2, et2.getText().toString());
                    if (rb1.isChecked()) pst.setString(3, "Si");
                    else pst.setString(3, "No");

                    pst.executeUpdate();
                    Toast.makeText(this, "¡Registro creado con éxito!", Toast.LENGTH_SHORT).show();
                    ventana.dismiss();
                    cargarDatos(4, "");
                } catch (SQLException ex) {
                } finally {
                    cn.cerrarConexion();
                }
            }
        });

        ventana.show();
    }

    public void modificarProducto(View v) {
        AlertDialog.Builder modif = new AlertDialog.Builder(this);
        View layoutM = getLayoutInflater().inflate(R.layout.modificar_layout, null);
        AlertDialog modificar = modif.setView(layoutM).create();

        AtomicInteger codigo = new AtomicInteger();
        EditText et = layoutM.findViewById(R.id.editTextText3), et2 = layoutM.findViewById(R.id.editTextText),
                et3 = layoutM.findViewById(R.id.editTextText2);

        Button btn = layoutM.findViewById(R.id.button5),
                btnModif = layoutM.findViewById(R.id.button4);

        RadioButton rb1 = layoutM.findViewById(R.id.radioButton2), rb2 = layoutM.findViewById(R.id.radioButton);

        btn.setOnClickListener(view -> {
            if (et.getText().toString().equals("")) {
                Toast.makeText(this, "Completa el campo de búsqueda por código antes de continuar.", Toast.LENGTH_SHORT).show();
            } else {
                if (cn == null) cn = new Conexion();
                try {
                    pst = cn.conectar().prepareStatement("SELECT NomProd, Categoria, Necesario FROM productos " +
                            "where Codigo = ?");
                    pst.setInt(1, Integer.parseInt(et.getText().toString()));
                    rs = pst.executeQuery();

                    if (rs.next()) {
                        codigo.set(Integer.parseInt(et.getText().toString()));
                        et2.setText(rs.getString(1));
                        et3.setText(rs.getString(2));

                        if (rs.getString(3).equals("Si")) rb1.setChecked(true);
                        else rb2.setChecked(true);
                        btnModif.setEnabled(true);
                    } else {
                        Toast.makeText(this, "Ese producto no existe. Prueba con otro código.", Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException ex) {
                } finally {
                    cn.cerrarConexion();
                }
            }
        });

        btnModif.setOnClickListener(view -> {
            if(codigo.get() <= 0){
                Toast.makeText(this, "Busca un producto válido antes de continuar.", Toast.LENGTH_SHORT).show();
            } else {
                if(cn == null) cn = new Conexion();
                try {
                    pst = cn.conectar().prepareStatement("UPDATE productos SET NomProd = ?, Categoria = ?, " +
                            "Necesario = ? WHERE Codigo = " + codigo);
                    pst.setString(1, et2.getText().toString());
                    pst.setString(2, et3.getText().toString());
                    if(rb1.isChecked()) pst.setString(3, "Si");
                    else pst.setString(3, "No");
                    pst.executeUpdate();
                    modificar.dismiss();
                    Toast.makeText(this, "¡Registro modificado con éxito!", Toast.LENGTH_SHORT).show();
                    cargarDatos(4, "");
                } catch (SQLException e) {
                }
            }
        });

        modificar.show();
    }

}