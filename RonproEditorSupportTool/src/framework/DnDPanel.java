package framework;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DnDPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DropStrategy dropStrategy;

	public DnDPanel(String label) {
		this.add(new JLabel(label));

		new DropTarget(this, new DropTargetAdapter() {

			@Override
			public void drop(DropTargetDropEvent e) {
				try {
					Transferable transfer = e.getTransferable();
					if (transfer
							.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						@SuppressWarnings("unchecked")
						List<File> files = (List<File>) (transfer
								.getTransferData(DataFlavor.javaFileListFlavor));
						getDropStrategy().dropPerformed(files);
						e.dropComplete(true);
						informComplete();
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(DnDPanel.this,
							ex.getMessage(), "ó·äOÇ™î≠ê∂ÇµÇ‹ÇµÇΩ",
							JOptionPane.ERROR_MESSAGE);
				}

			}

		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLUE);
		g.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
	}

	public DropStrategy getDropStrategy() {
		return dropStrategy;
	}

	public void setDropStrategy(DropStrategy dropStrategy) {
		this.dropStrategy = dropStrategy;
	}

	private void informComplete() {
		final JLabel label = new JLabel("Complete");
		this.add(label);
		// this.repaint(label.getBounds());
		this.revalidate();
		final JPanel p = this;
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				p.remove(label);
				p.repaint(label.getBounds());
				// p.revalidate();
				// p.setVisible(true);
			}
		};
		thread.start();
	}

	protected void showMessage(Object o) {
		JOptionPane.showMessageDialog(this, o);
	}

}
