package com.cofano.coffeecorner.business.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cofano.coffeecorner.business.model.chat.Message;
import com.cofano.coffeecorner.business.model.users.User;
import com.cofano.coffeecorner.data.Database;

/**
 * The class works with database, retrieving information regarding messages.
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
public class MessageDAO {

	private static final String TABLENAME = "message";
	
	public Message fill(ResultSet r, User u) throws SQLException {
		Message message = new Message();
		message.setId(r.getInt("id"));
		message.setText(r.getString("text"));
		message.setTime(r.getTimestamp("time"));
		message.setStarred(r.getBoolean("starred"));
		message.setAuthor(u);
		return message;
	}

	public Message get(int id) {
		Database DB = new Database();

		Message m = null;
		
		try {
			String q1 = "SELECT * FROM " + TABLENAME + " "
					   + "WHERE id = ?;";
			
			PreparedStatement prepStatement = DB.connection.prepareStatement(q1);
			prepStatement.setInt(1, id);
			
			ResultSet r1 = DB.executePreparedStatement(prepStatement);
			
			while (r1.next()) { 
				String q2 = "SELECT * FROM " + UserDAO.TABLENAME + " WHERE id = '" + r1.getString("author_id") + "';" ;
				ResultSet r2 = DB.executeQuery(q2);
				User u = null;
				while (r2.next()) { u = new UserDAO().fill(r2); }		
				m = fill(r1, u);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			DB.close();
		}

		return m;

	}

	public int save(Message m) {

		Database DB = new Database();

		
		try {
			String q = "INSERT INTO " + TABLENAME + " (text, time, author_id) VALUES (?, ?, ?) "
					+ "RETURNING id";
			
			PreparedStatement prepStatement = DB.connection.prepareStatement(q, PreparedStatement.RETURN_GENERATED_KEYS);
			prepStatement.setString(1, m.getText());
			LocalDateTime localDateTime = m.getTime().toInstant().atZone(ZoneId.of("GMT+2")).toLocalDateTime();
			prepStatement.setObject(2, localDateTime);
			prepStatement.setString(3, m.getAuthor().getId());
			
			DB.executePreparedStatement(prepStatement);
			
			ResultSet keys = prepStatement.getGeneratedKeys();
			int idSet = -1;
			
			if (keys.next()) { 
				idSet = keys.getInt(1); 
			}
			return idSet;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close();
		}

		return -1;
	}
	
	/**
	 * Marks a message as starred, if not already so (vice versa otherwise)
	 * 
	 * @param mid the message to mark
	 */
	public void markStarred(int mid) {
		
		Message retrieved = get(mid);
		retrieved.setStarred(!retrieved.isStarred());
		update(mid, retrieved);

	}
	
	/**
	 * Gets the amount of messages in the specified interval from the database.
	 * 
	 * @param min The starting interval value
	 * @param max The ending interval value
	 * 
	 * @return list of messages from database falling in the specified interval (min -> max)
	 */
	public List<Message> getAmount(int min, int max) {
		Database DB = new Database();
		List<Message> out = new ArrayList<>();
		int limit = max - min;
		
		try {
			String q = "SELECT * "
					+ "FROM " + TABLENAME + " "
					+ "ORDER BY time DESC "
					+ "LIMIT ? OFFSET ?;";

			PreparedStatement prepStatement = DB.connection.prepareStatement(q);
			prepStatement.setInt(1, limit);
			prepStatement.setInt(2, min);

			ResultSet r1 = DB.executePreparedStatement(prepStatement);

			while (r1.next()) {
				String q2 = "SELECT * FROM " + UserDAO.TABLENAME +" WHERE id = '" +  r1.getString("author_id") + "';" ;
				ResultSet r2 = DB.executeQuery(q2);
				User u = null;
				if (r2.next()) { u = new UserDAO().fill(r2); }
				out.add(fill(r1, u));
			}
		} catch (SQLException e) {
			e.printStackTrace(); 
		} finally {
			DB.close();
		}
		Collections.reverse(out);
		return out;

	}

	/**
	 * Alters a message's STARRED value.
	 * 
	 * @param id the message id to alter
	 * @param m the message whose starred value to copy
	 */
	public void update(int id, Message m) {
		Database DB = new Database();

		try {
			String q = "UPDATE " + TABLENAME + " "
					+ "SET starred = ? "
					+ "WHERE id = ?;";
			
			PreparedStatement prepStatement = DB.connection.prepareStatement(q);
			prepStatement.setBoolean(1, m.isStarred());
			prepStatement.setInt(2, m.getId());
			
			DB.executePreparedStatement(prepStatement);
		} catch (SQLException e) {
			e.printStackTrace(); 
		} finally {
			DB.close();
		}
	}
	
	public List<Message> getAll() {

		List<Message> out = new ArrayList<>();

		try {

			Database DB = new Database();
			String q = "SELECT * "
					+ "FROM " + TABLENAME + " "
					+ "ORDER BY time ASC;";
			ResultSet r1 = DB.executeQuery(q);

			while (r1.next()) { 
				String q2 = "SELECT * FROM " + UserDAO.TABLENAME + " WHERE id = '" + r1.getString("author_id") + "';" ;
				ResultSet r2 = DB.executeQuery(q2);
				User u = null;
				if (r2.next()) {
					u = new UserDAO().fill(r2);
				}
				out.add(fill(r1, u)); }
			DB.close();

		} catch (SQLException ex) { ex.printStackTrace(); }

		return out;

	}

}
