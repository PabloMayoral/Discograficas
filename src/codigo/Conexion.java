/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author Pablo Martin
 */
public class Conexion {

    Statement sta;
    Connection conexion;
    BasicDataSource bdSource = new BasicDataSource();
    ResultSet resultadoConsulta;

    public int abrirConexion() {
        //abre la conexion entre la aplicacion y la base de datos
        try {
            conexion = bdSource.getConnection();
            if (conexion != null) {
                System.out.println("Conectado a la BBDD");
            } else {
                System.out.println("No se ha podido conectar a la Base de Datos");
            }
            return 0;
        } catch (Exception ex) {
            System.out.println(ex.toString());
            // Logger.getLogger(conexion.class.getName()).log(Level.SEVERE, null,ex);
            return -1;
        }
    }

    public Conexion() {
        //constructor que implementa al metodo de abrir la conexion de app con la BBDD
        bdSource.setUrl("jdbc:mysql://127.0.0.1/discograficas");
        bdSource.setUsername("root");
        bdSource.setPassword("");

    }

    public int cerrarConexion() {
        //cierra la conexion de la aplicacion con la base de datos
        try {
            conexion.close();
            return 0;
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return -1;
            // Logger.getLogger(conexion.class.getName()).log(Level.SEVERE, null,ex);
        }
    }
    
    public void deleteSong(String nombreCancion) {
        try {
            sta = conexion.createStatement();
            sta.executeUpdate("Delete from canciones where nombre_cancion='" + nombreCancion + "';");
            sta.close();
        } catch (Exception e) {
            System.err.println("No se pudo borrar la cancion");
        }
    }

    public void insertarAlbum(String titulo, String autor, String release) {
        //inserta un album nuevo a la tabla album
        try {
            sta = conexion.createStatement();
            sta.executeUpdate("INSERT INTO albumes (nombre_album, artista,fechaSalida) VALUE ('" + titulo + "' , '" + autor + "' , '" + release + "');");
            sta.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

    public void insertarCancion(String titulo, int album, int duracion) {
        //inserta un album nuevo a la tabla album
        try {
            sta = conexion.createStatement();
            sta.executeUpdate("INSERT INTO canciones ( nombre_cancion, album,duracion) VALUE ('" + titulo + "' , '" + album + "' , '" + duracion + "');");
            sta.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

    public ArrayList<String> consulta_albumes() {
        // este metodo lo usaremos para que se muestre todos los albumes en 
        //un comboBox, para ellos usaremos una arrayList y una query para mostrar 
        //todos los albumes
        ArrayList<String> nombreAlbumes = new ArrayList<String>();
        try {
            sta = conexion.createStatement();
            String query = "SELECT nombre_album FROM albumes ;";
            ResultSet rs = sta.executeQuery(query);

            while (rs.next()) {
                nombreAlbumes.add(rs.getString("nombre_album"));
            }
            rs.close();
            sta.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
        return nombreAlbumes;
    }

    public ArrayList<String> consulta_Song() {
         // este metodo lo usaremos para que se muestre todas las canciones en 
        //un comboBox, para ellos usaremos una arrayList y una query para mostrar 
        //todas las canciones
        ArrayList<String> cancion = new ArrayList<String>();
        try {
            sta = conexion.createStatement();
            String query = "SELECT nombre_cancion from canciones";
            ResultSet rs = sta.executeQuery(query);
            while (rs.next()) {
                cancion.add(rs.getString("nombre_cancion"));
            }
        } catch (Exception e) {
            System.err.println("No se encontraron las canciones");
        }
        return cancion;
    }

    public void createBBDD() {
        //aqui metemos la query que usamos para crear la base de datos, y tendra
        //como finalidad el reseteo de la BBDD dejandola con los valores iniciales
        try {
            sta = conexion.createStatement();
            String[] query = {"DROP DATABASE IF EXISTS DISCOGRAFICAS;",
                "CREATE DATABASE DISCOGRAFICAS;",
                "use DISCOGRAFICAS;",
                "CREATE TABLE IF NOT EXISTS albumes ( id_album int(11) NOT NULL AUTO_INCREMENT ,nombre_album varchar(255) NOT NULL,artista varchar(255) NOT NULL,fechaSalida DATE NOT NULL,PRIMARY KEY (id_album));",
                "CREATE TABLE IF NOT EXISTS canciones (id_cancion int(11) NOT NULL AUTO_INCREMENT ,nombre_cancion varchar(255) NOT NULL,album int(11) NOT NULL,duracion varchar (255) NOT NULL,PRIMARY KEY (id_cancion));",
                "INSERT INTO canciones ( nombre_cancion, album,duracion) VALUES( 'U Said', 1 ,'208'),( 'Problems', 1 ,'207'),( 'Benz truck', 1 ,'196'),( 'yesterday', 2 ,'156');",
                "INSERT INTO albumes (nombre_album, artista,fechaSalida) VALUES('Come over when you are sober', 'Lil peep','2016-08-21'),( 'CryBaby','Lil peep','2016-11-24');",
                "ALTER TABLE canciones ADD FOREIGN KEY (album) REFERENCES albumes(id_album);"};
            for (int i = 0; i < query.length; i++) {
                sta.executeUpdate(query[i]);
            }
            sta.close();
        } catch (Exception e) {
            System.out.println("No se pudo crear la base de datos");
        }
    }

    public String muestraCanciones(String _album) {
        //Este metodo lo usaremos para mostrar las canciones, declaramos un string vacio
        //se realiza la query para mostrar todos los elementos de los albumes y 
        //se le suma al string vacio los resultados de la query 
        String album = "";
        try {
            sta = conexion.createStatement();
            String query = "select c.* , a.* from canciones c inner join albumes a  on c.album = a.id_album WHERE a.nombre_album='" + _album + "'";
            ResultSet rs = sta.executeQuery(query);
            //int i = 0;
            while (rs.next()) {
                album += "Nombre: " + rs.getString("nombre_cancion") + "\n" + "Artista: " + rs.getString("artista") + "\n" + "Duracion: " + rs.getString("duracion") + " seg. \n";
                album += "---------------------------------------------\n";
            }
        } catch (Exception e) {
            System.err.println(e.getMessage().toString());
            System.err.println("No se encontraron los albumes");
        }
        return album;
    }

    public void actualizaCanciones(String nameSong, String newName, int newAlbum, String _newDuration) {
      //este metodo sirve para modificar una cancion, basicamente se declaran los strings necesarios y se realiza la 
      //query adecuada para realizar los cambios en la cancion
        String query = "UPDATE canciones SET";
        try {
            sta = conexion.createStatement();
            query += " nombre_cancion = '" + newName + "'" + ',' + "album='" + newAlbum + "'" + ',' + "duracion='" + _newDuration + "'WHERE nombre_cancion like '" + nameSong + "';";
            System.out.println(query);
            sta.executeUpdate(query);
            sta.close();

        } catch (Exception e) {
            System.err.println("No se pudo actualizar la cancion");
        }
    }

}
