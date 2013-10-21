/*
 * NDebuggerListener.java
 * Created on 2013/01/09 by macchan
 * Copyright(c) 2013 Yoshiaki Matsuzawa, Shizuoka Univerisy
 */
package nd.novicedebugger;

/**
 * @author macchan NDebuggerListener
 */
public interface NDebuggerListener {

	public void debugStarted();

	public void stepPressed();

	public void playPressed();

	public void stopPressed();

	public void speedSet(int speed);

	public void debugFinished();
	
	public void contPressed();
	
	public void breakpointSet();
	
	public void breakpointClear();

	public void changeAPMode(String mode);
}
