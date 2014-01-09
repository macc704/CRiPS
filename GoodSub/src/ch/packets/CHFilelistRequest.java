package ch.packets;

import ch.connection.CHPacket;

public class CHFilelistRequest extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String member;

	public CHFilelistRequest(int command, String member) {
		super(command);
		this.member = member;
	}

	public String getMember() {
		return member;
	}
}
