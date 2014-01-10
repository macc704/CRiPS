package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHLoginResult extends CHPacket {

	private static final long serialVersionUID = 1L;

	private boolean result;

	public CHLoginResult(boolean result) {
		this.result = result;
	}

	public boolean isResult() {
		return result;
	}
}
