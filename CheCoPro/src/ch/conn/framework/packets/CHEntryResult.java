package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHEntryResult extends CHPacket {

	private static final long serialVersionUID = 1L;

	private boolean result;

	public CHEntryResult(boolean result) {
		this.result = result;
	}

	public boolean isResult() {
		return result;
	}
}
