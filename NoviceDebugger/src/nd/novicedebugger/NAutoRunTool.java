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
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nd.com.sun.tools.example.debug.bdi.EventRequestSpec;
import nd.com.sun.tools.example.debug.bdi.LineBreakpointSpec;
import nd.com.sun.tools.example.debug.event.JDIAdapter;
import nd.com.sun.tools.example.debug.event.LocationTriggerEventSet;
import nd.com.sun.tools.example.debug.gui.CommandInterpreter;
import nd.com.sun.tools.example.debug.gui.Environment;

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.StepEvent;

public class NAutoRunTool extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constant
	// private final int DELAY_MAX = 3000;
	// private final int DELAY_DEFAULT = 500;
	// private final int DELAY_MIN = 100;
	private final int SLIDER_MAX = 6;
	private final int SLIDER_DEFAULT = 3;
	private final int SLIDER_MIN = 0;

	private Environment env;
	// private VariableTool varTool;
	private CommandInterpreter interpreter;

	// private JToggleButton toggle;
	// private JButton contbtn;
	// private JButton stopbtn;
	private JToggleButton runbtn;
	private JButton stepbtn;
	private JLabel label;
	// private JScrollBar scroll;
	private JSlider slider;
	// private JTextField text;
	private Timer timer;
	// private int sbnum = DELAY_DEFAULT;
	private int sbnum = SLIDER_DEFAULT;

	private final int speedTable[] = { 1, 100, 300, 500, 800, 1200, 1700 };

	private JRadioButton lineMode;
	private JRadioButton betweenMode;
	private ButtonGroup apModeBtns;

	// icon
	private final ImageIcon playIcon = new ImageIcon(getClass().getResource(
			"icon/runbtn.gif"));
	private final ImageIcon pauseIcon = new ImageIcon(getClass().getResource(
			"icon/stopbtn.gif"));

	public NAutoRunTool(Environment env) {
		this.env = env;
		env.setAutoRunTool(this);
		// varTool = this.env.getVarTool();
		interpreter = new CommandInterpreter(env, true);
		init();
	}

	private int previousSteppedLine = -1;

	private void init() {


		// int extent = (DELAY_MAX + DELAY_MIN) / 10;
		// toggle = new JToggleButton("OFF");

		// radio button
		lineMode = new JRadioButton("�W�����[�h");
		lineMode.setSelected(false);
		betweenMode = new JRadioButton("DENO���[�h");
		betweenMode.setSelected(true);
		apModeBtns = new ButtonGroup();
		apModeBtns.add(lineMode);
		apModeBtns.add(betweenMode);
		JSplitPane radios = new JSplitPane(JSplitPane.VERTICAL_SPLIT, lineMode,
				betweenMode);
		radios.setDividerSize(0);

		// cont button
		/*
		 * contbtn = new JButton(new ImageIcon(getClass().getResource(
		 * "icon/contbtn.gif"))); contbtn.setMargin(new Insets(5, 5, 5, 5));
		 * contbtn.setEnabled(true); contbtn.setPreferredSize(new
		 * Dimension(25,25));
		 */

		// stop button
		/*
		 * stopbtn = new JButton(pauseIcon); stopbtn.setMargin(new Insets(5, 5,
		 * 5, 5)); stopbtn.setEnabled(false); stopbtn.setPreferredSize(new
		 * Dimension(25, 25));
		 */

		// run button
		runbtn = new JToggleButton(playIcon);
		runbtn.setMargin(new Insets(5, 5, 5, 5));
		runbtn.setEnabled(true);
		runbtn.setPreferredSize(new Dimension(25, 25));
		runbtn.setSelectedIcon(pauseIcon);

		// label
		label = new JLabel("���x�F");

		// scrollbar
		// scroll = new JScrollBar(JDELAY.HORIZONTAL, -DELAY_DEFAULT, extent,
		// -DELAY_MAX, (DELAY_MIN + extent));
		// scroll.setPreferredSize(new Dimension(300, 27));
		// scroll.setBlockIncrement(DELAY_MAX / 5);
		// scroll.setUnitIncrement(DELAY_MAX / 10);

		// slider
		// slider = new JSlider(JSlider.HORIZONTAL, -DELAY_MAX, -DELAY_MIN,
		// -DELAY_DEFAULT);
		slider = new JSlider(JSlider.HORIZONTAL, SLIDER_MIN, SLIDER_MAX,
				SLIDER_DEFAULT);
		slider.setInverted(true);
		Hashtable<Integer, JComponent> labeltable = new Hashtable<Integer, JComponent>();
		labeltable.put(new Integer(SLIDER_MAX), new JLabel("�x"));
		labeltable.put(new Integer(SLIDER_MIN), new JLabel("��"));
		slider.setLabelTable(labeltable);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);

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
				NDebuggerManager.fireChangeAPMode("DEFAULT");
				env.setAPMode(env.LINEMODE);
				env.getSourceTool().repaint();
			}
		});
		betweenMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NDebuggerManager.fireChangeAPMode("DENO");
				env.setAPMode(env.BETWEENMODE);
				env.getSourceTool().repaint();
			}
		});

		/*
		 * contbtn.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { // ���O
		 * interpreter.executeCommand("cont"); } });
		 */

		// toggle.addChangeListener(new ToggleButtonListener());
		/*
		 * stopbtn.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { NDebuggerManager.fireStopPressed();
		 * stopbtn.setEnabled(false); runbtn.setEnabled(true);
		 * stepbtn.setEnabled(true);
		 * 
		 * } });
		 */
		runbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JToggleButton source = (JToggleButton) e.getSource();
				// play
				if (source.isSelected()) {
					stepbtn.setEnabled(false);
					if (sbnum != SLIDER_MIN) {
						NDebuggerManager.firePlayPressed();
						if (timer == null) {
							timer = new Timer(speedTable[sbnum],
									new TimerListener());
						} else {
							timer.setDelay(speedTable[sbnum]);
						}
						timer.start();
					} else {
						// ���O�Ƃ�K�v
						interpreter.executeCommand("cont");
						source.setSelected(false);
						stepbtn.setEnabled(true);
						if (timer != null) {
							timer.stop();
						}
					}
				}
				// pause
				else {
					NDebuggerManager.fireStopPressed();
					stepbtn.setEnabled(true);
					if (timer != null) {
						timer.stop();
					}
				}
			}
		});
		// scroll.addAdjustmentListener(new ScrollBarListener());
		slider.addChangeListener(new SliderListener());
		stepbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stepbtn.setEnabled(false);// matsuzawa stepbutton disable�@�\
				NDebuggerManager.fireStepPressed();
				interpreter.executeCommand("step");
				// System.out.println(env.getLinenum());
			}
		});
		// matsuzawa stepbutton disable�@�\
		stepbtn.setEnabled(false);
		env.getExecutionManager().addJDIListener(new JDIAdapter() {
			@Override
			public void locationTrigger(LocationTriggerEventSet evtSet) {
				for (EventIterator it = evtSet.eventIterator(); it.hasNext();) {
					Event evt = it.next();
					if (evt instanceof StepEvent) {
						String mainClassName = env.getContextManager()
								.getMainClassName();
						String locClassName = ((StepEvent) evt).location()
								.declaringType().name();
						int lineNumber = ((StepEvent) evt).location()
								.lineNumber();
						if (mainClassName.equals(locClassName)
								&& lineNumber != previousSteppedLine) {
							stepbtn.setEnabled(true);
							previousSteppedLine = lineNumber;
						}
					}else if(evt instanceof BreakpointEvent){
						int lineNumber = ((BreakpointEvent) evt).location()
								.lineNumber();
						previousSteppedLine = lineNumber;
						stepbtn.setEnabled(true);
					}
					
				}
			}
		});		

		JSplitPane auto = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, label, slider),
				runbtn);
		auto.setResizeWeight(1);
		JSplitPane actionSplitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, auto, stepbtn);

		actionSplitPane.setPreferredSize(actionSplitPane.getPreferredSize());

		// this.add(actionSplitPane);//matsuzawa
		this.add(stepbtn);// matsuzawa

		// JSplitPane bar = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, radios,
		// action);
		// this.add(bar);
	}

	public void bpCheck() {
		if (timer != null) {
			for (EventRequestSpec evt : env.getExecutionManager()
					.eventRequestSpecs()) {
				if (evt instanceof LineBreakpointSpec) {
					LineBreakpointSpec levt = (LineBreakpointSpec) evt;
					if (levt.lineNumber() == env.getLinenum()) {
						timer.stop();
						runbtn.setSelected(false);
						stepbtn.setEnabled(true);
					}
				}
			}
		}
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
				sbnum = slider.getValue();
				// text.setText(sbnum + "ms");
				NDebuggerManager.fireSpeedSet(speedTable[sbnum]);
			}
		}
	}

	private class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			interpreter.executeCommand("step");
			// sbnum = -scroll.getValue();
			// System.out.println(env.getLinenum());
			sbnum = slider.getValue();
			timer.setDelay(speedTable[sbnum]);
		}
	}

}
