package ch.server;

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
		List<List<String>> userDatas = CCSVFileIO.loadAsListList(userList);
		for (List<String> aUserData : userDatas) {
			String user = aUserData.get(0);
			String password = aUserData.get(1);
			if (user.equals(this.user) && password.equals(this.password)) {
				// 成功
				CHServer.out.println("SUCCESS");
				return SUCCESS;
			} else if (user.equals(this.user)
					&& !password.equals(this.password)) {
				// パスワード不一致
				CHServer.out.println("FAILURE");
				return FAILURE;
			}
		}
		// 新規
		CHServer.out.println("NEW_ENTRY");
		return NEW_ENTRY;
	}
}
