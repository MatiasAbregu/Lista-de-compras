package com.example.appsupermercado;

import java.sql.*;

public class Conexion {

    Connection cn = null;

    public Conexion(){}

    public Connection conectar(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection(
                    "jdbc:mysql://bqbcfpyvhnrryz54htdj-mysql.services.clever-cloud.com:3306/bqbcfpyvhnrryz54htdj",
                    "uguakfy4nrn3q8y1", "GZ10WTArPcTAF4hYGX4o");
            return cn;
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        } finally {
            return cn;
        }
    }

    public void cerrarConexion(){
        try {
            cn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
