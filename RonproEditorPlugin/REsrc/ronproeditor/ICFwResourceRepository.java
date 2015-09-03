package ronproeditor;

import java.beans.PropertyChangeListener;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileFilter;

public interface ICFwResourceRepository {

	public static final String DOCUMENT_CLOSED = "documentClosed";
	public static final String PREPARE_DOCUMENT_CLOSE = "prepareDocumentClose";
	public static final String DOCUMENT_OPENED = "documentOpened";
	public static final String PREPARE_DOCUMENT_OPEN = "prepareDocumentOpen";
	public static final String MODEL_REFRESHED = "modelRefreshed";
	public static final String PROJECT_REFRESHED = "projectChanged";

	public CDirectory getCRootDirectory();

	public CDirectory getCCurrentProject();

	public CFileFilter getDirFilter();

	public CFileFilter getFileFilter();

	public void addPropertyChangeListener(PropertyChangeListener listener);

	public void removePropertyChangeListener(PropertyChangeListener listener);

	public CFile getCCurrentFile();

	public boolean hasCurrentFile();

}
