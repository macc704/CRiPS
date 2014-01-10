package ch.conn.framework.packets;

import java.util.ArrayList;
import java.util.List;

import ch.conn.framework.CHPacket;

public class CHFilegetResponse extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private List<CHFile> files = new ArrayList<CHFile>();

	public CHFilegetResponse(String user, List<CHFile> files) {
		this.user = user;
		this.files = files;

	}

	public String getUser() {
		return user;
	}

	public List<CHFile> getFiles() {
		return files;
	}
}
