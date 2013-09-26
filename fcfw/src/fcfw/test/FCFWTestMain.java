package fcfw.test;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import fcfw.FCFlowchartPanel;
import fcfw.components.FCCommandPanel;
import fcfw.components.FCIfElsePanel;
import fcfw.components.FCWhilePanel;

/*
 * FCFWTestMain.java
 * Created on 2012/01/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */

/**
 * @author macchan
 * 
 */
public class FCFWTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new FCFWTestMain().run();
	}

	void run() {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(100, 100, 500, 400);
		FCFlowchartPanel fcpanel = new FCFlowchartPanel();
		JScrollPane scroll = new JScrollPane(fcpanel);
		f.getContentPane().add(scroll);
		f.setVisible(true);

		a(fcpanel);
		fcpanel.refresh();
	}

	void a(FCFlowchartPanel fcpanel) {
		fcpanel.getRootPanel().add(new FCCommandPanel("fd(10000);"));
		fcpanel.getRootPanel().add(new FCCommandPanel("rt(180);"));

		FCIfElsePanel if1 = new FCIfElsePanel("x > 0");
		if1.getThenClause().add(new FCCommandPanel("x = 3;"));
		if1.getElseClause().add(new FCCommandPanel("fd(100);"));
		if1.getElseClause().add(new FCCommandPanel("rt(90);"));
		fcpanel.getRootPanel().add(if1);

		FCIfElsePanel if2 = new FCIfElsePanel("x > 0");
		if2.getThenClause().add(new FCCommandPanel("x = 3;"));
		if2.getElseClause().add(new FCCommandPanel("fd(100);"));
		if2.getElseClause().add(new FCCommandPanel("rt(90);"));
		if1.getThenClause().add(if2);

		FCWhilePanel while1 = new FCWhilePanel("x < 10");
		while1.getDoClause().add(new FCCommandPanel("x++;"));
		if2.getThenClause().add(while1);

		FCWhilePanel while2 = new FCWhilePanel("while2");
		while2.getDoClause().add(new FCCommandPanel("x++;"));
		if2.getElseClause().add(while2);

		FCWhilePanel while3 = new FCWhilePanel("while3");
		while3.getDoClause().add(new FCCommandPanel("x++;"));// ‚È‚¢‚Æ‚Ç‚¤‚È‚éH
		while2.getDoClause().add(while3);
	}

	void b(FCFlowchartPanel fcpanel) {

		fcpanel.getRootPanel().add(new FCCommandPanel("fd(10000);"));
		fcpanel.getRootPanel().add(new FCCommandPanel("rt(180);"));

		FCWhilePanel while1 = new FCWhilePanel("x < 10");
		while1.getDoClause().add(new FCCommandPanel("x++;"));
		fcpanel.getRootPanel().add(while1);

		// FCWhilePanel while2 = new FCWhilePanel("while2");
		// while2.getDoClause().add(new FCCommandPanel("x++;"));
		// if1.getElseClause().add(while2);
	}

	void c(FCFlowchartPanel fcpanel) {

		fcpanel.getRootPanel().add(new FCCommandPanel("fd(10000);"));
		fcpanel.getRootPanel().add(new FCCommandPanel("rt(180);"));

		FCIfElsePanel if1 = new FCIfElsePanel("x > 0");
		if1.getThenClause().add(new FCCommandPanel("x = 3;"));
		if1.getElseClause().add(new FCCommandPanel("fd(100);"));
		if1.getElseClause().add(new FCCommandPanel("rt(90);"));
		fcpanel.getRootPanel().add(if1);

		FCWhilePanel while1 = new FCWhilePanel("x < 10");
		while1.getDoClause().add(new FCCommandPanel("x++;"));
		if1.getThenClause().add(while1);

		// FCWhilePanel while2 = new FCWhilePanel("while2");
		// while2.getDoClause().add(new FCCommandPanel("x++;"));
		// if1.getElseClause().add(while2);
	}
}
