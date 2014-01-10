package ch.packets;

import java.util.ArrayList;
import java.util.List;

import ch.connection.CHPacket;

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
