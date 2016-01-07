package ch.util;

import java.util.EventListener;

public interface CHListener extends EventListener{

	public void processChanged(CHEvent e);
	
	public void memberSelectorChanged(CHEvent e);
}
