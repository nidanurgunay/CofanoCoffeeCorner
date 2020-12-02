package com.cofano.coffeecorner.data;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

public class Database {

	/**
	 * Indicates if the system is being run in its testing environment or otherwise.
	 */
	public static final boolean TESTING = false;
	
	//------------LIVE DATABASE INFO------------//
    private static final String HOST = "bronto.ewi.utwente.nl";
    private static final String PORT = "5432";
    private static final String DB = "dab_di19202b_237";
    private static final String SCHEMA = "cofano";
    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/"+ DB + "?currentSchema=" + SCHEMA;
    private static final String USER = "dab_di19202b_237";
    private static final String PASS = "AKo06Bs9uQ5rG7xg";
    //------------------------------------------//
    
    //-----------TESTING DATABASE INFO----------//
    private static final String TESTINGHOST = "bronto.ewi.utwente.nl";
    private static final String TESTINGPORT = "5432";
    private static final String TESTINGDB = "dab_di19202b_353";
    private static final String TESTINGSCHEMA = "cofano";
    private static final String TESTINGURL = "jdbc:postgresql://" + TESTINGHOST + ":" + TESTINGPORT + "/"+ TESTINGDB + "?currentSchema=" + TESTINGSCHEMA;
    private static final String TESTINGUSER = "dab_di19202b_353";
    private static final String TESTINGPASS = "NZIPoHqipf74izA7";
    //------------------------------------------//

    public ResultSet resultSet;
    public Statement statement;
    public Connection connection;
    
    public Database() { 
    	if (TESTING) openConnection(TESTINGURL, TESTINGUSER, TESTINGPASS);
    	else openConnection(URL, USER, PASS);
    }

    public Database(String URL, String USER, String PASS) {
        openConnection(URL, USER, PASS);
    }

    public Database(String JNDI) {
        openConnection(JNDI);
    }

    private void openConnection(String JDNI) {

        try {

            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(JDNI);
            connection = ds.getConnection();

        } catch (SQLException | NamingException e) { e.printStackTrace(); }

    }

    private void openConnection(String URL, String USERNAME, String PASSWORD) {

        try {

            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

    }

    public void close() {

        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) { e.printStackTrace(); }

    }

    public ResultSet executeQuery(String query) {
        
        try {

            if (!query.contains("RETURNING")
            		&& (query.startsWith("INSERT")
                    || query.startsWith("UPDATE")
                    || query.startsWith("ALTER")
                    || query.startsWith("DELETE"))) {

                statement = connection.createStatement();
                statement.executeUpdate(query);

            } else {

                statement = connection.createStatement();
                return statement.executeQuery(query);

            }

        } catch (SQLException e) { System.out.println(e); }

        return null;

    }
    
    public ResultSet executePreparedStatement(PreparedStatement preparedStatement) {

    	try {

			preparedStatement.execute();
			return preparedStatement.getResultSet();

		} catch (SQLException e) { e.printStackTrace(); }

    	return null;

    }
    
    public int getHighestColumnValue(String column, String table) {

        try {
        
            String query = "SELECT MAX(" + column +") AS max "
                         + "FROM " + table + ";";
            ResultSet r = executeQuery(query);

            while (r.next()) { return r.getInt("max"); }
        
        } catch (SQLException e) { System.out.println(e); }

        close();
        return -1;

    }

    public void resetAIValueToHighest(String table) {

        String query = "ALTER TABLE `" + table + "` "
                + "AUTO_INCREMENT=" + getHighestColumnValue("id", table) + ";";

        executeQuery(query);

    }
    
}