package ch.conn.framework.packets;

import java.awt.Point;

import ch.conn.framework.CHPacket;

public class CHSourceChanged extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private String source;
	private String currentFileName;
	private Point point;
	
	public CHSourceChanged(String user, String source, String currentFileName,
			Point point) {
		this.user = user;
		this.source = source;
		this.currentFileName = currentFileName;
		this.point = point;
	}

	public String getUser() {
		return user;
	}

	public String getSource() {
		return source;
	}

	public String getCurrentFileName() {
		return currentFileName;
	}

	public Point getPoint() {
		return point;
	}
}
