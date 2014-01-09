package ch.packets;

import java.util.ArrayList;
import java.util.List;

import ch.connection.CHPacket;

public class CHFilelistResponse extends CHPacket {

	private static final long serialVersionUID = 1L;

	private List<String> fileNames = new ArrayList<String>();

	public CHFilelistResponse(int command, List<String> fileNames) {
		super(command);
		this.fileNames = fileNames;
	}

	public List<String> getFileNames() {
		return fileNames;
	}
}
