package ch.util;

import javax.swing.event.EventListenerList;

public class CHComponent {
	
	public static final int PROCESS_EVENT = 0;
	public static final int MEMBERSELECTOR_EVENT = 1;
	
	private EventListenerList listenerList = new EventListenerList();
	
	public void addCHListener(CHListener listener) {
		listenerList.add(CHListener.class, listener);
	}
	
	public void removeCHListener(CHListener listener) {
		listenerList.remove(CHListener.class, listener);
	}

	/*****************
	 * PROCESS_EVENTS
	 *****************/
	public void fireLoginResult() {
		setMessage("LoginResultReceived", PROCESS_EVENT);
	}
	
	public void fireEntryResult() {
		setMessage("EntryResultReceived", PROCESS_EVENT);
	}
	
	public void fireLoginMemberChanged() {
		setMessage("LoginMemberChanged", PROCESS_EVENT);
	}
	
	public void fireSourceChanged() {
		setMessage("SourceChanged", PROCESS_EVENT);
	}
	
	public void fireLoguoutResult() {
		setMessage("LogoutResultReceived", PROCESS_EVENT);
	}
	
	public void fireFileRequest() {
		setMessage("FileRequestReceived", PROCESS_EVENT);
	}
	
	public void fireFileResponse() {
		setMessage("FileResponseReceived", PROCESS_EVENT);
	}
	
	public void fireFileListRequest() {
		setMessage("FileListRequestReceived", PROCESS_EVENT);
	}
	
	public void fireFileListResponse() {
		setMessage("FileListResponseReceived", PROCESS_EVENT);
	}
	
	public void fireFileSizeNotice() {
		setMessage("FileSizeNotieReceived", PROCESS_EVENT);
	}
	
	/***********************
	 * MEMBERSELECTOR_EVENTS
	 ***********************/
	
	public void fireWindowClosing() {
		setMessage("WindowClosing", MEMBERSELECTOR_EVENT);
	}
	
	public void setMessage(String message, int eventType) {
		CHEvent e = new CHEvent(message);
		for(CHListener listener : listenerList.getListeners(CHListener.class)){
			if (eventType == PROCESS_EVENT) {
				listener.processChanged(e);	
			} else if (eventType == MEMBERSELECTOR_EVENT) {
				listener.memberSelectorChanged(e);
			}
		}
	}
}
