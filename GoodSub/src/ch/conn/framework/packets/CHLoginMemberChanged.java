package ch.conn.framework.packets;

import java.util.ArrayList;
import java.util.List;

import ch.conn.framework.CHPacket;

public class CHLoginMemberChanged extends CHPacket {

	private static final long serialVersionUID = 1L;

	private List<String> members = new ArrayList<String>();

	public CHLoginMemberChanged(List<String> members) {
		this.members = members;
	}

	public List<String> getMembers() {
		return members;
	}
}
