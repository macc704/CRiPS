package ch.conn.framework;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.table.CCSVFileIO;

public class CHUserLogWriter {

	public static final String CHDIR_PATH = "MyProjects/final/.ch";
	public static final String LOGFILE = "chLog.csv";

	public static final String LOGIN = "login";
	public static final String LOGOUT = "logout";
	public static final String SYNC_START = "start sync";
	public static final String SYNC_STOP = "stop sync";
	public static final String SEND_FILE = "send file";
	public static final String RECIVE_FILE = "recive file";
	public static final String COPY_FILE = "copy file";
	public static final String COPY_CODE = "copy code";
	public static final String PASTE_CODE = "paste code";

	private List<List<String>> table = new ArrayList<List<String>>();
	private List<String> row = new ArrayList<String>();
	private DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public CHUserLogWriter() {
		initialize();
	}

	private void initialize() {
		CFile file = getCHDir().findFile(LOGFILE);
		if (file != null) {
			table = CCSVFileIO.loadAsListList(file);
		} else {
			table.add(getHeaderList());
			saveTableToFile();
		}
		initRow();
	}

	public CDirectory getCHDir() {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(
				CHDIR_PATH);
	}

	public CFile getLogFile() {
		return getCHDir().findOrCreateFile(LOGFILE);
	}

	public List<String> getHeaderList() {
		List<String> header = new ArrayList<String>();
		header.add("time");
		header.add("command");
		header.add("from");
		header.add("to");
		header.add("code");
		return header;
	}

	public void writeCommand(String command) {
		if (!row.get(0).equals("")) {
			initRow();
		} else if (row.get(0).equals("")) {
			row.set(0, formatter.format(new Date()));
		}
		if (!row.get(1).equals("")) {
			initRow();
		} else if (row.get(1).equals("")) {
			row.set(1, command);
		}
	}

	public void writeFrom(String user) {
		if (!row.get(2).equals("")) {
			initRow();
		} else if (row.get(2).equals("")) {
			row.set(2, user);
		}
	}

	public void writeFrom(CFile file) {
		if (!row.get(2).equals("")) {
			initRow();
		} else if (row.get(2).equals("")) {
			row.set(2, file.getAbsolutePath().toString());
		}
	}

	public void writeTo(String user) {
		if (!row.get(3).equals("")) {
			initRow();
		} else if (row.get(3).equals("")) {
			row.set(3, user);
		}
	}

	public void writeTo(CFile file) {
		if (!row.get(3).equals("")) {
			initRow();
		} else if (row.get(3).equals("")) {
			row.set(3, file.getAbsolutePath().toString());
		}
	}

	public void writeCode(String code) {
		if (!row.get(4).equals("")) {
			initRow();
		} else if (row.get(4).equals("")) {
			row.set(4, code);
		}
	}

	public void addRowToTable() {
		table.add(row);
		initRow();
	}

	public void saveTableToFile() {
		CCSVFileIO.saveByListList(table, getLogFile());
	}

	private void initRow() {
		row = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			row.add("");
		}
	}

	public static void main(String[] args) {
		CHUserLogWriter log = new CHUserLogWriter();
		log.writeCommand(LOGIN);
		log.addRowToTable();
		log.writeCommand(SEND_FILE);
		log.writeFrom("final/Final/java");
		log.addRowToTable();
		log.writeCommand(RECIVE_FILE);
		log.writeFrom("user2");
		log.writeTo("user1");
		log.addRowToTable();
		log.writeCommand(SYNC_START);
		log.addRowToTable();
		log.writeCommand(SYNC_STOP);
		log.addRowToTable();
		log.writeCommand(COPY_FILE);
		log.writeFrom(".CH/user2/Final.java");
		log.addRowToTable();
		log.writeCommand(COPY_CODE);
		log.writeFrom(".CH/user2/Final.java");
		log.writeCode("int i");
		log.addRowToTable();
		log.writeCommand(PASTE_CODE);
		log.writeTo("final/Final.java");
		log.writeCode("int i");
		log.addRowToTable();
		log.writeCommand(LOGOUT);
		log.addRowToTable();
		log.saveTableToFile();
	}
}
