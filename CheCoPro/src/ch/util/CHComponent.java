package ch.util;

import javax.swing.event.EventListenerList;

public class CHComponent {
	
	private EventListenerList listenerList = new EventListenerList();
	
	public void addCHListener(CHListener listener) {
		listenerList.add(CHListener.class, listener);
	}
	
	public void removeCHListener(CHListener listener) {
		listenerList.remove(CHListener.class, listener);
	}

	public void fireLoginResult() {
		setMessage("LoginResultReceived");
	}
	
	public void fireEntryResult() {
		setMessage("EntryResultReceived");
	}
	
	public void fireLoginMemberChanged() {
		setMessage("LoginMemberChanged");
	}
	
	public void fireSourceChanged() {
		setMessage("SourceChanged");
	}
	
	public void fireLoguoutResult() {
		setMessage("LogoutResultReceived");
	}
	
	public void fireFileRequest() {
		setMessage("FileRequestReceived");
	}
	
	public void fireFileResponse() {
		setMessage("FileResponseReceived");
	}
	
	public void fireFileListRequest() {
		setMessage("FileListRequestReceived");
	}
	
	public void fireFileListResponse() {
		setMessage("FileListResponseReceived");
	}
	
	public void fireFileSizeNotice() {
		setMessage("FileSizeNotieReceived");
	}
	
	public void setMessage(String message) {
		CHEvent e = new CHEvent(message);
		for(CHListener listener : listenerList.getListeners(CHListener.class)){
			listener.processChanged(e);
		}
	}
}
