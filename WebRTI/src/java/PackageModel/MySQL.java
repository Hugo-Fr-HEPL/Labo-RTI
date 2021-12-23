package PackageModel;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQL {
    private static Connection con;
    private static Statement instruc;
    
    public static Connection MySQL_Connexion(String BDD, String port, String IP, String user, String password) {
        System.out.println("Essai de connexion JDBC");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Driver successfully registered !");
        } 
        catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL non chargé " + e.getMessage());
        }

        try {
            con = DriverManager.getConnection("jdbc:mysql://" + IP + ":" + port + "/" + BDD, user, password);
            System.out.println("Connexion à la base de donnée ok");
            instruc = con.createStatement();

            return con;
        }
        catch (SQLException e) {
            System.out.println("Erreur JDBC-OBCD : " + e.getMessage() + " ** " + e.getSQLState() + "--\n\n");
            return null;
        }
    }
    
    public static ResultSet MySQL_Request(String req)
    {
        String type = req.substring(0, req.indexOf(" "));
        type=type.toUpperCase();
        ResultSet rs = null;

        try {
            if(type.equals("SELECT"))
                rs = instruc.executeQuery(req);
            else
            {
                if(type.equals("UPDATE") || type.equals("INSERT") || type.equals("DELETE"))
                    instruc.executeUpdate(req);
            }
                
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'exécution de la requête: " + ex);
        }
        
        return rs;
    }
}
