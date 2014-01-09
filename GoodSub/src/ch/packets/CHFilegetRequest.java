package ch.packets;

import java.util.ArrayList;
import java.util.List;

import ch.connection.CHPacket;

public class CHFilegetRequest extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String member;
	private List<String> addedFiles = new ArrayList<String>();
	private List<String> removedFiles = new ArrayList<String>();

	public CHFilegetRequest(int command, String member) {
		super(command);
		this.member = member;
	}

	public String getMember() {
		return member;
	}

	public List<String> getAddedFiles() {
		return addedFiles;
	}

	public List<String> getRemovedFiles() {
		return removedFiles;
	}
}
