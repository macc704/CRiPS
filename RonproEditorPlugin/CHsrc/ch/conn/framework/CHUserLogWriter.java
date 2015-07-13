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

	public static final String CHDIR_PATH = "workspace/final/.ch";
	public static final String LOGFILE = "CHLog.csv";

	public static final String ECLIPSE_OPEN = "ECLIPSE_OPEN";
	public static final String ECLIPSE_CLOSE = "ECLIPSE_CLOSE";
	public static final String LOGIN = "LOGIN";
	public static final String LOGOUT = "LOGOUT";
	public static final String SYNC_START = "start sync";
	public static final String SYNC_STOP = "stop sync";
	public static final String SEND_FILE = "send file";
	public static final String RECIVE_FILE = "recive file";
	public static final String PULL_ALL = "PULL_ALL";
	public static final String PULL_JAVA = "PULL_JAVA";
	public static final String PULL_MATERIAL = "PULL_MATERIAL";
	public static final String COPY = "COPY";
	public static final String PASTE = "PASTE";
	public static final String OPEN_CHEDITOR = "open cheditor";
	public static final String CLOSE_CHEDITOR = "close cheditor";
	public static final String FILE_REQUEST = "file request";

	private List<List<String>> table = new ArrayList<List<String>>();
	private List<String> row = new ArrayList<String>();
	private DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private String user = "";

	public CHUserLogWriter(String user) {
		this.user = user;
		initialize();
	}

	public CHUserLogWriter() {
		initialize();
	}

	private void initialize() {
		CFile file = getCHDir().findFile(user + LOGFILE);
		if (file != null) {
			table = CCSVFileIO.loadAsListList(file);
			if (file.getNameByString().equals("CHLog.csv") && !user.equals("")) {
				file.renameTo(user + "_CHLog.csv");
			}
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
		return getCHDir().findOrCreateFile(user + LOGFILE);
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

	public void eclipseOpen() {
		writeCommand(ECLIPSE_OPEN);
		addRowToTable();
		saveTableToFile();
	}

	public void eclipseClose() {
		writeCommand(ECLIPSE_CLOSE);
		addRowToTable();
		saveTableToFile();
	}

	public void login() {
		writeCommand(LOGIN);
		addRowToTable();
		saveTableToFile();
	}

	public void logout() {
		writeCommand(LOGOUT);
		addRowToTable();
		saveTableToFile();
	}

	public void pull(String user, String type) {
		writeCommand(type);
		writeFrom(user);
		addRowToTable();
		saveTableToFile();
	}

	public void copy(CFile file, String code) {
		writeCommand(COPY);
		writeFrom(file);
		writeCode(code);
		addRowToTable();
		saveTableToFile();
	}

	public void paste(String fileName, String code) {
		writeCommand(PASTE);
		writeTo(fileName);
		writeCode(code);
		addRowToTable();
		saveTableToFile();
	}

	public static void main(String[] args) {

		CHUserLogWriter log = new CHUserLogWriter("hoge");
		log.eclipseOpen();
		log.login();
		log.pull("fuge", PULL_JAVA);
		log.pull("abc", PULL_ALL);
		log.pull("fda", PULL_MATERIAL);
		log.logout();
		log.eclipseClose();
	}
}
