package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

public class Copier {

	private String baseDir="";
	protected String enc = "UTF-8";

	public Copier(String baseDir){
		this.baseDir = baseDir;
	}

	public void print(File file){

	}

	public String getBaseDir(){
		return this.baseDir;
	}

	public void printDOM(String text, FileOutputStream ldfOS) throws IOException{
		OutputStreamWriter ldfFOS = new OutputStreamWriter(ldfOS, enc);
		BufferedWriter ldfWriter = new BufferedWriter(ldfFOS);
		ldfWriter.write(text);
		ldfWriter.flush();
		ldfWriter.close();
	}

	public BufferedReader createBufferReader(String fileName) throws IOException{
		FileInputStream ldfReader = new FileInputStream(System.getProperty("user.dir") + "/" + getBaseDir() + fileName);
		InputStreamReader ldfISR = new InputStreamReader(ldfReader, enc);

		return new BufferedReader(ldfISR);
	}

	public PrintStream createPrintStream(){
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		return new PrintStream(byteArray);
	}

	public String getHomeDir(File file){
		File tmp = new File(file.getPath());
		String home = "";

		while (!hasExtFolder(tmp)) {
			tmp = tmp.getParentFile();
			home = home + "../";
		}

		return home;
	}

	public boolean hasExtFolder(File file) {
		File[] files = file.getParentFile().listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() && files[i].getName().equals("ext")) {
				return true;
			}
		}
		return false;
	}
}
