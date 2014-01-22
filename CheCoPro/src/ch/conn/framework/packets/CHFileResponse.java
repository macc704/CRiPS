package ch.conn.framework.packets;

import java.util.ArrayList;
import java.util.List;

import ch.conn.framework.CHFile;
import ch.conn.framework.CHPacket;

public class CHFileResponse extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private List<CHFile> files = new ArrayList<CHFile>();

	public CHFileResponse(String user, List<CHFile> files) {
		this.user = user;
		this.files = files;

	}

	public String getUser() {
		return user;
	}

	public List<CHFile> getFiles() {
		return files;
	}

	@Override
	public String paramString() {
		return files.toString();
	}
}
