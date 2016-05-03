
package de.ugoe.cs.cpdp.eval;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.ugoe.cs.util.console.Console;

public class MySQLResultStorage implements IResultStorage {
    private Connection con = null;
    
    public MySQLResultStorage() {
        this("localhost", "3306", "crosspare", "crosspare", "benchmark");
    }
    
    public MySQLResultStorage(String dbHost, String dbPort, String dbName, String dbUser, String dbPass) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" +
                    dbName + "?" + "user=" + dbUser + "&" + "password=" + dbPass);
        }
        catch (ClassNotFoundException e) {
            Console.printerr("JDBC driver not found");
        }
        catch (SQLException e) {
            Console.printerr("Problem with MySQL connection: ");
            Console.printerr("SQLException: " + e.getMessage());
            Console.printerr("SQLState: " + e.getSQLState());
            Console.printerr("VendorError: " + e.getErrorCode());
        }
    }

    public void addResult(ExperimentResult result) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO crosspare.results VALUES (NULL,");
        sql.append("\'" + result.getConfigurationName()+"\',");
        sql.append("\'" + result.getProductName()+"\',");
        sql.append("\'" + result.getClassifier()+"\',");
        sql.append(result.getSizeTestData()+",");
        sql.append(result.getSizeTrainingData()+",");
        sql.append(result.getSuccHe()+",");
        sql.append(result.getSuccZi()+",");
        sql.append(result.getSuccG75()+",");
        sql.append(result.getSuccG60()+",");
        sql.append(result.getError()+",");
        sql.append(result.getRecall()+",");
        sql.append(result.getPrecision()+",");
        sql.append(result.getFscore()+",");
        sql.append(result.getGscore()+",");
        sql.append(result.getMcc()+",");
        sql.append(result.getAuc()+",");
        sql.append(result.getAucec()+",");
        sql.append(result.getTpr()+",");
        sql.append(result.getTnr()+",");
        sql.append(result.getFpr()+",");
        sql.append(result.getFnr()+",");
        sql.append(result.getTp()+",");
        sql.append(result.getFn()+",");
        sql.append(result.getTn()+",");
        sql.append(result.getFp()+");");
        
        Statement stmt;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql.toString().replace("NaN", "NULL"));
        }
        catch (SQLException e) {
            Console.printerr("Problem with MySQL connection: ");
            Console.printerr("SQLException: " + e.getMessage());
            Console.printerr("SQLState: " + e.getSQLState());
            Console.printerr("VendorError: " + e.getErrorCode());
            return;
        }
    }
    
    @Override
    public boolean containsResult(String experimentName, String productName) {
        String sql = "SELECT COUNT(*) as cnt FROM crosspare.results WHERE configurationName=\'" + experimentName + "\' AND productName=\'" + productName + "\';";
        Statement stmt;
        boolean contained = false;
        try {
            stmt = con.createStatement();
            ResultSet results = stmt.executeQuery(sql);
            results.next();
            int count = results.getInt("cnt");
            contained = count>0;
        }
        catch (SQLException e) {
            Console.printerr("Problem with MySQL connection: \n");
            Console.printerr("SQLException: " + e.getMessage() + "\n");
            Console.printerr("SQLState: " + e.getSQLState() + "\n");
            Console.printerr("VendorError: " + e.getErrorCode() + "\n");
        }
        return contained;
    }
}
