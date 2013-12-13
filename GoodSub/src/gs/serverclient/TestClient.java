package gs.serverclient;

import gs.connection.Connection;
import gs.connection.LoginData;
import gs.frame.CHMemberSelectorFrame;

import java.net.Socket;
import java.util.List;

import javax.swing.JFrame;

public class TestClient {

	public static void main(String[] args) throws Exception {
		TestClient a = new TestClient();
		a.run();
	}

	@SuppressWarnings("unchecked")
	void run() throws Exception {
		Socket sock = new Socket("localhost", 10000);
		Connection conn = new Connection(sock);
		conn.shakehandForClient();
		LoginData data = new LoginData();
		String myName = "hoge" + ((int) (Math.random() * 10000)) % 1000;
		data.setMyName(myName);
		data.setRoomNum(1);
		conn.write(data);
		boolean login = conn.established();
		if (!login) {
			return;
		}

		CHMemberSelectorFrame msFrame = new CHMemberSelectorFrame(myName);	
		msFrame.open();
		msFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.out.println("client established");

		try {
			while (conn.established()) {
				Object obj = conn.read();
				if (obj instanceof List) {
					// for (String aMember : (List<String>) obj) {
					// if (!members.contains(aMember)) {
					// members.add(aMember);
					// }
					// }
					// // msFrame.setMembers(members);
					// System.out.println(obj);					
					msFrame.setMembers((List<String>) obj);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		conn.close();
		System.out.println("client closed");
	}

}
