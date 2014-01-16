package ch.conn.framework;

import java.util.ArrayList;
import java.util.List;

import ch.library.CHFileSystem;
import clib.common.filesystem.CFile;
import clib.common.table.CCSVFileIO;

public class CHLoginCheck {

	public static final int SUCCESS = 1;
	public static final int NEW_ENTRY = 0;
	public static final int FAILURE = -1;

	private String user;
	private String password;

	public CHLoginCheck(String user, String passward) {
		this.user = user;
		this.password = passward;
	}

	public int checkPattern(int port) {
		CFile userList = CHFileSystem.getEntryUserList(port);
		List<List<String>> loadDatas = CCSVFileIO.loadAsListList(userList);
		for (List<String> aLoadData : loadDatas) {
			String user = aLoadData.get(0);
			String password = aLoadData.get(1);
			if (user.equals(this.user) && password.equals(this.password)) {
				// 成功
				// CHServer.out.println("SUCCESS");
				return SUCCESS;
			} else if (user.equals(this.user)
					&& !password.equals(this.password)) {
				// パスワード不一致
				// CHServer.out.println("FAILURE");
				return FAILURE;
			}
		}
		// 新規
		// CHServer.out.println("NEW_ENTRY");
		return NEW_ENTRY;
	}

	public boolean entryNewUser(int port) {
		boolean result = true;
		CFile file = CHFileSystem.getEntryUserList(port);
		List<List<String>> loadDatas = CCSVFileIO.loadAsListList(file);
		for (List<String> aLoadData : loadDatas) {
			if (user.equals(aLoadData.get(0))) {
				// CHServer.out.println("user : " + user + " is used.");
				result = false;
			}
		}
		if (result) {
			List<String> newUser = new ArrayList<String>();
			newUser.add(user);
			newUser.add(password);
			loadDatas.add(newUser);
		}
		CCSVFileIO.saveByListList(loadDatas, file);
		return result;
	}
}
