/*
 * CFrameUtils.java
 * Created on 2012/02/04
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ronproeditor.helpers;

import java.awt.Frame;

import javax.swing.JFrame;

/**
 * @author macchan
 * 
 */
public class CFrameUtils {
	public static void toFront(JFrame frame) {
		if (frame.getExtendedState() >= Frame.MAXIMIZED_BOTH) {
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		} else if (frame.getExtendedState() > Frame.NORMAL) {
			frame.setExtendedState(Frame.NORMAL);
		}
		frame.setVisible(true);
	}
}
