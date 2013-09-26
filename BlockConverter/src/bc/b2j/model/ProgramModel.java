package bc.b2j.model;

import java.util.ArrayList;

public class ProgramModel extends BlockModel {

	private ArrayList<PageModel> pages = new ArrayList<PageModel>();
	
	public void addPage(PageModel page){
		pages.add(page);
	}
	
	public ArrayList<PageModel> getPages() {
		return pages;
	}
}
