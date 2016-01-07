/*
 * @(#)SourceTool.java	1.15 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package nd.com.sun.tools.example.debug.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
// import javax.swing.JMenuItem;
import javax.swing.JPanel;
// import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;

import nd.com.sun.tools.example.debug.bdi.EventRequestSpec;
import nd.com.sun.tools.example.debug.bdi.ExecutionManager;
import nd.com.sun.tools.example.debug.bdi.LineBreakpointSpec;
import nd.com.sun.tools.example.debug.bdi.SpecErrorEvent;
import nd.com.sun.tools.example.debug.bdi.SpecEvent;
import nd.com.sun.tools.example.debug.bdi.SpecListener;
// import nd.novicedebugger.NDebuggerManager;
import clib.view.textpane.CJavaCodeDocument;
import clib.view.textpane.CTextPaneUtils;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.BreakpointRequest;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SourceTool extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Environment env;

	private ExecutionManager runtime;
	private ContextManager context;
	private SourceManager sourceManager;

	private JList list;
	private ListModel sourceModel;
	private LineNumberView lineNumberView;

	// Information on source file that is on display, or failed to be
	// displayed due to inaccessible source. Used to update display
	// when sourcepath is changed.

	private String sourceName; // relative path name, if showSourceFile
	private Location sourceLocn; // location, if showSourceForLocation

	// private CommandInterpreter interpreter;

	public JList getList(){
		return list;
	}
	
	public SourceTool(Environment env) {

		super(new BorderLayout());

		this.env = env;

		runtime = this.env.getExecutionManager();
		sourceManager = this.env.getSourceManager();
		this.context = this.env.getContextManager();
		// this.interpreter = new CommandInterpreter(env, true);

		sourceModel = new DefaultListModel(); // empty

		list = new JList(sourceModel);
		list.setCellRenderer(new SourceLineRenderer());

		list.setPrototypeCellValue(SourceModel.prototypeCellValue);

		SourceToolListener listener = new SourceToolListener();
		context.addContextListener(listener);
		runtime.addSpecListener(listener);
		sourceManager.addSourceListener(listener);

		// MouseListener squeek = new STMouseListener();
		// list.addMouseListener(squeek);

		this.lineNumberView = new LineNumberView(this);
		JScrollPane scroll = new JScrollPane(list);
		scroll.setRowHeaderView(lineNumberView);
		add(scroll);
		
		// add(new JScrollPane());
	}

	public void setTextFont(Font f) {
		list.setFont(f);
		list.setPrototypeCellValue(SourceModel.prototypeCellValue);
		lineNumberView.setFontInformation(f);
	}

	private class SourceToolListener implements ContextListener,
			SourceListener, SpecListener {

		// ContextListener

		public void currentFrameChanged(CurrentFrameChangedEvent e) {
			showSourceContext(e.getThread(), e.getIndex());
		}

		// Clear source view.
		// sourceModel = new DefaultListModel(); // empty

		// SourceListener

		public void sourcepathChanged(SourcepathChangedEvent e) {
			// Reload source view if its contents depend
			// on the source path.
			if (sourceName != null) {
				showSourceFile(sourceName);
			} else if (sourceLocn != null) {
				showSourceForLocation(sourceLocn);
			}
		}

		// SpecListener

		public void breakpointSet(SpecEvent e) {
			breakpointResolved(e);
		}

		public void breakpointDeferred(SpecEvent e) {
		}

		public void breakpointDeleted(SpecEvent e) {
			BreakpointRequest req = (BreakpointRequest) e.getEventRequest();
			Location loc = req.location();
			if (loc != null) {
				try {
					SourceModel sm = sourceManager.sourceForLocation(loc);
					sm.showBreakpoint(loc.lineNumber(), false);
					showSourceForLocation(loc);
				} catch (Exception exc) {
				}
			}
		}

		public void breakpointResolved(SpecEvent e) {
			BreakpointRequest req = (BreakpointRequest) e.getEventRequest();
			Location loc = req.location();
			try {
				SourceModel sm = sourceManager.sourceForLocation(loc);
				sm.showBreakpoint(loc.lineNumber(), true);
				showSourceForLocation(loc);
			} catch (Exception exc) {
			}
		}

		public void breakpointError(SpecErrorEvent e) {
			breakpointDeleted(e);
		}

		public void watchpointSet(SpecEvent e) {
		}

		public void watchpointDeferred(SpecEvent e) {
		}

		public void watchpointDeleted(SpecEvent e) {
		}

		public void watchpointResolved(SpecEvent e) {
		}

		public void watchpointError(SpecErrorEvent e) {
		}

		public void exceptionInterceptSet(SpecEvent e) {
		}

		public void exceptionInterceptDeferred(SpecEvent e) {
		}

		public void exceptionInterceptDeleted(SpecEvent e) {
		}

		public void exceptionInterceptResolved(SpecEvent e) {
		}

		public void exceptionInterceptError(SpecErrorEvent e) {
		}
	}

	private void showSourceContext(ThreadReference thread, int index) {
		// ### Should use ThreadInfo here.
		StackFrame frame = null;
		if (thread != null) {
			try {
				frame = thread.frame(index);
			} catch (IncompatibleThreadStateException e) {
			}
		}
		if (frame == null) {
			return;
		}
		Location locn = frame.location();
		/*****
		 * if (!showSourceForLocation(locn)) {
		 * env.notice("Could not display source for " +
		 * Utils.locationString(locn)); }
		 *****/
		showSourceForLocation(locn);
	}

	public boolean showSourceForLocation(Location locn) {
		sourceName = null;
		sourceLocn = locn;
		int lineNo = locn.lineNumber();
		if (lineNo != -1) {
			SourceModel source = sourceManager.sourceForLocation(locn);
			if (source != null) {
				showSourceAtLine(source, lineNo - 1);
				return true;
			}
		}
		// Here if we could not display source.
		showSourceUnavailable();
		return false;
	}

	public boolean showSourceFile(String fileName) {
		sourceLocn = null;
		File file;
		if (!fileName.startsWith(File.separator)) {
			sourceName = fileName;
			SearchPath sourcePath = sourceManager.getSourcePath();
			file = sourcePath.resolve(fileName);
			if (file == null) {
				// env.failure("Source not found on current source path.");
				showSourceUnavailable();
				return false;
			}
		} else {
			sourceName = null; // Absolute pathname does not depend on
								// sourcepath.
			file = new File(fileName);
		}
		SourceModel source = sourceManager.sourceForFile(file);
		if (source != null) {
			showSource(source);
			return true;
		}
		showSourceUnavailable();
		return false;
	}

	private void showSource(SourceModel model) {
		setViewModel(model);
	}

	private void showSourceAtLine(SourceModel model, int lineNo) {
		setViewModel(model);
		if (model.isActuallySource && (lineNo < model.getSize())) {
			list.setSelectedIndex(lineNo);
			if (lineNo + 4 < model.getSize()) {
				list.ensureIndexIsVisible(lineNo + 4); // give some context
			}
			list.ensureIndexIsVisible(lineNo);
		}
	}

	private void showSourceUnavailable() {
		// SourceModel model = new
		// SourceModel("[Source code is not available]");
		// setViewModel(model);
	}

	private void setViewModel(SourceModel model) {
		if (model != sourceModel) {
			// install new model
			list.setModel(model);
			sourceModel = model;
		}
	}


	private class SourceLineRenderer extends DefaultListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			// matsuzawa version (Textpane)
			SourceModel.Line line = (SourceModel.Line) value;
			// String linenum = String.format("%4d: ", index + 1);// <-ずれてない?
			String text = line.text;
			try {
				MyJTextPane pane = new MyJTextPane(new CJavaCodeDocument());
				// pane.setLineNumber(index + 1);
				boolean isExecution = env.linenum == index + 1;
				// boolean hasBreakpoint = line.hasBreakpoint;
				boolean hasBreakpoint = false;
				pane.setExecution(isExecution);
				if(isExecution && env.getAPMode() == env.LINEMODE){
					pane.setForeground(list.getSelectionForeground());
					pane.setBackground(list.getSelectionBackground());
				}
				pane.hasBreakpoint(hasBreakpoint);
				pane.setFont(list.getFont());
				CTextPaneUtils.setTabs(pane, 2);
				pane.setText(text);
				pane.setMargin(new Insets(0, 5, 0, 0));
				return pane;
			} catch (Exception ex) {
				setText(text);
				return this;
			}

			// original
			// // ### Should set background highlight and/or icon if breakpoint
			// on
			// // this line.
			// // Configures "this"
			// super.getListCellRendererComponent(list, value, index,
			// isSelected,
			// cellHasFocus);
			//
			// SourceModel.Line line = (SourceModel.Line) value;
			//
			// // ### Tab expansion is now done when source file is read in,
			// // ### to speed up display. This costs a lot of space, slows
			// // ### down source file loading, and has not been demonstrated
			// // ### to yield an observable improvement in display performance.
			// // ### Measurements may be appropriate here.
			// // String sourceLine = expandTabs((String)value);
			//
			// String linenum = String.format("%4d: ", index);
			// setText(linenum + line.text);
			// if (line.hasBreakpoint) {
			// setIcon(Icons.stopSignIcon);
			// } else if (line.isExecutable()) {
			// setIcon(Icons.execIcon);
			// } else {
			// setIcon(Icons.blankIcon);
			// }
			//
			// return this;
		}

		public Dimension getPreferredSize() {
			Dimension dim = super.getPreferredSize();
			// return new Dimension(dim.width, dim.height - 5);
			return new Dimension(dim.width, dim.height);
		}
		
		protected void paintComponent(Graphics g){
			super.paintComponents(g);
			if(this.getComponent(0) instanceof MyJTextPane){
				MyJTextPane mjtp = (MyJTextPane) this.getComponent(0);
				mjtp.paintComponent(g);
			}
		}

		protected class MyJTextPane extends JTextPane {
			// int lineNumber;
			boolean isExecution, hasBreakpoint;
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public MyJTextPane(CJavaCodeDocument cJavaCodeDocument) {
				super(cJavaCodeDocument);
			}

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				int red = hasBreakpoint || !(hasBreakpoint || isExecution) ? 255 : 0;
				int green = !(hasBreakpoint || isExecution) ? 255 : 0;
				int blue = isExecution || !(hasBreakpoint || isExecution) ? 255 : 0;
				g.setColor(new Color(red, green, blue));
//				if (env.getAPMode() == env.LINEMODE) {
//					g.drawRect(0, 0,
//							env.getSourceTool().getPreferredSize().width, getPreferredSize().height - 1);
//				}
				if (env.getAPMode() == env.BETWEENMODE) {
					g.drawLine(0, 0,
							env.getSourceTool().getPreferredSize().width, 0);
				}
				
			}

			public void setExecution(boolean b) {
				this.isExecution = b;
			}
			
			public void hasBreakpoint(boolean b) {
				this.hasBreakpoint = b;
			}
			
		}

	}

	/*
	private class STMouseListener extends MouseAdapter implements MouseListener {
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPopupMenu((Component) e.getSource(), e.getX(), e.getY());
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPopupMenu((Component) e.getSource(), e.getX(), e.getY());
			}
		}

		private void showPopupMenu(Component invoker, int x, int y) {
			JList list = (JList) invoker;
			int ln = list.locationToIndex(new Point(x, y)) + 1;
			SourceModel.Line line = (SourceModel.Line) list.getModel().getElementAt(ln - 1);
			JPopupMenu popup = new JPopupMenu();

			if (line == null) {
				popup.add(new JMenuItem("please select a line"));
			} else if (line.isExecutable()) {
				String className = line.refType.name();
				if (line.hasBreakpoint()) {
					popup.add(commandItem("Clear Breakpoint", "clear "
							+ className + ":" + ln));
				} else {
					popup.add(commandItem("Set Breakpoint", "stop at "
							+ className + ":" + ln));
				}
			} else {
				popup.add(new JMenuItem("not an executable line"));
			}

			popup.show(invoker, x + popup.getWidth() / 2, y + popup.getHeight()
					/ 2);
		}

		private JMenuItem commandItem(final String label, final String cmd) {
			JMenuItem item = new JMenuItem(label);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(label.indexOf("Clear") != -1) {
						NDebuggerManager.fireBreakpointClear();
					}
					if(label.indexOf("Set") != -1) {
						NDebuggerManager.fireBreakpointSet();
					}
					interpreter.executeCommand(cmd);
				}
			});
			return item;
		}
	}
	*/
	
	public class LineNumberView extends JComponent {

		private static final long serialVersionUID = 1L;

		private static final int MARGIN = 5;
		private static final int DEBUG_BUTTON_MARGIN = 20;
		
		private final SourceTool srcTool;

		private FontMetrics fontMetrics;
		private int topInset;
		private int fontAscent;
		private int fontHeight;
		// private int fontDescent;
		// private int fontLeading;

		public LineNumberView(SourceTool srcTool) {
			this.srcTool = srcTool;
			this.setFontInformation(srcTool.list.getFont());
			setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));

			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					int pos = getLineAtPoint(e.getY());
					pos = pos + 1;
				}
			});

		}

		public void setFontInformation(Font font) {
			fontMetrics = getFontMetrics(font);
			fontHeight = fontMetrics.getHeight();
			fontAscent = fontMetrics.getAscent();
			topInset = srcTool.getInsets().top;
		}

		public FontMetrics getFontMetrics() {
			return fontMetrics;
		}

		private int getComponentWidth() {
			return DEBUG_BUTTON_MARGIN + getLineTextWidth();
		}

		private int getLineTextWidth() {
			int lineCount = srcTool.list.getModel().getSize();
			int maxDigits = Math.max(3, String.valueOf(lineCount).length());
			return maxDigits * fontMetrics.stringWidth("0") + MARGIN * 2;
		}

		private int getLineAtPoint(int y) {
			return srcTool.list.locationToIndex(new Point(0, y));
		}

		public Dimension getPreferredSize() {
			return new Dimension(getComponentWidth(), srcTool.getHeight());
		}

		public void paintComponent(Graphics g) {
			Rectangle clip = g.getClipBounds();
			g.setColor(getBackground());
			g.fillRect(clip.x, clip.y, clip.width, clip.height);
			g.setColor(getForeground());
			Rectangle src = srcTool.list.getBounds();
			int base = -src.y - topInset;
			int start = getLineAtPoint(base);
			int end = getLineAtPoint(base + clip.height);
			int rest = base % fontHeight == 0 ? 0 : base % fontHeight + (fontHeight - fontAscent);
			int y = topInset + clip.y - rest;
			for (int i = start; i <= end; i++) {
				String text = String.valueOf(i + 1);
				int x = DEBUG_BUTTON_MARGIN + getLineTextWidth() - MARGIN
						- fontMetrics.stringWidth(text);
				if(env.getAPMode() == env.LINEMODE){
					for(EventRequestSpec evt : env.getExecutionManager().eventRequestSpecs()){
						if(evt instanceof LineBreakpointSpec){
							LineBreakpointSpec levt = (LineBreakpointSpec) evt;
							if(levt.lineNumber() == i + 1){
								g.drawImage(Icons.stopSignIcon.getImage(), (DEBUG_BUTTON_MARGIN - fontHeight) / 2, y + 1, fontHeight, fontHeight, this);
							}
						}
					}
				}
				y = y + fontHeight;
				g.drawString(text, x, y);
			}
			g.setColor(Color.black);
			g.drawLine(DEBUG_BUTTON_MARGIN, 0, DEBUG_BUTTON_MARGIN, srcTool.getHeight());
			g.setColor(Color.blue);

		}
	}
}
