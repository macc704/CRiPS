/*
 * NDebuggerManager.java
 * Created on 2013/01/09 by macchan
 * Copyright(c) 2013 Yoshiaki Matsuzawa, Shizuoka Univerisy
 */
package nd.novicedebugger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author macchan NDebuggerManager
 */
public class NDebuggerManager {

	private static List<NDebuggerListener> listeners = new ArrayList<NDebuggerListener>();

	public static void registerListener(NDebuggerListener listener) {
		if (listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}

	public static void firePlayPressed() {
		for (NDebuggerListener listener : listeners) {
			listener.playPressed();
		}
	}

	public static void fireStopPressed() {
		for (NDebuggerListener listener : listeners) {
			listener.stopPressed();
		}
	}

	public static void fireSpeedSet(int speed) {
		for (NDebuggerListener listener : listeners) {
			listener.speedSet(speed);
		}
	}

	public static void fireDebugStarted() {
		for (NDebuggerListener listener : listeners) {
			listener.debugStarted();
		}
	}

	public static void fireDebugFinished() {
		for (NDebuggerListener listener : listeners) {
			listener.debugFinished();
		}
	}

	public static void fireStepPressed() {
		for (NDebuggerListener listener : listeners) {
			listener.stepPressed();
		}
	}
	
	public static void fireContPressed() {
		for (NDebuggerListener listener : listeners) {
			listener.contPressed();
		}
	}
	
	public static void fireBreakpointSet() {
		for (NDebuggerListener listener : listeners) {
			listener.breakpointSet();
		}
	}

	public static void fireBreakpointClear() {
		for (NDebuggerListener listener : listeners) {
			listener.breakpointClear();
		}
	}
	
	public static void fireChangeAPMode(String mode) {
		for (NDebuggerListener listener : listeners) {
			listener.changeAPMode(mode);
		}
	}
}
