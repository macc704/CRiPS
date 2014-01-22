package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHLoginResult extends CHPacket {

	private static final long serialVersionUID = 1L;

	private int result;

	public CHLoginResult(int result) {
		this.result = result;
	}

	public int isResult() {
		return result;
	}
}
