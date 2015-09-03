package ch.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class CHProgressDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel label;
	private JProgressBar progressBar;

	public CHProgressDialog(Frame owner, boolean modal) {
		
		super(owner, modal);
	}
	
	public void init(int min, int max) {
		
		this.setTitle("ファイル受信中");
		this.setSize(256, 128);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		label = new JLabel(Integer.toString(min) + "/"  + Integer.toString(max));
		label.setHorizontalAlignment(JLabel.CENTER);
		progressBar = new JProgressBar(min, max);
		
		Container container = this.getContentPane();
		container.add(label, BorderLayout.NORTH);
		container.add(progressBar, BorderLayout.CENTER);
	}
	
	public void setValue(int n) {
		
		progressBar.setValue(n);
		label.setText(Integer.toString(progressBar.getValue()) + "/"  
		+ Integer.toString(progressBar.getMaximum()));
		
		if (n == progressBar.getMaximum()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.dispose();
		}
	}
	
	public int getValue() {
		
		return progressBar.getValue();
	}
	
	public static void main(String[] args) {
		
		final CHProgressDialog chProgressBar = new CHProgressDialog(null, true);
		chProgressBar.init(0, 10);
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < 11; i++) {
					chProgressBar.setValue(i);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
		chProgressBar.setVisible(true);
	}
}
