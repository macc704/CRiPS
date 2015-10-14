package ch.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHSourceChanged;

public class CHComponent {
	
	public static final int PROCESS_EVENT = 0;
	public static final int MEMBERSELECTOR_EVENT = 1;
	
	private EventListenerList listenerList = new EventListenerList();
	
	private String user;
	private List<CHUserState> userStates = new ArrayList<CHUserState>();
	private CHSourceChanged scPacket;
	
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
	
	public void fireMyNameClicked() {
		setMessage("MyNameClicked", MEMBERSELECTOR_EVENT);
	}
	
	public void fireAlreadyOpened(String user) {
		setUser(user);
		setMessage("AlreadyOpened", MEMBERSELECTOR_EVENT);
	}
	
	public void fireNewOpened(String user) {
		setUser(user);
		setMessage("NewOpened", MEMBERSELECTOR_EVENT);
	}
	
	public void fireWindowClosing() {
		setMessage("WindowClosing", MEMBERSELECTOR_EVENT);
	}

	/**************
	 * sendMessage
	 **************/
	
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
	
	/********************
	 * setter and getter
	 ********************/
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public List<CHUserState> getUserStates() {
		return  userStates;
	}
	
	public void setUserStates(List<CHUserState> userStates) {
		this.userStates = userStates;
	}
	
	public CHSourceChanged getScPacket() {
		return scPacket;
	}
	
	public void setScPakcet(CHSourceChanged scPacket) {
		this.scPacket = scPacket;
	}
}
