package nd.novicedebugger;

//import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import nd.com.sun.tools.example.debug.gui.ContextManager;
import nd.com.sun.tools.example.debug.gui.Environment;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

public class NVariableTool extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable table;
	private JScrollPane scroll;
	private DefaultTableModel tableModel;
	// private DefaultTableColumnModel columnModel;

	private Environment env;

	// private ExecutionManager runtime;
	private ContextManager context;

	// private SourceManager sourceManager;

	private List<Integer> currentStackFrameRows = new ArrayList<Integer>();

	private boolean currentFlag = false;
	private List<Object[]> beforeCurrentVariables = new ArrayList<Object[]>();
	private List<Object[]> currentVariables = new ArrayList<Object[]>();
	private List<Integer> changeVariableRows = new ArrayList<Integer>();
	
	public NVariableTool(Environment env) {
		this.env = env;
		this.context = this.env.getContextManager();
		init();
	}

	@SuppressWarnings("serial")
	private void init() {
		setLayout(new BorderLayout());

		tableModel = new DefaultTableModel(columnNames, 0);

		table = new JTable(tableModel) {
			public TableCellRenderer getCellRenderer(int row, int column) {
				return new DefaultTableCellRenderer() {
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						Component c = super
								.getTableCellRendererComponent(table, value,
										isSelected, hasFocus, row, column);
						if (!currentStackFrameRows.contains(row)) {
							c.setBackground(Color.lightGray);
						}
						if(changeVariableRows.contains(row) && column == 1) {
							c.setBackground(Color.yellow);
						}
						return c;
					}
				};
			}
		};

		// }

		// table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// columnModel = (DefaultTableColumnModel) table.getColumnModel();

		scroll = new JScrollPane(table);
		scroll.setPreferredSize(new java.awt.Dimension(300, 500));
		this.add(scroll);
	}

	private boolean reversed = false;

	public void refreshVariableTable() {
		try {
			ThreadReference current = context.getCurrentThread();
			if (current == null) {
				env.failure("No default thread specified: "
						+ "use the \"thread\" command first.");
				return;
			}
			if (current.frameCount() <= 0) {
				env.failure("Threads have not yet created any stack frames.");
				return;
			}

			LinkedList<StackFrame> frames = new LinkedList<StackFrame>(
					current.frames());
			StackFrame currentFrame = frames.getFirst();

			int row = 0;
			if (reversed == false) {// 古いものから上->下(メソッドは（スタック逆順になっているので）逆順に，
									// 変数順はそのまま）
				Collections.reverse(frames);
				for (StackFrame frame : frames) {
					currentFlag = frame == currentFrame;
					List<LocalVariable> vars = new ArrayList<LocalVariable>(
							frame.visibleVariables());
					for (LocalVariable var : vars) {
						addVariable(var, frame);
						if (currentFlag) {
							currentStackFrameRows.add(row);
							boolean f = true;
							for(Object[] data : beforeCurrentVariables) {
								if(data[3].equals(toString(frame))) {
									if(data[0].equals(var.name()) && !data[1].equals(frame.getValue(var).toString())) {
										changeVariableRows.add(row);
									}
								}
								if(data[0].equals(var.name()) && data[3].equals(toString(frame))){
									f = false;
								}
							}
							if(f) {
								changeVariableRows.add(row);
							}
						}
						row++;
					}
				}
			} else {// 新しいものから上->下（メソッド順はそのまま，変数順は逆順に）
				for (StackFrame frame : frames) {
					currentFlag = frame == currentFrame;
					List<LocalVariable> vars = new ArrayList<LocalVariable>(
							frame.visibleVariables());
					Collections.reverse(vars);
					for (LocalVariable var : vars) {
						addVariable(var, frame);
						if (currentFlag) {
							currentStackFrameRows.add(row);
							boolean f = true;
							for(Object[] data : beforeCurrentVariables) {
								if(data[3].equals(toString(frame))) {
									if(data[0].equals(var.name()) && !data[1].equals(frame.getValue(var).toString())) {
										changeVariableRows.add(row);
									}
								}
								if(data[0].equals(var.name()) && data[3].equals(toString(frame))){
									f = false;
								}
							}
							if(f) {
								changeVariableRows.add(row);
							}
						}
						row++;
					}
				}
			}
		} catch (AbsentInformationException ex) {
			env.failure("Local variable information not available."
					+ " Compile with -g to generate variable information");
		} catch (IncompatibleThreadStateException ex) {
			env.failure("Target VM must be in interrupted state.");
		} catch (Exception ex) {
			env.failure("exception");
		}
		return;
	}

	private void addVariable(LocalVariable var, StackFrame frame)
			throws Exception {
		if (var.isVisible(frame)) {
			// Value val = frame.getValue(var);
			if (var.type() instanceof ArrayType) {
				ArrayReference array = (ArrayReference) frame.getValue(var);
				if(array == null) {
					addVariable(frame, var.name(), var.typeName(), "null");
				}
				else {
					List<Value> list = array.getValues();
					for (int i = 0; i < array.length(); i++) {
						addVariable(frame, var.name() + "[" + i + "]", list.get(i)
								.type().name(), list.get(i));
					}
				}
			} else {
				addVariable(frame, var.name(), var.typeName(),
						frame.getValue(var));
			}
		}
	}

	private String[] columnNames = { "変数名", "値", "型", "位置" };

	private void addVariable(StackFrame stackFrame, String varName,
			String typeName, Object value) {
		String valString = value == null ? "null" : value.toString();
		// String data[] = { methodName, varName + " (" + typeName +
		// "型)",valString };
		Object data[] = { varName, valString, typeName + "型",
				toString(stackFrame) };
		tableModel.addRow(data);
		currentVariables.add(data);
	}

	private String toString(StackFrame frame) {
		Method method = frame.location().method();
		StringBuffer buf = new StringBuffer();
		buf.append(method.name());
		buf.append("(");
		StringBuffer argBuf = new StringBuffer();
		for (Value value : frame.getArgumentValues()) {
			String valString = value == null ? "null" : value.toString();
			if (argBuf.length() > 0) {
				argBuf.append(", ");
			}
			argBuf.append(valString);
		}
		buf.append(argBuf.toString());
		buf.append(")");
		return buf.toString();
	}

	/**
	 * @return the reversed
	 */
	public boolean isReversed() {
		return reversed;
	}

	/**
	 * @param reversed
	 *            the reversed to set
	 */
	public void setReversed(boolean reversed) {
		if (this.reversed == reversed) {
			return;
		}
		this.reversed = reversed;
		update();
	}

	public void update() {
		tableModel.setRowCount(0);
		beforeCurrentVariables = currentVariables;
		currentVariables = new ArrayList<Object[]>();
		currentStackFrameRows.clear();
		changeVariableRows.clear();
		refreshVariableTable();
	}

}
