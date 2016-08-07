package websocket_server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.json.JSONObject;

public class DatabaseService {

	private Connection con;
	private Statement stat;
	private final String dbhost = "127.0.0.1:3306";
	private final String dbname = "amt_imgmark";
	private final String user = "root";
	private final String pass = "123456";

	public DatabaseService() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection("jdbc:mysql://" + dbhost + "/" + dbname + "?user=" + user + "&password="
				+ pass + "&useUnicode=true&characterEncoding=UTF8&useSSL=true");
		stat = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
	}

	public String getImageURL(String hit) throws SQLException {
		ResultSet rs = stat.executeQuery("select * from hits where hitid='" + hit+"'");
		if (!rs.first())
			return "notfound";
		int imgid = rs.getInt("imgid");
		rs = stat.executeQuery("select * from images where id=" + imgid);
		if (!rs.first())
			return "notfound";
		return rs.getString("url");
	}

	public boolean checkHit(String hit, String img) throws SQLException {
		ResultSet rs = stat.executeQuery("select * from hits where hitid='" + hit + "'");
		if (!rs.first())
			return false;
		int imgid = rs.getInt("imgid");
		rs = stat.executeQuery("select * from images where id='" + imgid + "'");
		if (!rs.first())
			return false;
		if ((rs.getString("url").endsWith(img))||img.endsWith(rs.getString("url")))
			return true;
		return false;
	}

	public void insAssignment(String hit, String assignment, String worker, int width, int height) throws SQLException {
		ResultSet rs = stat.executeQuery("select * from assignments");
		rs.moveToInsertRow();
		rs.updateString("hitid", hit);
		rs.updateString("assignmentid", assignment);
		rs.updateString("workerid", worker);
		rs.updateInt("width", width);
		rs.updateInt("height", height);
		rs.insertRow();
	}

	public void insRect(String assignment, JSONObject obj) throws SQLException {
		ResultSet rs = stat.executeQuery("select * from rectangles");
		rs.moveToInsertRow();
		rs.updateString("assignmentid", assignment);
		rs.updateInt("num", obj.getInt("num"));
		rs.updateString("type", obj.getString("type"));
		rs.updateInt("width", obj.getInt("width"));
		rs.updateInt("height", obj.getInt("height"));
		rs.updateInt("x", obj.getInt("x"));
		rs.updateInt("y", obj.getInt("y"));
		rs.insertRow();
	}

}
