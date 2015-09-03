package blockEditorplugin.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;


public class BlockEditorHandler extends AbstractHandler {

	BlockEditorManager manager;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		if(manager == null){
			manager = new BlockEditorManager(window);
		}else{
			manager.openBlockEditor(window);
		}


		return null;
	}


}
