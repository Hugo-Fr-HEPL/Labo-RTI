package PackageModel;

import java.sql.ResultSet;

public class BDAccess {
    public synchronized void connection()
    {
        MySQL.MySQL_Connexion("bd_airport", "3307", "localhost", "liodrs", "liodrs");
    }
    
    public synchronized ResultSet requeteLogin()
    {
        return MySQL.MySQL_Request("SELECT nomClient, motDePasse, idClient FROM clients");
    }
    
    public synchronized ResultSet requetedate()
    {
        return MySQL.MySQL_Request("SELECT datevol, destination FROM vols ORDER BY datevol");
    }
    
    public synchronized ResultSet requetelieu()
    {
        return MySQL.MySQL_Request("SELECT DISTINCT destination FROM vols");
    }
    
    public synchronized void insertLogin(String nom, String prenom, String mdp)
    {
        MySQL.MySQL_Request("INSERT INTO CLIENTS VALUES(NULL, '" + nom + "', '" + prenom + "', 0, '', '', '" + mdp + "')");
    }
    
    public synchronized ResultSet requetevols(String d, String l)
    {
        return MySQL.MySQL_Request("SELECT numvol, datevol, heuredepart, destination, nomcompagnie FROM vols inner join avions using(idavion) inner join compagnies using (idCompagnie) WHERE datevol = '" + d + "' AND destination = '" + l + "'");                                                
    }
    
    public synchronized ResultSet requeteplaces(String nv)
    {
        return MySQL.MySQL_Request("SELECT nombreAccompagnant from billets where numvol = " + nv);                                                
    }
    
    public synchronized ResultSet requeteSiege(String nv)
    {
        return MySQL.MySQL_Request("select nombrePassager from avions inner join vols using(idavion) where numvol = " + nv);                                                
    }
    
    public synchronized void insertBillets(String cli, String vol, String ac)
    {
       MySQL.MySQL_Request("INSERT INTO BILLETS VALUES(NULL, " + vol + ", " + cli + ", " + ac +", 0, 0)");
    }
    
    public synchronized void updatebillet(String idcl)
    {
        MySQL.MySQL_Request("UPDATE billets SET paye = 1 where idclient = " + idcl);
    }
    
    public synchronized void deletebillet(String idcl)
    {
        MySQL.MySQL_Request("DELETE from billets WHERE idclient = " + idcl + " AND paye = 0");
    }
}
