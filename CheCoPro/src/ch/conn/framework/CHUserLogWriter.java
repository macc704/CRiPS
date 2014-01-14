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

	public static final String CHDIR_PATH = "MyProjects/.CH";
	public static final String LOGFILE = "chLog.csv";

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
			CCSVFileIO.saveByListList(table, getLogFile());
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
		header.add("copy time");
		header.add("from");
		header.add("paste time");
		header.add("to");
		header.add("code");
		return header;
	}

	public void setCopyTime() {
		if (row.get(0) != null) {
			initRow();
		} else if (row.get(0) == null) {
			row.set(0, formatter.format(new Date()));
		}
	}

	public void setFrom(String path) {
		if (row.get(1) != null) {
			initRow();
		} else if (row.get(1) == null) {
			row.set(1, path);
		}
	}

	public void setPasteTime() {
		if (row.get(2) != null) {
			initRow();
		} else if (row.get(2) == null) {
			row.set(2, formatter.format(new Date()));
		}
	}

	public void setTo(String path) {
		if (row.get(3) != null) {
			initRow();
		} else if (row.get(3) == null) {
			row.set(3, path);
			addTabel(row);
		}
	}

	public void setCode(String code) {
		if (row.get(4) != null) {
			initRow();
		} else if (row.get(4) == null) {
			row.set(4, code);
		}
	}

	private void addTabel(List<String> row) {
		if (row.size() == 5) {
			table.add(row);
			CCSVFileIO.saveByListList(table, getLogFile());
			initRow();
		}
	}

	private void initRow() {
		for (int i = 0; i < 5; i++) {
			row.add(null);
		}
	}

	public static void main(String[] args) {
		CHUserLogWriter log = new CHUserLogWriter();
		log.setCopyTime();
		log.setFrom("user1");
		log.setCode("int i");
		log.setPasteTime();
		log.setTo("user2");
		log.setCopyTime();
		log.setTo("user3");
	}
}
