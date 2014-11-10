package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;

import bc.b2j.analyzer.BlockConnectorModel;

public abstract class BlockModel {

	private String name = "";
	private String label = "";
	private int id = -1;
	private String type = null;
	private int beforeID = -1;
	private int afterID = -1;
	private int plugID = -1;
	private String text = "";
	private ArrayList<Integer> connectorIDs = new ArrayList<Integer>();
	private ArrayList<BlockConnectorModel> sockets = new ArrayList<BlockConnectorModel>();
	private boolean isCollapsed = false;
	// #ohata added block position
	private int posX = 0;
	private int posY = 0;
	private String comment = "";
	private ArrayList<String> parameterizedType;
	private String javaType;// javaの型
	private String javaLabel;// javaのラベル　メソッド用
	private int parentID = -1;// 変数ブロックの親のID

	// Defines a NULL id for a Block
	public static final int NULL = Integer.valueOf(-1);

	public BlockModel() {
	}

	public void setParentID(int id) {
		this.parentID = id;
	}

	public int getParentID() {
		return this.parentID;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	protected String getGenusName() {
		return name;
	}

	/**
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * 
	 * @return
	 */
	protected String getLabel() {
		return label;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return
	 */
	public String getType() {
		if(javaType != null){
			return javaType;
		}
		return typeString(type);
	}

	protected final String typeString(String typeString) {
		if (typeString == null) {
			if(javaType != null){
				return javaType;
			}else{
				return null;	
			}
		}
		typeString = typeString.replaceAll("＜", "<");
		typeString = typeString.replaceAll("＞", ">");
		return typeString;
	}

	/**
	 * @param privious
	 *            the beforeID
	 */
	public void setBeforeID(int beforeID) {
		this.beforeID = beforeID;
	}

	/**
	 * @return the beforeID
	 */
	protected int getBeforeID() {
		return beforeID;
	}

	/**
	 * @param afterID
	 *            the afterID to set
	 */
	public void setAfterID(int afterID) {
		this.afterID = afterID;
	}

	/**
	 * @return the afterID
	 */
	protected int getAfterID() {
		return afterID;
	}

	/**
	 * 
	 * @param plugID
	 */
	public void setPlug(BlockConnectorModel conn) {
		this.plugID = conn.getId();
	}

	/**
	 * 
	 * @return
	 */
	protected int getPlugID() {
		return plugID;
	}

	/**
	 * @param connectorID
	 *            the connectorId to set
	 */
	public void addConnector(BlockConnectorModel conn) {
		connectorIDs.add(conn.getId());
		sockets.add(conn);
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Integer> getConnectorIDs() {
		return connectorIDs;
	}

	/**
	 * @return the sockets
	 */
	public ArrayList<BlockConnectorModel> getSockets() {
		return sockets;
	}

	/**
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	// #ohata added
	/**
	 * @param posX
	 *            ,posY the position to set
	 */
	public void setPosition(int x, int y) {
		this.posX = x;
		this.posY = y;
	}

	/**
	 * 
	 * @return posX
	 */
	protected int getX() {
		return this.posX;
	}

	/**
	 * 
	 * @return posY
	 */
	protected int getY() {
		return this.posY;
	}

	public void setComment(String str) {
		this.comment = str;
	}

	protected String getComment() {
		return this.comment;
	}

	/**
	 * 
	 * @return
	 */
	protected String getText() {
		return text;
	}

	public void setCollapsed(boolean isCollapsed) {
		this.isCollapsed = isCollapsed;
	}

	protected boolean isCollapsed() {
		return isCollapsed;
	}

	protected String getName() {
		return name;
	}

	/**
	 * 
	 * @param out
	 * @param indent
	 */
	public void print(PrintStream out, int indent) {
	}

	/**
	 * 
	 */
	public void checkError() {

	}

	public void setParameterizedType(ArrayList<String> parameterizedType) {
		this.parameterizedType = parameterizedType;
	}

	public ArrayList<String> getParameterizedType() {
		return this.parameterizedType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getJavaType() {
		if (this.label == null) {
			return "null";
		}
		
		if(this.javaType == null){
			return this.type;
		}
		
		return this.javaType;
	}

	public void setJavaLabel(String javaLabel) {
		this.javaLabel = javaLabel;
	}

	public String getJavaLabel() {
		return this.javaLabel;
	}

	public void makeIndent(PrintStream out, int number) {
		for (int i = 0; i < number; i++) {
			out.print("\t");
		}
	}
}
