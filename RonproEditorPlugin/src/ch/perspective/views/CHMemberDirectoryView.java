package ch.perspective.views;

import java.io.File;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ch.library.CHFileSystem;

public class CHMemberDirectoryView extends ViewPart{

	@Override
	public void createPartControl(Composite parent) {
		TreeViewer viewer = new TreeViewer(parent);
		viewer.setContentProvider(new ExplororContentProvider());
		viewer.setLabelProvider(new ExplororLabelProvider());
		viewer.setInput(CHFileSystem.getEclipseMemberDir().toJavaFile().listFiles());
		viewer.setSorter(new ViewerSorter() {
			public int category(Object element) {
				if(((File)element).isDirectory()){
					return 0;
				}
				return 1;
			}
		});
	}

	@Override
	public void setFocus() {
		
	}
	
	class ExplororContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return (File[])inputElement;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			File[] children = ((File)parentElement).listFiles();
			return children == null ? new Object[0] : children;
		}

		@Override
		public Object getParent(Object element) {
			return ((File)element).getParentFile();
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length==0 ? false : true;
		}
	}
	
	class ExplororLabelProvider extends LabelProvider {
		
	    public Image getImage(Object element) {
	        if(((File)element).isDirectory()){
	            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
	        } else {
	            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	        }
	    }

	    public String getText(Object element) {
	        File file = (File)element;
	        String name = file.getName();
	        if(name.equals("")){
	            name = file.getPath();
	        }
	        return name;
	    }
	}
}
