package websocket_server;

import java.io.IOException;
import java.sql.SQLException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@ServerEndpoint(value = "/mark")
public class data_server {

	private Session session;
	private DatabaseService dbservice;
	private String ip;

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		// ip = (String)
		// session.getUserProperties().get("javax.websocket.endpoint.remoteAddress");
		// System.out.println("New connection from:" + ip);
		// System.out.println("incoming connection");
	}

	@OnClose
	public void onClose() {
	}

	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		System.out.println(message);
		try {
			dbservice = new DatabaseService();
			JSONObject jsonObj = JSONObject.fromObject(message);
			if (jsonObj.getString("msgtype").equals("getimage")) {
				sendMessage(dbservice.getImageURL(jsonObj.getString("hitId")));
			} else if (jsonObj.getString("msgtype").equals("submit")) {
				if (dbservice.checkHit(jsonObj.getString("hitId"), jsonObj.getString("imgFile"))) {
					dbservice.insAssignment(jsonObj.getString("hitId"), jsonObj.getString("assignmentId"),
							jsonObj.getString("workerId"), jsonObj.getInt("nwidth"), jsonObj.getInt("nheight"));
					JSONArray arr = jsonObj.getJSONArray("rects");
					int size = arr.size();
					for (int i = 0; i < size; i++)
						dbservice.insRect(jsonObj.getString("assignmentId"), arr.getJSONObject(i));
					sendMessage("ok");
				} else {
					sendMessage("hit not found");
				}
			} else {
				sendMessage("commanderror");
			}
		} catch (SQLException e) {
			sendMessage("database error");
			e.printStackTrace();
		} catch (IOException e) {
			sendMessage("server io error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			sendMessage("server lib error");
			e.printStackTrace();
		} catch (Exception e) {
			sendMessage("unknown error");
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		error.printStackTrace();
	}

	public void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}

}
