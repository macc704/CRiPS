/*
 * @(#)JDBToolBar.java	1.12 05/11/17
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

import javax.swing.JToolBar;

import nd.com.sun.tools.example.debug.bdi.ExecutionManager;
import nd.novicedebugger.NAutoRunTool;


public class JDBToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Environment env;

	ExecutionManager runtime;
	// ClassManager classManager;
	// SourceManager sourceManager;

	// CommandInterpreter interpreter;
	
	// 自動実行ツール
	private NAutoRunTool autoRunTool;

	public JDBToolBar(Environment env) {
		this.env = env;
		this.runtime = env.getExecutionManager();
		// this.classManager = env.getClassManager();
		// this.sourceManager = env.getSourceManager();
		// this.interpreter = new CommandInterpreter(env, true);

		// ===== Configure toolbar here =====

		// addTool("Run application", "run", "run");
		// addTool("Connect to application", "connect", "connect");
		// addSeparator();
		
		setLayout(new BorderLayout());
		
		autoRunTool = new NAutoRunTool(env);
		this.add(autoRunTool, BorderLayout.EAST);
		
		// addTool("Step into next line", "ステップ", "step");
		// addTool("Step over next line", "locals", "locals");
		// addSeparator();

		// addTool("Step into next instruction", "stepi", "stepi");
		// addTool("Step over next instruction", "nexti", "nexti");
		// addSeparator();

		// addTool("Step out of current method call", "step up", "step up");
		// addSeparator();

		// addTool("Suspend execution", "interrupt", "interrupt");
		// addTool("Continue execution", "cont", "cont");
		// addSeparator();

		// addTool("Display current stack", "where", "where");
		// addSeparator();

		// addTool("Move up one stack frame", "up", "up");
		// addTool("Move down one stack frame", "down", "down");
		// addSeparator();

		// addTool("Display command list", "help", "help");
		// addSeparator();

		// addTool("Exit debugger", "exit", "exit");

		// ==================================
	}

	/*
	private void addTool(String toolTip, String labelText, String command) {
		JButton button = new JButton(labelText);
		button.setToolTipText(toolTip);
		final String cmd = command;
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NDebuggerManager.fireStepPressed();
				interpreter.executeCommand(cmd);
			}
		});
		this.add(button);
	}
	*/

}
