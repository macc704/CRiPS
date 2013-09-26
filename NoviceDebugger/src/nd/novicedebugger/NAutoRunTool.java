package nd.novicedebugger;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nd.com.sun.tools.example.debug.gui.CommandInterpreter;
import nd.com.sun.tools.example.debug.gui.Environment;

public class NAutoRunTool extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constant
	private final int DELAY_MAX = 3000;
	private final int DELAY_DEFAULT = 500;
	private final int DELAY_MIN = 100;

	private Environment env;
	// private VariableTool varTool;
	private CommandInterpreter interpreter;

	// private JToggleButton toggle;
	private JButton contbtn;
	private JButton stopbtn;
	private JButton runbtn;
	private JButton stepbtn;
	private JLabel label;
	// private JScrollBar scroll;
	private JSlider slider;
	// private JTextField text;
	private Timer timer;
	private int sbnum = DELAY_DEFAULT;
	
	private JRadioButton lineMode;
	private JRadioButton betweenMode;
	private ButtonGroup apModeBtns;

	public NAutoRunTool(Environment env) {
		this.env = env;
		// varTool = this.env.getVarTool();
		interpreter = new CommandInterpreter(env, true);
		init();
	}

	private void init() {
		// int extent = (DELAY_MAX + DELAY_MIN) / 10;
		// toggle = new JToggleButton("OFF");
		
		// radio button
		lineMode = new JRadioButton("行");
		lineMode.setSelected(true);
		betweenMode = new JRadioButton("行の間");
		betweenMode.setSelected(false);
		apModeBtns = new ButtonGroup();
		apModeBtns.add(lineMode);
		apModeBtns.add(betweenMode);
		JSplitPane radios = new JSplitPane(JSplitPane.VERTICAL_SPLIT, lineMode, betweenMode);
		radios.setDividerSize(0);
		
		
		// cont button
		contbtn = new JButton(new ImageIcon(getClass().getResource(
				"icon/contbtn.gif")));
		contbtn.setMargin(new Insets(5, 5, 5, 5));
		contbtn.setEnabled(true);
		contbtn.setPreferredSize(new Dimension(25,25));
		
		// stop button
		stopbtn = new JButton(new ImageIcon(getClass().getResource(
				"icon/stopbtn.gif")));
		stopbtn.setMargin(new Insets(5, 5, 5, 5));
		stopbtn.setEnabled(false);
		stopbtn.setPreferredSize(new Dimension(25, 25));

		// run button
		runbtn = new JButton(new ImageIcon(getClass().getResource(
				"icon/runbtn.gif")));
		runbtn.setMargin(new Insets(5, 5, 5, 5));
		runbtn.setEnabled(true);
		runbtn.setPreferredSize(new Dimension(25, 25));

		// label
		label = new JLabel("速度：");

		// scrollbar
		// scroll = new JScrollBar(JDELAY.HORIZONTAL, -DELAY_DEFAULT, extent,
		// -DELAY_MAX, (DELAY_MIN + extent));
		// scroll.setPreferredSize(new Dimension(300, 27));
		// scroll.setBlockIncrement(DELAY_MAX / 5);
		// scroll.setUnitIncrement(DELAY_MAX / 10);

		// slider
		slider = new JSlider(JSlider.HORIZONTAL, -DELAY_MAX, -DELAY_MIN,
				-DELAY_DEFAULT);
		Hashtable<Integer, JComponent> labeltable = new Hashtable<Integer, JComponent>();
		labeltable.put(new Integer(-DELAY_MAX), new JLabel("遅"));
		labeltable.put(new Integer(-DELAY_MIN), new JLabel("速"));
		slider.setLabelTable(labeltable);
		slider.setPaintLabels(true);

		// time text
		// text = new JTextField(-sb.getValue() + "ms");
		// text.setHorizontalAlignment(JTextField.RIGHT);
		// text.setPreferredSize(new JTextField(-(DELAY_MAX + extent) +
		// "ms").getPreferredSize());
		// text.setEditable(false);

		// step button
		stepbtn = new JButton(new ImageIcon(getClass().getResource(
				"icon/stepbtn.gif")));
		stepbtn.setMargin(new Insets(5, 5, 5, 5));
		stepbtn.setEnabled(true);
		stepbtn.setPreferredSize(new Dimension(25, 25));

		// Listener
		lineMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ログ
				env.setAPMode(env.LINEMODE);
				env.getSourceTool().repaint();
			}
		});
		betweenMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ログ
				env.setAPMode(env.BETWEENMODE);
				env.getSourceTool().repaint();
			}
		});
		
		contbtn.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				// ログ
				interpreter.executeCommand("cont");
			}
		});
		
		// toggle.addChangeListener(new ToggleButtonListener());
		stopbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NDebuggerManager.fireStopPressed();
				stopbtn.setEnabled(false);
				runbtn.setEnabled(true);
				stepbtn.setEnabled(true);
				if (timer != null) {
					timer.stop();
				}
			}
		});
		runbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NDebuggerManager.firePlayPressed();
				stopbtn.setEnabled(true);
				runbtn.setEnabled(false);
				stepbtn.setEnabled(false);
				if (timer == null) {
					timer = new Timer(sbnum, new TimerListener());
				} else {
					timer.setDelay(sbnum);
				}
				timer.start();
			}
		});
		// scroll.addAdjustmentListener(new ScrollBarListener());
		slider.addChangeListener(new SliderListener());
		stepbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NDebuggerManager.fireStepPressed();
				interpreter.executeCommand("step");
			}
		});

		// JSplitPane left = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, toggle,
		// sb);
		// JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left,
		// text);

		JSplitPane stop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, stopbtn,
				runbtn);
		JSplitPane step = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, label, slider),
				stepbtn);
		step.setResizeWeight(1);
		JSplitPane action = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, stop,
				step);
		JSplitPane action2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contbtn, action);
	
		action.setPreferredSize(action.getPreferredSize());
		JSplitPane bar = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, radios, action2);
		
		this.add(bar);
	}

	/*
	 * private class ToggleButtonListener implements ChangeListener { public
	 * void stateChanged(ChangeEvent e) { if(toggle.isSelected()){
	 * toggle.setText("ON"); //sb.setEnabled(false); if(timer == null) { timer =
	 * new Timer(sbnum, new TimerListener()); } else { timer.setDelay(sbnum); }
	 * timer.start(); } else { toggle.setText("OFF"); //sb.setEnabled(true);
	 * if(timer != null) { timer.stop(); } } } }
	 */

	/*
	 * private class ScrollBarListener implements AdjustmentListener { public
	 * void adjustmentValueChanged(AdjustmentEvent e) {
	 * if(!scroll.getValueIsAdjusting()){ sbnum = -scroll.getValue(); //
	 * text.setText(sbnum + "ms"); } } }
	 */

	private class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				sbnum = -slider.getValue();
				// text.setText(sbnum + "ms");
				NDebuggerManager.fireSpeedSet(sbnum);
			}
		}
	}

	private class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			interpreter.executeCommand("step");
			// sbnum = -scroll.getValue();
			sbnum = -slider.getValue();
			timer.setDelay(sbnum);

		}
	}

}
