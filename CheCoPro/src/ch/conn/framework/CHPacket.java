package ch.conn.framework;

import java.io.Serializable;

public abstract class CHPacket implements Serializable {

	private static final long serialVersionUID = 1L;

	public CHPacket() {
	}

	@Override
	public String toString() {
		return "CHPacket[" + getClass().getSimpleName() + " (" + paramString()
				+ ")]";
	}

	public String paramString() {
		return "";
	}

}
