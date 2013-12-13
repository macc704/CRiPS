package framework;

import java.io.File;
import java.util.List;

public interface DropStrategy {
	
	public void dropPerformed(List<File> files) throws Exception ;

}
