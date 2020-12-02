package com.cofano.coffeecorner.business.dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cofano.coffeecorner.business.model.bulletin.Bulletin;
import com.cofano.coffeecorner.business.model.users.User;
import com.cofano.coffeecorner.data.Database;

/**
 * The class works with database, retrieving information regarding bulletins.
 * 
 * @author Jasper van Amerongen
 * @author Nidanur Gunay
 * @author Adamo Mariani
 * @author Albina Shynkar
 * @author Eda Yardim
 * @author Lola Solovyeva
 * 
 * @version 1
 *
 */
public class BulletinDAO {
	
	private static final String TABLENAME = "bulletin";
	private static final String  USERTABLE = UserDAO.TABLENAME;
	
	public Bulletin fill(ResultSet r, User author) throws SQLException {
		
		Bulletin bulletin = new Bulletin();
		bulletin.setId(r.getInt("id"));
		bulletin.setTitle(r.getString("title"));
		bulletin.setBody(r.getString("body"));
		bulletin.setImageUri(r.getString("image_uri"));
		bulletin.setAuthor(author);

		return bulletin;

	}
	
	public Bulletin get(int id) {
		Bulletin res = null;

		Database DB = new Database();

		
		try {
			String q1 = "SELECT * FROM " + TABLENAME
					   + " WHERE id= ?;";
			PreparedStatement prepStatement = DB.connection.prepareStatement(q1);
			prepStatement.setInt(1, id);
			ResultSet r1 = DB.executePreparedStatement(prepStatement);
			
			while (r1.next()) {
				String q2 = "SELECT * FROM " + USERTABLE + " WHERE id = ?;" ;
				PreparedStatement prepStatement2 = DB.connection.prepareStatement(q2);
				prepStatement2.setString(1, r1.getString("author_id"));
				ResultSet r2 = DB.executePreparedStatement(prepStatement2);
				User u = null;
				if (r2.next()) {
					u = new UserDAO().fill(r2);
				}
				res = fill(r1, u);
			}
		} catch (SQLException ex) {
			ex.printStackTrace(); 
		}finally {
			DB.close();
		}
		return res;
	}
	
	public List<Bulletin> getAll() {

		List<Bulletin> out = new ArrayList<>();

		Database DB = new Database();


		try {

			String q = "SELECT * FROM " + TABLENAME + " ORDER BY id;";
			ResultSet r1 = DB.executeQuery(q);
			while (r1.next()) { 
				String q2 = "SELECT * FROM " + USERTABLE + " WHERE id = ?;" ;
				PreparedStatement prepStatement = DB.connection.prepareStatement(q2);
				prepStatement.setString(1, r1.getString("author_id"));
				ResultSet r2 = DB.executePreparedStatement(prepStatement);
				
				User u = null;
				while (r2.next()) { u = new UserDAO().fill(r2); }
				out.add(fill(r1, u)); 
			}
		} catch (SQLException ex) {
			ex.printStackTrace(); 
		}finally {
			DB.close();
		}

		return out;

	}
	
	/**
	 * Retrieves the list of a certain number of bulletins from the database.
	 * 
	 * @param min the amount of most recent events to be skipped
	 * @param max the amount of events to retrieve after </i>min</i>
	 * 
	 * @return list of all bulletins from database
	 */
	public List<Bulletin> getAmount(int min, int max) {
		
		List<Bulletin> out = new ArrayList<>();
		Database DB = new Database();

		int limit = max - min;
		try {

			String query = "SELECT * "
					+ "FROM " + TABLENAME 
					+ " ORDER BY id DESC "
					+ "LIMIT ? OFFSET ?";
			PreparedStatement prepStatement = DB.connection.prepareStatement(query);
			prepStatement.setInt(1, limit);
			prepStatement.setInt(2, min);
			ResultSet r1= DB.executePreparedStatement(prepStatement);
			

			while (r1.next()) { 
				String q2 = "SELECT * FROM " + USERTABLE + " WHERE id = ?;" ;
				PreparedStatement prepStatement2 = DB.connection.prepareStatement(q2);
				prepStatement2.setString(1, r1.getString("author_id"));
				ResultSet r2 = DB.executePreparedStatement(prepStatement2);
				
				
				User u = null;
				while (r2.next()) { u = new UserDAO().fill(r2); }
				out.add(fill(r1, u));
			}
		
		} catch (SQLException e) { 
			e.printStackTrace(); 
		}finally {
			DB.close();
		}
		return out;
	}

	/**
	 * Save a {@link Bulletin bulletin} object and return its id (SERIALIZED).
	 * 
	 * @param b the bulletin to save
	 * 
	 * @return the id of the bulletin which was just saved
	 */
	public int save(Bulletin b) {
		
		Database DB = new Database();


		try {
	    	
	    	String q = "INSERT INTO " + TABLENAME + " (title, body, image_uri, author_id) VALUES (? ,? ,? ,?)";
	    	
	    	PreparedStatement prepStatement = DB.connection.prepareStatement(q, PreparedStatement.RETURN_GENERATED_KEYS);
			prepStatement.setString(1, b.getTitle());
			prepStatement.setString(2, b.getBody());
			prepStatement.setString(3, b.getImageUri());
			prepStatement.setString(4, b.getAuthor().getId());
			DB.executePreparedStatement(prepStatement);
			ResultSet keys = prepStatement.getGeneratedKeys();
			
			int idSet = -1;
		
			if (keys.next()) { 
				idSet = keys.getInt(1); 
			}
			return idSet;
		}catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			DB.close();
		}
		return -1;

	}
	
	public void delete(int id) {

		Database DB = new Database();

		try {
			String q = "DELETE FROM " + TABLENAME +" WHERE id = ?;";
			PreparedStatement prepStatement = DB.connection.prepareStatement(q);
			prepStatement.setInt(1, id);
			DB.executePreparedStatement(prepStatement);
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close();
		}
	}

}
