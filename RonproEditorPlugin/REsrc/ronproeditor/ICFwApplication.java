package ronproeditor;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileElement;

public interface ICFwApplication {

	public void doSetProjectDirectory(CDirectory directory);

	public void doOpen(CFileElement file);

	public ICFwResourceRepository getResourceRepository();

}
