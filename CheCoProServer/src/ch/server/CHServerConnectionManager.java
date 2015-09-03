package ch.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.conn.framework.CHConnection;
import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHLoginMemberChanged;

public class CHServerConnectionManager {

	private Object lock = new Object();

	private Map<String, CHConnection> connections = new LinkedHashMap<String, CHConnection>();
	private Map<CHConnection, String> users = new LinkedHashMap<CHConnection, String>();
	private List<CHUserState> userStates = new ArrayList<CHUserState>();

	public String getUser(CHConnection conn) {
		return users.get(conn);
	}

	//
	// public CHConnection getConnection(String user) {
	// return connections.get(user);
	// }

	public List<String> getAllUsers() {
		return new ArrayList<String>(users.values());
	}

	public List<CHUserState> getUserStates() {
		return userStates;
	}

	// private List<CHConnection> getConnections() {
	// return new ArrayList<CHConnection>(connections.values());
	// }

	public boolean login(CHUserState userState, CHConnection conn) {
		synchronized (lock) {
			if (connections.containsKey(userState.getUser())) {
				logout(userState.getUser());
			}
			CHUserState state = userState;
			for (CHUserState aUserState : userStates) {
				if (userState.getUser().equals(aUserState.getUser())) {
					state = aUserState;
				}
			}
			userStates.remove(state);
			state.setLogin(true);
			userStates.add(state);
			connections.put(userState.getUser(), conn);
			users.put(conn, userState.getUser());
			// userStates.add(userState);
			CHServer.out.println("login user: " + state.getUser());
			return true;
		}
	}

	public boolean logout(String user) {
		CHConnection conn = connections.get(user);
		return logout(conn);
	}

	// userStates未処理
	public boolean logout(CHConnection conn) {
		synchronized (lock) {
			if (users.containsKey(conn)) {
				String user = users.get(conn);
				CHUserState state = null;
				for (CHUserState aUserState : userStates) {
					if (user.equals(aUserState.getUser())) {
						state = aUserState;
					}
				}
				userStates.remove(state);
				state.setLogin(false);
				state.setLastLogin(new Date());
				userStates.add(state);
				connections.remove(user);
				users.remove(conn);
				conn.close();
				CHServer.out.println("logout user: " + user);
				return true;
			} else {
				return false;
			}
		}
	}

	public void broadCastExceptSender(Object obj, String sender) {
		synchronized (lock) {
			List<String> users = getAllUsers();
			users.remove(sender);
			broadCast0(obj, users);
		}
	}

	public void broadCast(Object obj) {
		synchronized (lock) {
			broadCast0(obj, getAllUsers());
		}
	}

	private void broadCast0(Object obj, List<String> users) {
		synchronized (lock) {
			for (String user : users) {
				sendToOne(obj, user);
			}
		}
	}

	public void sendToOne(Object obj, String user) {
		synchronized (lock) {
			if (connections.get(user).write(obj)) {
				CHServer.out.println("send to: " + user + ", object: " + obj);
			} else {
				logout(user);
				broadCast(new CHLoginMemberChanged(getUserStates()));
			}
		}
	}

	public void sendToOne(Object obj, CHConnection conn) {
		synchronized (lock) {
			if (conn.write(obj)) {
				CHServer.out.println("send to: " + conn + ", object: " + obj);
			} else {
				logout(conn);
				broadCast(new CHLoginMemberChanged(getUserStates()));
			}
		}
	}
}