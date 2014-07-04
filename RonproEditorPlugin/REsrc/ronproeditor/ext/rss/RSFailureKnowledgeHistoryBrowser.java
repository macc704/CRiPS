package ronproeditor.ext.rss;

import generef.knowledge.RSFKWritingPoint;
import generef.knowledge.RSFailureKnowledgeRepository;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import ronproeditor.ext.REGeneRefManager;

public class RSFailureKnowledgeHistoryBrowser extends JFrame {

	private static final long serialVersionUID = 1L;

	private RSFailureKnowledgeRepository repository;
	private REGeneRefManager manager;

	public RSFailureKnowledgeHistoryBrowser(
			RSFailureKnowledgeRepository repository,
			REGeneRefManager manager) {
		this.repository = repository;
		this.manager = manager;
		initialize();
	}

	private void initialize() {
		final JList<RSFKWritingPoint> list = new JList<RSFKWritingPoint>();
		list.setListData(new Vector<RSFKWritingPoint>(repository.getPoints()));

		getContentPane().add(new JScrollPane(list));
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					RSFKWritingPoint point = list.getSelectedValue();
					if (point != null) {
						open(point);
					}
				}
			}
		});
	}

	private void open(RSFKWritingPoint point) {
		manager.openReflectionDialog(point);
	}
}
