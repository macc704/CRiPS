package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class PrivateVariableBlockModel extends VariableBlockModel {
	//#ohata　プライベート変数ブロックモデル　
	
	@Override
	public void checkError() {

		ArrayList<Integer> connectorIDs = getConnectorIDs();
		for (int connectorID : connectorIDs) {
			if (connectorID != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(connectorID).checkError();
			}
		}

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}
	}

	@Override
	public void print(PrintStream out, int indent) {
		
		makeIndent(out, indent);
		
		out.print("private " + getType() + " " + getLabel() );
		ArrayList<Integer> connectorIDs = getConnectorIDs();
		
		for (int connectorID : connectorIDs) {
			if (connectorID != BlockModel.NULL) {
				out.print(" = ");
				BlockToJavaAnalyzer.getBlock(connectorID).print(out, indent);
			}
		}
		
		out.println(";"+"//" + getComment() + "@(" + getX() + ", " + getY() + ")");

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}
	}
	
	public String getPrivateValue(){
		ArrayList<Integer> connectorIDs = getConnectorIDs();
		for (int connectorID : connectorIDs) {
			if (connectorID != BlockModel.NULL) {
				if("private-var-string".equals(getGenusName())){
					return "\"" + BlockToJavaAnalyzer.getBlock(connectorID).getLabel() + "\"";
				} else{
					return BlockToJavaAnalyzer.getBlock(connectorID).getLabel();
				}			
			}
		}
		return null;
	}


}
