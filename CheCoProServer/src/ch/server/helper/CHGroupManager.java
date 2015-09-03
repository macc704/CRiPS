package ch.server.helper;

import java.util.ArrayList;
import java.util.List;

import ch.library.CHFileSystem;
import clib.common.filesystem.CFile;
import clib.common.table.CCSVFileIO;

public class CHGroupManager {

	public static final int SUCCESS = 1;
	public static final int NEW_ENTRY = 0;
	public static final int FAILURE = -1;

	private String user;
	private String password;

	public CHGroupManager(String user, String password) {
		this.user = user;
		this.password = password;
	}

	public int checkPattern(int port) {
		List<List<String>> loadDatas = loadUserListAsListList(port);
		for (List<String> aLoadData : loadDatas) {
			String user = aLoadData.get(0);
			String password = aLoadData.get(1);
			if (user.equals(this.user) && password.equals(this.password)) {
				// 成功
				return SUCCESS;
			} else if (user.equals(this.user)
					&& !password.equals(this.password)) {
				// パスワード不一致
				return FAILURE;
			}
		}
		// 新規
		return NEW_ENTRY;
	}

	public List<List<String>> loadUserListAsListList(int port) {
		return CCSVFileIO.loadAsListList(getUserListFile(port));
	}

	public CFile getUserListFile(int port) {
		return CHFileSystem.getEntryUserList(port);
	}

	public boolean entryNewUser(int port) {
		List<List<String>> loadDatas = loadUserListAsListList(port);
		for (List<String> aLoadData : loadDatas) {
			if (user.equals(aLoadData.get(0))) {
				return false;
			}
		}

		List<String> newUser = new ArrayList<String>();
		newUser.add(user);
		newUser.add(password);
		loadDatas.add(newUser);
		CCSVFileIO.saveByListList(loadDatas, getUserListFile(port));
		return true;
	}
}
