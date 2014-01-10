package ch.conn.framework.packets;

import java.util.ArrayList;
import java.util.List;

import ch.conn.framework.CHPacket;

public class CHFilegetRequest extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private List<String> requestFilePaths = new ArrayList<String>();

	public CHFilegetRequest(String user, List<String> requestFilePaths) {
		this.user = user;
		this.requestFilePaths = requestFilePaths;
	}

	public String getUser() {
		return user;
	}

	public List<String> getRequestFilePaths() {
		return requestFilePaths;
	}

}
