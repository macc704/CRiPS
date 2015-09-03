package ronproeditor.helpers;

import java.awt.FontMetrics;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import clib.common.system.CJavaSystem;

/**
 * CommandExecuter.java
 * 
 * @author Megumi Araki
 * @version 2007/04/19 16:26:11
 */

public class RECommandExecuter {

	public static String commandEncoding;

	static {
		if (CJavaSystem.getInstance().isMac()) {
			commandEncoding = "UTF-8";
		} else {
			commandEncoding = System.getProperty("file.encoding");
		}
	}

	// public static void main(String args[]) throws Exception {
	// executeCommand("mogrify");
	// }

	// public static void executeCommand(String command) throws Exception {
	// executeCommand(command, new File("."));
	// }

	private static List<Process> processes = new ArrayList<Process>();

	public static synchronized void killAll() {
		List<Process> copy = new ArrayList<Process>(processes);
		for (Process p : copy) {
			p.destroy();
		}
	}

	public static void executeCommand(final List<String> commands,
			final File dir, final IConsole console, final FontMetrics font) {
		new Thread() {
			public void run() {
				try {
					RECommandExecuter.run(commands, dir, console, font);
				} catch (Exception ex) {
					ex.printStackTrace(console.getErr());
				}
			}
		}.start();
	}

	public static void executeCommandWait(final List<String> commands,
			final File dir, final IConsole console, FontMetrics font) throws Exception {
		run(commands, dir, console, font);
	}

	public static void run(List<String> commands, File dir, IConsole console, FontMetrics fontMetrics)
			throws Exception {
		console.toLast();

		Runtime rt = Runtime.getRuntime();
		console.getOut().println(
				"�R�}���h���s(" + new Date(System.currentTimeMillis()) + "):"
						+ getCommandString(commands));

		Process p = rt.exec(listToStringArray(commands), null, dir);

		processes.add(p);
		
		createPrintStreamThread(p.getInputStream(), console.getOut(), fontMetrics).start();
		createPrintStreamThread(p.getErrorStream(), console.getErr(), fontMetrics).start();

		console.setConsoleToStream(new PrintStream(p.getOutputStream()));
		InputStream in = console.getIn();
		if (in instanceof JTextAreaInputStream) {
			((JTextAreaInputStream) in).refresh();
		}
		// int result = p.waitFor();
		p.waitFor();
		console.setConsoleToStream(null);
		processes.remove(p);
		console.getOut().println(
				"�R�}���h�I��(" + new Date(System.currentTimeMillis()) + "):"
						+ getCommandString(commands));
	}

	private static String getCommandString(List<String> list) {
		String commands = "";
		Iterator<String> i = list.iterator();
		while (i.hasNext()) {
			commands += i.next();
			if (i.hasNext()) {
				commands += " ";
			}
		}
		return commands;
	}

	private static String[] listToStringArray(List<String> list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	private static Thread createPrintStreamThread(final InputStream in,
			final PrintStream out, final FontMetrics fontMetrics) {
		return new Thread() {
			public void run() {
				try {
					InputStreamReader reader = new InputStreamReader(in,
							commandEncoding);
					char[] buf = new char[1024];
					int n;
					while ((n = reader.read(buf)) > 0) {
						char[] text = new char[n];
						System.arraycopy(buf, 0, text, 0, text.length);
						out.print(fixErrorMessage(String.valueOf(text),fontMetrics));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
	}

	private static String fixErrorMessage(String message, FontMetrics fontMetrics) {
		// 2�s�ڂ̃G���[�ӏ��𒊏o����
		String[] messages = message.split(System.getProperty("line.separator"));
		if (messages.length < 3) {
			// �ϊ��̕K�v�����Ȃ��̂ł��̂܂ܕԂ�
			return message;
		}
		
		//�G���[�����ӏ���tab�L�[,�X�y�[�X�L�[����x��菜��
		String tmpErrorMessage = messages[1].replaceAll("\t", " ");
		String tmpPointoutMessage = messages[2].replaceAll("\t", " ");		
		
		if(tmpPointoutMessage.indexOf("^") == -1){
			return message;
		}
		
		//�G���[���b�Z�[�W�̃s�N�Z�������擾����
		int errorMessagePixel = fontMetrics.stringWidth(tmpErrorMessage.substring(0, tmpPointoutMessage.indexOf("^")));
		
		//�s�N�Z�����𒲐�����
		while(fontMetrics.stringWidth(tmpPointoutMessage) < errorMessagePixel){
			tmpPointoutMessage = " " + tmpPointoutMessage;
		}
		
		//�V�������b�Z�[�W���쐬����
		String newMessage = "";
		
		messages[1]= tmpErrorMessage;
		messages[2] = tmpPointoutMessage;
		
		for (int i = 0; i < messages.length; i++) {
			newMessage += messages[i] + System.getProperty("line.separator");
		}
		
		return newMessage;
	}

	// private static String[] getEnv() {
	// ArrayList<String> env = new ArrayList<String>();
	// Map<String, String> envMap = System.getenv();
	// for (String key : envMap.keySet()) {
	// String value = envMap.get(key);
	// env.add(new String(key + "=" + value));
	// }
	// String[] strenv = new String[env.size()];
	// int i = 0;
	// for (String aenv : env) {
	// strenv[i] = aenv;
	// i++;
	// }
	// return strenv;
	// }
}
