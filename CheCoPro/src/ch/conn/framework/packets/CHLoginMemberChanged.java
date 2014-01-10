package ch.conn.framework.packets;

import java.util.ArrayList;
import java.util.List;

import ch.conn.framework.CHPacket;
import ch.conn.framework.CHUserState;

public class CHLoginMemberChanged extends CHPacket {

	private static final long serialVersionUID = 1L;

	private List<CHUserState> userStates = new ArrayList<CHUserState>();

	public CHLoginMemberChanged(List<CHUserState> userStates) {
		this.userStates = userStates;
	}

	public List<CHUserState> getUserStates() {
		return userStates;
	}
}
