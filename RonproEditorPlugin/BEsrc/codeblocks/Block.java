package codeblocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import renderable.BlockImageIcon;
import renderable.BlockImageIcon.ImageLocation;
import renderable.RenderableBlock;
import workspace.ISupportMemento;
import codeblocks.BlockConnector.PositionType;

/**
 * Block holds the mutable prop (data) of a particular block. These mutable prop
 * include socket, before, after and blocks, "bad"-ness. In addition, Block
 * maintains information to describe a particular block?ｽ?ｽs relationship with
 * other blocks.
 *
 */
public class Block implements ISupportMemento {

	/** The ID that is to be assigned to the next new block */
	private static long NEXT_ID = 1;

	// Defines a NULL id for a Block
	public static final Long NULL = Long.valueOf(-1);

	/** A universal hashmap of all the Block instances */
	private final static HashMap<Long, Block> ALL_BLOCKS = new HashMap<Long, Block>();

	// block identifying information
	private final Long blockID;
	private String label;
	private int lineNumber = -1;
	private Long parentID = null;
	private String pageLabel = null;
	/** created by sakai lab 2011/11/20 */
	private String headerLabel = null;
	/** created by sakai lab 2011/11/22 */
	private String footerLabel = null;
	private String genusName;

	// 新規追加属性
	private String javaType;
	private String javaLabel;

	// block connection information
	private List<BlockConnector> sockets;
	private BlockConnector plug;
	private BlockConnector before;
	private BlockConnector after;

	/**
	 * The expand-groups. A list is used instead of a map, because we don't
	 * expect a lot of groups in one block.
	 */
	private List<List<BlockConnector>> expandGroups;

	// this flag determines if this block will create stubs if its
	// genus species that it does. if false, then this block even though
	// it may have stubs will not create stubs
	private boolean linkToStubs = true;

	// block state information
	private boolean isBad = false;
	// private boolean isPermanentlyBad = false;
	private String badMsg;

	// focus information
	private boolean hasFocus = false;

	// additional properties of a block
	// can not contain keys that are within genus
	private HashMap<String, String> properties = new HashMap<String, String>();

	// argument descriptions
	private ArrayList<String> argumentDescriptions;

	// ohata added
	private Map<String, List<Map<String, List<String>>>> methods = new HashMap<String, List<Map<String, List<String>>>>();

	private ArrayList<String> parameterizedTypes;

	public Map<String, List<Map<String, List<String>>>> getMethods() {
		return methods;
	}

	public String getJavaType() {

		return this.javaType;

	}

	/**
	 * Constructs a new Block from the specified information. This class
	 * constructor is protected as block loading from XML content or the
	 * (careful!) creation of its subclasses should override BlockID assignment.
	 *
	 * @param id
	 *            the Block ID of this
	 * @param genusName
	 *            the String name of this block's BlockGenus
	 * @param label
	 *            the String label of this Block
	 */
	protected Block(Long id, String genusName, String label, boolean linkToStubs) {
		if (ALL_BLOCKS.containsKey(id)) {// hashMap にidがあるか？
//			Block dup = ALL_BLOCKS.get(id);// 既存のものを取り出す
			// System.out.println("pre-existing block is: " + dup +
			// " with genus "+ dup.getGenusName() + " and label " +
			// dup.getBlockLabel());
			assert !ALL_BLOCKS.containsKey(id) : "Block id: " + id
					+ " already exists!  BlockGenus " + genusName + " label: "
					+ label;
		}
		this.blockID = id;

		// if this assigned id value equals the next id to automatically assign
		// a new block, increment the next id value by 1
		if (id.longValue() == NEXT_ID)
			NEXT_ID++;

		sockets = new ArrayList<BlockConnector>();// 初期容量10
													// BlockConnectorを格納するリスト
		argumentDescriptions = new ArrayList<String>();// 初期容量10 String型を格納するリスト
		// copy connectors from BlockGenus
		try {
			BlockGenus genus = BlockGenus.getGenusWithName(genusName);// genusNameからgenusのインスタンス生成
			if (genus == null)// genusNameのblockgenusが存在しない genus:種類
				throw new Exception("genusName: " + genusName
						+ " does not exist.");

			// copy the block connectors from block genus
			Iterable<BlockConnector> iter = genus.getInitSockets();
			for (BlockConnector con : iter) {
				sockets.add(new BlockConnector(con));
			}

			if (genus.getInitPlug() != null)
				plug = new BlockConnector(genus.getInitPlug());

			if (genus.getInitBefore() != null)
				before = new BlockConnector(genus.getInitBefore());

			if (genus.getInitAfter() != null)
				after = new BlockConnector(genus.getInitAfter());

			this.genusName = genusName;

			this.label = label;

			this.methods = genus.getMethods();// ohata added

			setHeaderLabel(BlockGenus.getGenusWithName(genusName)
					.getInitHeaderLabel());
			setFooterLabel(BlockGenus.getGenusWithName(genusName)
					.getInitFooterLabel());

			javaLabel = (BlockGenus.getGenusWithName(genusName).getJavaLabel());
			javaType = (BlockGenus.getGenusWithName(genusName).getJavaType());

			Iterable<String> arguumentIter = genus.getInitialArgumentDescriptions();

			for (String arg : arguumentIter) {
				argumentDescriptions.add(arg.trim());
			}

			this.expandGroups = new ArrayList<List<BlockConnector>>(
					genus.getExpandGroups());

			// add to ALL_BLOCKS
			// warning: publishing this block before constructor finishes has
			// the
			// potential to cause some problems such as data races
			// other threads could access this block from getBlock()
			ALL_BLOCKS.put(this.blockID, this);

			// add itself to stubs hashmap
			// however factory blocks will have entries in hashmap...
			if (linkToStubs && this.hasStubs()) {
				BlockStub.putNewParentInStubMap(this.blockID);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs a new <code>Block</code> instance. Using the genusName
	 * specified of this Block's corresponding BlockGenus, this constructor
	 * populates this Block with its genus information.
	 *
	 * @param genusName
	 *            the name of its associated <code>BlockGenus</code>
	 * @param label
	 *            the label of this Block.
	 * @param linkToStubs
	 *            if true, this block can have stubs and be linked to them; if
	 *            false, then this block even though the genus specifies it will
	 *            not be linked to stubs
	 */
	public Block(String genusName, String label, boolean linkToStubs) {
		// more will go into constructor;
		this(NEXT_ID, genusName, label, linkToStubs);

		NEXT_ID++;

		while (ALL_BLOCKS.containsKey(NEXT_ID))
			NEXT_ID++;
	}

	/**
	 * Constructs a new <code>Block</code> instance. Using the genusName
	 * specified of this Block's corresponding BlockGenus, this constructor
	 * populates this Block with its genus information.
	 *
	 * @param genusName
	 *            the name of its associated <code>BlockGenus</code>
	 * @param label
	 *            the label of this Block.
	 */
	public Block(String genusName, String label) {
		// more will go into constructor;
		this(NEXT_ID, genusName, label, true);

		NEXT_ID++;

		while (ALL_BLOCKS.containsKey(NEXT_ID))
			NEXT_ID++;
	}

	/**
	 * Constructs a new <code>Block</code> instance. Using the genusName
	 * specified of this Block's corresponding BlockGenus, this constructor
	 * populates this Block with its genus information.
	 *
	 * @param genusName
	 *            the name of its associated <code>BlockGenus</code>
	 */
	public Block(String genusName) {
		this(genusName, BlockGenus.getGenusWithName(genusName)
				.getInitialLabel());
	}

	/**
	 * Constructs a new <code>Block</code> instance. Using the genusName
	 * specified of this Block's corresponding BlockGenus, this constructor
	 * populates this Block with its genus information.
	 *
	 * @param genusName
	 *            the name of its associated <code>BlockGenus</code>
	 * @param if true, this block can have stubs and be linked to them; if
	 *        false, then this block even though the genus specifies it will not
	 *        be linked to stubs
	 */
	public Block(String genusName, boolean linkToStubs) {
		this(genusName, BlockGenus.getGenusWithName(genusName).getInitialLabel(), linkToStubs);
	}

	/**
	 * Returns the Block instance with the specified blockID
	 *
	 * @param blockID
	 * @return the Block instance with the specified blockID
	 */
	public static Block getBlock(Long blockID) {
		return ALL_BLOCKS.get(blockID);
	}

	/**
	 * Clears all block instances and resets id assigment.
	 */
	public static void reset() {
		// System.out.println("reseting all blocks");
		ALL_BLOCKS.clear();
		NEXT_ID = 1;
	}

	// /////////////////
	// BLOCK prop
	// /////////////////
	/**
	 * Returns the block ID of this
	 *
	 * @return the block ID of this
	 */
	public Long getBlockID() {
		return blockID;
	}

	/**
	 * Sets the block property with the specified property and value. If this
	 * block's genus already contains a value with the same property, then the
	 * specified property will not be added to this block's property collection.
	 *
	 * @param property
	 *            the property key to set
	 * @param value
	 *            the value associated with this property
	 * @return true if this property was set successfully
	 */
	public boolean setProperty(String property, String value) {
		if (getGenus().getProperty(property) != null)
			return false;
		else {
			properties.put(property, value);
			return true;
		}
	}

	/**
	 * Returns the block label of this
	 *
	 * @return the block label of this
	 */
	public String getBlockLabel() {
		return getGenus().getLabelPrefix() + label
				+ getGenus().getLabelSuffix();
	}

	/**
	 * Returns true if this block has a page label and it is non-empty
	 *
	 * @return true if this block has a page label and it is non-empty
	 */
	public boolean hasPageLabel() {
		return pageLabel != null && !pageLabel.equals("");
	}

	/**
	 * created by sakai lab 2011/11/20 Returns true if this block has a header
	 * label and it is non-empty
	 *
	 * @return true if this block has a header label and it is non-empty
	 */
	public boolean hasHeaderLabel() {
		return headerLabel != null && !headerLabel.equals("");
	}

	/**
	 * created by sakai lab 2011/11/21 Returns true if this block has a footer
	 * label and it is non-empty
	 *
	 * @return true if this block has a footer label and it is non-empty
	 */
	public boolean hasFooterLabel() {
		return footerLabel != null && !footerLabel.equals("");
	}

	/**
	 * Returns the page label string of this
	 *
	 * @return the page label string of this
	 */
	public String getPageLabel() {
		return pageLabel;
	}

	/**
	 * created by sakai lab 2011/11/20 Returns the header label string of this
	 *
	 * @return the header label string of this
	 */
	public String getHeaderLabel() {
		return headerLabel;
	}

	/**
	 * created by sakai lab 2011/11/22 Returns the footer label string of this
	 *
	 * @return the footer label string of this
	 */
	public String getFooterLabel() {
		return footerLabel;
	}

	/**
	 * Sets the block label of this iff this block label is editable
	 *
	 * @param newLabel
	 *            the desired label
	 */
	public void setBlockLabel(String newLabel) {
		if (this.linkToStubs && this.hasStubs())
			BlockStub.parentNameChanged(this.label, newLabel, this.blockID);
		label = newLabel;
	}

	/**
	 * Sets the page label of this
	 *
	 * @param newPageLabel
	 *            the desired page label
	 */
	public void setPageLabel(String newPageLabel) {
		// update stubs
		if (this.linkToStubs && this.hasStubs())
			BlockStub.parentPageLabelChanged(newPageLabel, this.blockID);

		pageLabel = newPageLabel;
	}

	/**
	 * created by sakai lab 2011/11/20 Sets the header label of this
	 *
	 * @param neHeaderLabel
	 *            the desired header label
	 */
	public void setHeaderLabel(String newHeaderLabel) {
		headerLabel = newHeaderLabel;
	}

	/**
	 * created by sakai lab 2011/11/22 Sets the header label of this
	 *
	 * @param neHeaderLabel
	 *            the desired footer label
	 */
	public void setFooterLabel(String newfooterLabel) {
		footerLabel = newfooterLabel;
	}

	/**
	 * Returns the BlockGenus of this
	 *
	 * @return the BlockGenus of this
	 */
	private BlockGenus getGenus() {
		return BlockGenus.getGenusWithName(genusName);
	}

	/**
	 * Changes the genus of this block, while maintaining this current blocks
	 * relationships with other blocks it's connected to.
	 *
	 * @param genusName
	 *            the String name of the BlockGenus to change this Block to
	 */
	public void changeGenusTo(String genusName) {
		this.genusName = genusName;
		label = BlockGenus.getGenusWithName(genusName).getInitialLabel();
		if (headerLabel != null) {
			headerLabel = BlockGenus.getGenusWithName(genusName)
					.getInitHeaderLabel();
		}

	}

	// //////////////////////////////
	// BLOCK CONNECTION METHODS
	// //////////////////////////////

	/**
	 * Returns the Block ID connected to the before connector of this;
	 * Block.Null if this does not have a before block
	 *
	 * @return the Block ID connected to the before connector of this;
	 *         Block.Null if this does not have a before block
	 */
	public Long getBeforeBlockID() {
		if (before == null)
			return Block.NULL;
		return before.getBlockID();
	}

	/**
	 * Returns the Block ID connected to the after connector of this; Block.Null
	 * if this does not have an after block
	 *
	 * @return the Block ID connected to the after connector of this; Block.Null
	 *         if this does not have an after block
	 */
	public Long getAfterBlockID() {
		if (after == null)
			return Block.NULL;
		return after.getBlockID();
	}

	/**
	 * Returns the BlockConnector representing the connection to the block after
	 * this
	 *
	 * @return the BlockConnector of the after connector
	 */
	public BlockConnector getAfterConnector() {
		return after;
	}

	/**
	 * Returns the BlockConnector representing the connection to the block
	 * before this
	 *
	 * @return the BlockConnector of the before connector
	 */
	public BlockConnector getBeforeConnector() {
		return before;
	}

	/**
	 * Resets the before and after connectors to their initial kinds. Only
	 * privileged classes (ie. BlockStub) should call this method.
	 */
	void resetBeforeAndAfter() {
		before = new BlockConnector(getGenus().getInitBefore());
		after = new BlockConnector(getGenus().getInitAfter());
	}

	/**
	 * Removes the before and after connectors. Only privileged classes (ie.
	 * BlockStub) should call this method.
	 */
	void removeBeforeAndAfter() {
		before = null;
		after = null;
	}

	/**
	 * Return the expand-group for the given group. Can be null if group doesn't
	 * exist.
	 */
	private static List<BlockConnector> getExpandGroup(
			List<List<BlockConnector>> groups, String group) {
		for (List<BlockConnector> list : groups) {
			// Always at least one element in the group.
			if (list.get(0).getExpandGroup().equals(group)) {
				return list;
			}
		}
		return null;
	}

	/**
	 * Expand a socket group in this block. For now, all new sockets will be
	 * added after the last socket in the group.
	 */
	private void expandSocketGroup(String group) {
		List<BlockConnector> expandSockets = getExpandGroup(expandGroups, group);
		assert expandSockets != null;

		// Search for the socket to insert after.
		int index = sockets.size() - 1;
		String label = expandSockets.get(expandSockets.size() - 1).getLabel();
		for (; index >= 0; index--) {
			BlockConnector conn = sockets.get(index);
			if (conn.getLabel().equals(label)
					&& conn.getExpandGroup().equals(group))
				break;
		}

		assert index >= 0;

		// Insert all the new sockets
		for (BlockConnector conn : expandSockets) {
			index++;
			BlockConnector newConn = new BlockConnector(conn);
			sockets.add(index, newConn);
		}
	}

	/**
	 * Shrink a socket group (un-expand it).
	 */
	private void shrinkSocketGroup(BlockConnector socket) {
		String group = socket.getExpandGroup();
		List<BlockConnector> expandSockets = getExpandGroup(expandGroups, group);
		assert expandSockets != null;

		// Search for the first socket in the group, if not the expandable
		// one.
		String label = expandSockets.get(0).getLabel();
		int index = getSocketIndex(socket);
		for (; index >= 0; index--) {
			BlockConnector con = sockets.get(index);
			if (con.getLabel().equals(label)
					&& con.getExpandGroup().equals(group))
				break;
		}

		assert index >= 0;

		// Remove all the sockets.
		removeSocket(index);
		int total = expandSockets.size();
		for (int i = 1; i < total;) {
			BlockConnector con = sockets.get(index);
			if (con.getLabel().equals(expandSockets.get(i).getLabel())
					&& con.getExpandGroup().equals(group)) {
				removeSocket(index);
				i++;
			} else {
				index++;
			}
		}
	}

	/**
	 * Returns true if the given expandable socket can be removed.
	 */
	private boolean canRemoveSocket(BlockConnector socket) {
		int total = sockets.size();
		int first = -1;
		for (int i = 0; i < total; i++) {
			BlockConnector conn = sockets.get(i);
			if (conn == socket) {
				if (first == -1)
					first = i;
				else
					return true;
			} else if (conn.getPositionType().equals(socket.getPositionType())
					&& conn.isExpandable() == socket.isExpandable()
					&& conn.initKind().equals(socket.initKind())
					&& conn.getExpandGroup().equals(socket.getExpandGroup())) {
				if (first == -1)
					first = i;
				else
					return true;
			}
		}

		// If the socket is the first and last of its kind, then we can NOT
		// remove it. (We also can't remove it if they're both -1, obviously.)
		return false;
	}

	/**
	 * Informs this Block that a block with id connectedBlockID has connected to
	 * the specified connectedSocket
	 */
	public void blockConnected(BlockConnector connectedSocket,
			Long connectedBlockID) {
		if (connectedSocket.isExpandable()) {
			if (connectedSocket.getExpandGroup().length() > 0) {
				// Part of an expand group
				expandSocketGroup(connectedSocket.getExpandGroup());
			} else {
				// expand into another one
				int index = getSocketIndex(connectedSocket);
				if (isProcedureDeclBlock())
					addSocket(index + 1, connectedSocket.initKind(),
							connectedSocket.getPositionType(), "",
							connectedSocket.isLabelEditable(),
							connectedSocket.isExpandable(), Block.NULL);
				else
					addSocket(index + 1, connectedSocket.initKind(),
							connectedSocket.getPositionType(),
							connectedSocket.getLabel(),
							connectedSocket.isLabelEditable(),
							connectedSocket.isExpandable(), Block.NULL);
			}
		}

		// NOTE: must update the sockets of this before updating its stubs as
		// stubs use this as a reference to update
		// its own sockets
		// if block has stubs, update its stubs as well
		if (hasStubs() && connectedSocket.getPositionType().toString().equals("SINGLE"))
				BlockStub.parentConnectorsChanged(blockID);
	}

	/**
	 * Informs this Block that a block has disconnected from the specified
	 * disconnectedSocket
	 *
	 * @param disconnectedSocket
	 */
	public void blockDisconnected(BlockConnector disconnectedSocket) {
		if (disconnectedSocket.isExpandable() && canRemoveSocket(disconnectedSocket)) {
			if (disconnectedSocket.getExpandGroup().length() > 0)
				shrinkSocketGroup(disconnectedSocket);
			else
				removeSocket(disconnectedSocket);
		}

		// NOTE: must update the sockets of this before updating its stubs as
		// stubs use this as a reference to update
		// its own sockets
		// if block has stubs, update its stubs as well
		if (hasStubs() && "BOTTOM".equals(disconnectedSocket.getPositionType())){
				BlockStub.parentConnectorsChanged(blockID);
		}
	}

	// //////////////////////////////
	// BLOCK SOCKET AND PLUG METHODS
	// //////////////////////////////
	/**
	 * Returns an unmodifiable iterable over a safe copy of the Sockets of this
	 *
	 * @return an unmodifiable iterable over a safe copy of the Sockets of this
	 */
	public Iterable<BlockConnector> getSockets() {
		return Collections.unmodifiableList(new ArrayList<BlockConnector>(
				sockets));
	}

	/**
	 * Returns the number of sockets of this
	 *
	 * @return the number of sockets of this
	 */
	public int getNumSockets() {
		return sockets.size();
	}

	/**
	 * Returns the socket (BlockConnector instance) at the specified index
	 *
	 * @param index
	 *            the index of the desired socket. 0 <= index < getNumSockets()
	 * @return the socket (BlockConnector instance) at the specified index
	 */
	public BlockConnector getSocketAt(int index) {
		assert index < sockets.size() : "Index " + index
				+ " is greater than the num of sockets: " + sockets.size()
				+ " of " + this;
		return sockets.get(index);
	}

	/**
	 * Replaces the socket at the specified index with the new specified
	 * parameters
	 *
	 * @param index
	 *            of the BlockConnector to replace
	 * @param isLabelEditable
	 *            is true iff this BlockConnector can have its labels edited.
	 * @return true if socket successfully replaced
	 */
	public boolean setSocketAt(int index, String kind, PositionType pos,
			String label, boolean isLabelEditable, boolean isExpandable,
			Long blockID) {
		if (sockets.set(index, new BlockConnector(kind, pos, label,
				isLabelEditable, isExpandable, blockID)) != null)
			return true;
		else
			return false;
	}

	/**
	 * Returns the index number of a given socket
	 *
	 * @param socket
	 *            a socket of this block
	 * @return the index number of a given socket or -1 if socket doesn't exists
	 *         on the block
	 */
	public int getSocketIndex(BlockConnector socket) {
		for (int i = 0; i < sockets.size(); i++) {

			BlockConnector currentSocket = sockets.get(i);

			// check for reference equality
			if (currentSocket == socket) {
				return i;
			}
		}
		// then it was not found
		return -1;
	}

	/**
	 * Adds another socket to this. Socket is added to the "end" of socket list.
	 *
	 * @param kind
	 *            the socket kind of new socket
	 * @param label
	 *            the label of the new socket
	 * @param positionType
	 *            the BlockConnector.PositionType of the new connector
	 * @param isLabelEditable
	 *            is true iff this BlockConnector can have its labels edited.
	 * @param isExpandable
	 *            true iff this connector can expand to another connector
	 * @param blockID
	 *            the block id of the block attached to new socket
	 */
	public void addSocket(String kind, PositionType positionType, String label,
			boolean isLabelEditable, boolean isExpandable, Long blockID) {
		BlockConnector newSocket = new BlockConnector(kind, positionType,
				label, isLabelEditable, isExpandable, blockID);
		sockets.add(newSocket);
	}

	/**
	 * Adds another socket to this. Socket is inserted at the specified index of
	 * socket list, where 0 is the first socket. if index is equal
	 * numOfSockets(), then socket is added to the end of the socket list. If
	 * index > numOfSockets(), an exception is thrown.
	 *
	 * @param index
	 *            the index to insert new socket to
	 * @param kind
	 *            the socket kind of new socket
	 * @param label
	 *            the label of the new socket
	 * @param positionType
	 *            the BlockConnector.PositionType of the new connector
	 * @param isLabelEditable
	 *            is true iff this BlockConnector can have its labels edited.
	 * @param isExpandable
	 *            true iff this connector can expand to another connector
	 * @param blockID
	 *            the block id of the block attached to new socket
	 */
	public BlockConnector addSocket(int index, String kind,
			PositionType positionType, String label, boolean isLabelEditable,
			boolean isExpandable, Long blockID) {
		BlockConnector newSocket = new BlockConnector(kind, positionType,
				label, isLabelEditable, isExpandable, blockID);
		sockets.add(index, newSocket);
		return newSocket;
	}

	/**
	 * Removes the socket at the specified index
	 *
	 * @param index
	 *            the index of the socket to remove
	 */
	public void removeSocket(int index) {
		removeSocket(sockets.get(index));
	}

	/**
	 * Removes specified socket
	 *
	 * @param socket
	 *            BlockConnector to remove from this
	 */
	public void removeSocket(BlockConnector socket) {
		// disconnect any blocks connected to socket
		if (socket.getBlockID() != Block.NULL) {
			Block connectedBlock = Block.getBlock(socket.getBlockID());
			connectedBlock.getConnectorTo(this.blockID).setConnectorBlockID(
					Block.NULL);
			socket.setConnectorBlockID(Block.NULL);
			RenderableBlock.getRenderableBlock(blockID).blockDisconnected(socket);
		}
		sockets.remove(socket);
	}

	// plug information
	/**
	 * Returns if BlockConnector plug exists
	 *
	 * @return if BlockConnector plug exists
	 */
	public boolean hasPlug() {
		return !(plug == null);
	}

	/**
	 * Returns the BlockConnector plug of this
	 *
	 * @return the BlockConnector plug of this
	 */
	public BlockConnector getPlug() {
		return plug;
	}

	/**
	 * Sets the plug of this.
	 *
	 * @param kind
	 *            the socket kind of plug
	 * @param label
	 *            the label of the plug
	 * @param positionType
	 *            the BlockConnector.PositionType of this plug
	 * @param isLabelEditable
	 *            is true iff this BlockConnector can have its labels edited.
	 * @param blockID
	 *            the block id of the block attached to plug
	 */
	public void setPlug(String kind, PositionType positionType, String label,
			boolean isLabelEditable, Long blockID) {
		plug = new BlockConnector(kind, positionType, label, isLabelEditable,
				false, blockID);
	}

	/**
	 * Sets the plug kind of this
	 *
	 * @param kind
	 *            the desired plug kind
	 */
	public void setPlugKind(String kind) {
		assert plug != null : "Plug is null!  Can not set this information.";

		if (hasPlug())
			plug.setKind(kind);
	}

	/**
	 * Sets the plug label of this
	 *
	 * @param label
	 *            the desired plug label
	 */
	public void setPlugLabel(String label) {
		assert plug != null : "Plug is null!  Can not set this information.";

		if (hasPlug())
			plug.setLabel(label);
	}

	/**
	 * Sets the block attached to this plug
	 *
	 * @param id
	 *            the block id to attach to this plug
	 */
	public void setPlugBlockID(Long id) {
		assert plug != null : "Plug is null!  Can not set this information.";

		if (hasPlug())
			plug.setConnectorBlockID(id);
	}

	/**
	 * Return plug kind; null if plug does not exist
	 *
	 * @return plug kind; null if plug does not exist
	 */
	public String getPlugKind() {
		if (hasPlug())
			return plug.getKind();
		return null;
	}

	/**
	 * Return plug label; null if plug does not exist
	 *
	 * @return plug label; null if plug does not exist
	 */
	public String getPlugLabel() {
		if (hasPlug())
			return plug.getLabel();
		return null;
	}

	/**
	 * Return plug block id; null if plug does not exist
	 *
	 * @return plug block id; null if plug does not exist
	 */
	public Long getPlugBlockID() {
		if (plug == null)
			return Block.NULL;
		return plug.getBlockID();
	}

	/**
	 * Removes the plug.
	 */
	void removePlug() {
		plug = null;
	}

	/**
	 * Searches for the BlockConnector linking this block to another block
	 *
	 * @param otherBlockID
	 *            the Block ID if the other block
	 * @return the BlockConnector linking this block to the other block
	 */
	public BlockConnector getConnectorTo(Long otherBlockID) {
		if (otherBlockID == null || otherBlockID == Block.NULL)
			return null;
		if (getPlugBlockID().equals(otherBlockID))
			return plug;
		if (getBeforeBlockID().equals(otherBlockID))
			return before;
		if (getAfterBlockID().equals(otherBlockID))
			return after;
		for (BlockConnector socket : getSockets())
			if (socket.getBlockID().equals(otherBlockID))
				return socket;
		return null;
	}

	// //////////////
	// "BAD" METHODS
	// //////////////
	/**
	 * Returns true iff this block is "bad." Bad means that this block has an
	 * associated compile error.
	 */
	public boolean isBad() {
		return isBad;
	}

	/**
	 * Sets the "bad"-ness of this block. Bad means that this block has an
	 * associated compile error.
	 *
	 * @param isBad
	 */
	public void setBad(boolean isBad) {
		this.isBad = isBad;
	}

	/**
	 * Returns the "bad" message of this block.
	 */
	public String getBadMsg() {
		return badMsg;
	}

	/**
	 * Sets the message describing this block's badness. In other words, the
	 * message describes the compile error associated with this block.
	 *
	 * @param badMsg
	 */
	public void setBadMsg(String badMsg) {
		this.badMsg = badMsg;
	}

	// //////////////
	// FOCUS METHODS
	// //////////////

	/**
	 * Returns true iff this block has focus. Focus means it is currently
	 * selected in the workspace. Multiple blocks can have focus simultaniously.
	 */
	public boolean hasFocus() {
		return hasFocus;
	}

	/**
	 * Sets the focus state of the block. Should only be used by FocusManager.
	 *
	 * @param hasFocus
	 */
	public void setFocus(boolean hasFocus) {
		this.hasFocus = hasFocus;
	}

	// //////////////////
	// DEFAULT ARGUMENTS
	// //////////////////
	/**
	 * Links all the default arguments specified in the <code>BlockGenus</code>
	 * of this to the specified sockets of this block. By default a new
	 * <code>Block</code> does not have default arguments attached. Each index
	 * in the Long list corresponds to the index of the socket the default
	 * argument is attached to. If an element in this list is Block.NULL, then
	 * no default argument exists for that socket or there already is a block
	 * attached at that socket.
	 *
	 * Default arguments are linked whenever a block is dragged to the workspace
	 * for the first time
	 *
	 * @return Returns a Long list of the newly created default argument block
	 *         IDs; null if this block has none.
	 */
	public Iterable<Long> linkAllDefaultArgs() {
		if (getGenus().hasDefaultArgs()) {
			ArrayList<Long> defargIDs = new ArrayList<Long>();
			for (BlockConnector con : sockets) {
				Long id = con.linkDefArgument();
				defargIDs.add(id);
				// if id not null, then connect def arg's plug to this block
				if (id != Block.NULL)
					Block.getBlock(id).getPlug()
							.setConnectorBlockID(this.blockID);
			}
			return defargIDs;
		}
		return null;
	}

	// ///////////////////
	// STUB INFORMATION //
	// ///////////////////

	/**
	 * Returns the Stubs of this Block, if it has Stubs; null otherwise
	 *
	 * @return the Stubs of this Block, if it has Stubs; null otherwise
	 */
	public Iterable<BlockStub> getFreshStubs() {

		if (this.linkToStubs && this.hasStubs()) {
			ArrayList<BlockStub> newStubBlocks = new ArrayList<BlockStub>();

			for (String stubGenus : getStubList()) {
				int i = 0;
				// arranged by sakai lab 2011/10/29
				while (i < BlockGenus.getGenusWithName(stubGenus)
						.getFactorySize()) {
					BlockStub newStubBlock = new BlockStub(this.getBlockID(),
							this.getGenusName(), this.getBlockLabel(),
							stubGenus);
					newStubBlock.setFactoryName(BlockGenus.getGenusWithName(
							stubGenus).getFactory(i));
					newStubBlocks.add(newStubBlock);
					i++;
				}
			}
			return newStubBlocks;
		}

		return null;
	}

	/**
	 * 新しくブロックを生成する
	 *
	 * @param 生成するブロックの名前
	 * @return 新しく生成するブロック
	 */
	public Block createFreshStub(String stubGenus) {
		return new BlockStub(this.getBlockID(), this.getGenusName(),
				this.getBlockLabel(), stubGenus);
	}

	// /////////////////////////////////////////////
	// NOTIFICATION OF VIEW FOR IMMEDIATE CHANGES //
	// /////////////////////////////////////////////

	/**
	 * Notifies the "views" (in this case the RenderableBlock) of any changes to
	 * the Block data that would need an immediate visual update. note: to be
	 * used only if there is no other convenient way to update view of an
	 * event/change to the block data from the ui side
	 */
	public void notifyRenderable() {
		RenderableBlock.getRenderableBlock(blockID).repaintBlock();
	}

	// //////////////////////////////////////
	// METHODS FORWARDED FROM BLOCK GENUS //
	// //////////////////////////////////////

	// ria added all genus methods for now, may remove some that other classes
	// don't really need in the future

	/**
	 * Returns the siblings of this genus. If this does not have siblings,
	 * returns an empty list. Each element in the list is the block genus name
	 * of a sibling. FORWARDED FROM BLOCK GENUS
	 *
	 * @return the siblings of this genus.
	 */
	public List<String> getSiblingsList() {
		return getGenus().getSiblingsList();
	}

	/**
	 * Returns true if this genus has siblings; false otherwise. Note: For a
	 * genus to have siblings, its label must be uneditable. An editable label
	 * interferes with the drop down menu widget that blocks with siblings have.
	 * FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if this genus has siblings; false otherwise.
	 */
	public boolean hasSiblings() {
		return getGenus().hasSiblings();
	}

	/**
	 * Returns a list of the stub kinds (or stub genus names) of this; if this
	 * genus does not have any stubs, returns an empty list FORWARDED FROM BLOCK
	 * GENUS
	 *
	 * @return a list of the stub kinds (or stub genus names) of this; if this
	 *         genus does not have any stubs, returns an empty list
	 */
	public Iterable<String> getStubList() {
		if (this.linkToStubs)
			return getGenus().getStubList();
		else
			return new ArrayList<String>();
	}

	/**
	 * Returns true is this genus has stubs (references such as getters,
	 * setters, etc.); false otherwise FORWARDED FROM BLOCK GENUS
	 *
	 * @return true is this genus has stubs (references such as getters,
	 *         setters, etc.); false otherwise
	 */
	public boolean hasStubs() {
		return this.linkToStubs && getGenus().hasStubs();
	}

	/**
	 * Returns true iff any one of the connectors for this genus has default
	 * arguments; false otherwise FORWARDED FROM BLOCK GENUS
	 *
	 * @return true iff any one of the connectors for this genus has default
	 *         arguments; false otherwise
	 */
	public boolean hasDefaultArgs() {
		return getGenus().hasDefaultArgs();
	}

	/**
	 * Returns true if this block is a command block (i.e. forward, say, etc.);
	 * false otherwise FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if this block is a command block (i.e. forward, say, etc.);
	 *         false otherwise
	 */
	public boolean isCommandBlock() {
		return getGenus().isCommandBlock();
	}

	/**
	 * Returns true if this block is a data block a.k.a. a primitive (i.e.
	 * number, string, boolean); false otherwise FORWARDED FROM BLOCK GENUS
	 *
	 * @return Returns true if this block is a data block a.k.a. a primitive
	 *         (i.e. number, string, boolean); false otherwise
	 */
	public boolean isDataBlock() {
		return getGenus().isDataBlock();
	}

	/**
	 * created by sakai lab 2011/11/21
	 *
	 * @return
	 */
	public boolean isBlockForSubstitute() {
		return getGenus().isBlockForSubstitute();
	}

	/**
	 * created by sakai lab 2011/11/29
	 *
	 * @return
	 */
	public boolean isNumberVariableDecBlock() {
		return getGenus().isNumberVariableDecBlock();
	}

	/**
	 * created by sakai lab 2011/11/29
	 *
	 * @return
	 */
	public boolean isStringVariableDecBlock() {
		return getGenus().isStringVariableDecBlock();
	}

	/**
	 * Returns true iff this block is a function block, which takes in an input
	 * and produces an output. (i.e. math blocks, arctan, add to list); false
	 * otherwise. FORWARDED FROM BLOCK GENUS
	 *
	 * @return true iff this block is a function block, which takes in an input
	 *         and produces an output. (i.e. math blocks, arctan, add to list);
	 *         false otherwise.
	 */
	public boolean isFunctionBlock() {
		return getGenus().isFunctionBlock();
	}

	/**
	 * Returns true if this block is a variable block; false otherwise FORWARDED
	 * FROM BLOCK GENUS
	 *
	 * @return true if this block is a variable block; false otherwise
	 */
	public boolean isVariableDeclBlock() {
		return getGenus().isVariableDeclBlock();
	}

	/**
	 * created by sakai lab 2011/10/29 Returns true if this block is a global
	 * variable block; false otherwise FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if this block is a global variable block; false otherwise
	 */
	public boolean isGlobalVariableDeclBlock() {
		return getGenus().isGlobalVariableDeclBlock();
	}

	/**
	 * created by sakai lab 2011/10/29 Returns true if this block is a local
	 * variable block; false otherwise FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if this block is a local variable block; false otherwise
	 */
	public boolean isLocalVariableDeclBlock() {
		return getGenus().isLocalVariableDeclBlock();
	}

	/**
	 * created by sakai lab 2011/10/29 Returns true if this block is a local
	 * variable of object type block; false otherwise FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if this block is a local variable of object type block;
	 *         false otherwise
	 */
	public boolean isObjectTypeVariableDeclBlock() {
		return getGenus().isObjectTypeVariableDeclBlock();
	}

	/**
	 * Returns true if this block is a procedure declaration block; false
	 * otherwise FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if this block is a procedure declaration block; false
	 *         otherwise
	 */
	public boolean isProcedureDeclBlock() {
		return getGenus().isProcedureDeclBlock();
	}

	/**
	 * Returns true if this genus is a declaration block. Declaration blocks
	 * define variables and procedures. FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if this genus is a declaration block; false otherwise
	 */
	public boolean isDeclaration() {
		return getGenus().isDeclaration();
	}

	/**
	 * Returns true if this block is a procedure parameter block; false
	 * otherwise. FORWARDED FROM BLOCK GENUS
	 */
	public boolean isProcedureParamBlock() {
		return getGenus().isProcedureParamBlock();
	}

	// #ohata added
	public boolean isPrivateVariableBlock() {
		return getGenus().isPrivateVariableBlock();
	}

	/**
	 * created by sakai lab 2011/10 Return true if this block is a abstraction
	 * block; false otherwise. FORWARDED FROM BLOCK GENUS
	 */
	public boolean isAbstractionBlock() {
		return getGenus().isAbstractionBlock();
	}

	/**
	 * created by sakai lab 2011/10 Return true if this block is a reference
	 * block; false otherwise. FORWARDED FROM BLOCK GENUS
	 */
	public boolean isCallActionMethodBlock() {
		return getGenus().isCallActionMethodBlock();
	}

	/**
	 * created by sakai lab 2011/10/22 Return true if this block is a reference
	 * block; false otherwise. FORWARDED FROM BLOCK GENUS
	 */
	public boolean isCallGetterMethodBlock() {
		return getGenus().isCallGetterMethodBlock();
	}

	/**
	 * created by sakai lab 2011/10 Return true if this block is a method block;
	 * false otherwise. FORWARDED FROM BLOCK GENUS
	 */
	public boolean isMethodBlock() {
		return getGenus().isMethodBlock();
	}

	/**
	 * Returns true if this block is a list or a list operator (determined by
	 * whether it has at least one list connector of any type); false otherwise.
	 *
	 * @return is determined by whether it has at least one list connector of
	 *         any type. FORWARDED FROM BLOCK GENUS
	 */
	public boolean isListRelated() {
		return getGenus().isListRelated();
	}

	/**
	 * Returns true if this genus has a "before" connector; false otherwise.
	 * FORWARDED FROM BLOCK GENUS
	 *
	 * @return true is this genus has a "before" connector; false otherwise.
	 */
	public boolean hasBeforeConnector() {
		return before != null;
	}

	/**
	 * Returns true if this genus has a "after" connector; false otherwise.
	 * FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if this genus has a "after" connector; false otherwise.
	 */
	public boolean hasAfterConnector() {
		return after != null;
	}

	/**
	 * created by sakai lab 2011/11/25
	 *
	 * @return
	 */
	public String getKind() {
		return getGenus().getKind();
	}

	/**
	 * Returns the initial before connector of this FORWARDED FROM BLOCK GENUS
	 *
	 * @return the initial before connector of this
	 */
	public BlockConnector getInitBefore() {
		return getGenus().getInitBefore();
	}

	/**
	 * Returns the initial after connector of this FORWARDED FROM BLOCK GENUS
	 *
	 * @return the initial after connector of this
	 */
	public BlockConnector getInitAfter() {
		return getGenus().getInitAfter();
	}

	/**
	 * Returns true if the value of this genus is contained within the label of
	 * this; false otherwise FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if the value of this genus is contained within the label of
	 *         this; false otherwise
	 */
	public boolean isLabelValue() {
		return getGenus().isLabelValue();
	}

	/**
	 * Returns true if the label of this is editable; false otherwise FORWARDED
	 * FROM BLOCK GENUS
	 *
	 * @return true if the label of this is editable; false otherwise
	 */
	public boolean isLabelEditable() {
		return getGenus().isLabelEditable();
	}

	/**
	 * Returns true iff page label of this is set by a page FORWARDED FROM BLOCK
	 * GENUS
	 *
	 * @return true iff page label of this is set by a page
	 */
	public boolean isPageLabelSetByPage() {
		return getGenus().isPageLabelSetByPage();
	}

	/**
	 * Returns true if the label of this must be unique; false otherwise
	 * FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if the label of this must be unique; false otherwise
	 */
	public boolean labelMustBeUnique() {
		return getGenus().labelMustBeUnique();
	}

	/**
	 * Returns true if this genus is infix (i.e. math blocks, and/or blocks);
	 * false otherwise FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if this genus is infix (i.e. math blocks, and/or blocks);
	 *         false otherwise
	 */
	public boolean isInfix() {
		return getGenus().isInfix();
	}

	/**
	 * Returns true if this genus has expandable sockets; false otherwise
	 * FORWARDED FROM BLOCK GENUS
	 *
	 * @return true if this genus has expandable sockets; false otherwise
	 */
	public boolean areSocketsExpandable() {
		return getGenus().areSocketsExpandable();
	}

	/**
	 * Returns the name of this genus FORWARDED FROM BLOCK GENUS
	 *
	 * @return the name of this genus
	 */
	public String getGenusName() {
		return genusName;
	}

	/**
	 * Returns the initial label of this FORWARDED FROM BLOCK GENUS
	 *
	 * @return the initial label of this
	 */
	public String getInitialLabel() {
		return getGenus().getInitialLabel();
	}

	/**
	 * Returns the String block label prefix of this FORWARDED FROM BLOCK GENUS
	 *
	 * @return the String block label prefix of this
	 */
	public String getLabelPrefix() {
		return getGenus().getLabelPrefix();
	}

	/**
	 * Returns the String block label prefix of this FORWARDED FROM BLOCK GENUS
	 *
	 * @return the String block label prefix of this
	 */
	public String getLabelSuffix() {
		return getGenus().getLabelSuffix();
	}

	/**
	 * Returns the String block text description of this. Also known as the
	 * block tool tip, or block description. If no descriptions exists, return
	 * null. FORWARDED FROM BLOCK GENUS
	 *
	 * @returns the String block text description of this or NULL if none
	 *          exists.
	 */
	public String getBlockDescription() {
		return getGenus().getBlockDescription();
	}

	/**
	 * Returns the the argument description at index i. If the index is out of
	 * bounds or if no argument description exists for arguemnt at index i ,
	 * return null.
	 *
	 * @returns the String argument descriptions of this or NULL.
	 */
	public String getArgumentDescription(int index) {
		if (index < argumentDescriptions.size() && index >= 0) {
			return argumentDescriptions.get(index);
		}
		return null;
	}

	/**
	 * Returns the Color of this; May return Color.Black if color was
	 * unspecified. FORWARDED FROM BLOCK GENUS
	 *
	 * @return the Color of this; May return Color.Black if color was
	 *         unspecified.
	 */
	public Color getColor() {
		return getGenus().getColor();
	}

	/**
	 * Returns the initial BlockImageIcon mapping of this. Returned Map is
	 * unmodifiable. FORWARDED FROM BLOCK GENUS
	 *
	 * @return the initial and unmodifiable BlockImageIcon mapping of this
	 */
	public Map<ImageLocation, BlockImageIcon> getInitBlockImageMap() {
		return getGenus().getInitBlockImageMap();
	}

	/**
	 * Returns the value of the specified language dependent property
	 * (partially) FORWARDED FROM BLOCK GENUS depending on specified property
	 *
	 * @param property
	 *            the property to look up
	 * @return the value of the specified language dependent property; null if
	 *         property does not exist
	 */
	public String getProperty(String property) {
		// block property overrides genus if that situation exists
		String prop = properties.get(property);
		if (prop != null)
			return prop;
		return getGenus().getProperty(property);
	}

	/**
	 * Returns the initial set of sockets of this FORWARDED FROM BLOCK GENUS
	 *
	 * @return the initial set of sockets of this
	 */
	public Iterable<BlockConnector> getInitSockets() {
		return getGenus().getInitSockets();
	}

	/**
	 * Returns the initial plug connector of this FORWARDED FROM BLOCK GENUS
	 *
	 * @return the initial plug connector of this
	 */
	public BlockConnector getInitPlug() {
		return getGenus().getInitPlug();
	}

	/**
	 * @returns current information about block
	 */
	public String toString() {
		return "Block " + blockID + ": " + label + " with sockets: " + sockets
				+ " and plug: " + plug + " before: " + before + " after: "
				+ after;
	}

	/**
	 * Returns true iff the other Object is an instance of Block and has the
	 * same blockID as this; false otherwise
	 *
	 * @return true iff the other Object is an instance of Block and has the
	 *         same blockID as this; false otherwise
	 */
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Block))
			return false;

		Block otherBlock = (Block) other;
		return (this.blockID == otherBlock.blockID);
	}

	/**
	 * Returns the hash code of this
	 *
	 * @return hash code of this
	 */
	public int hashCode() {
		return blockID.hashCode();
	}

	// //////////////////////
	// SAVING AND LOADING //
	// //////////////////////
	private final String EQ_OPEN_QUOTE = "=\"";
	private final String CLOSE_QUOTE = "\" ";

	/**
	 * Returns an escaped (safe) version of string.
	 */
	public static String escape(String s) {
		return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	/**
	 * Returns the save string of this using additional location information
	 * specified in x and y and comment text . NOTE: in the future will not send
	 * these coordinates and instead will have renderable block insert them.
	 *
	 * @param x
	 * @param y
	 * @return the save string of this
	 */
	public String getSaveString(int x, int y, String commentSaveString,
			boolean isCollapsed) {
		StringBuffer saveString = new StringBuffer();

		saveString.append("<Block ");
		appendAttribute("id", this.blockID.toString(), saveString);
		appendAttribute("genus-name", this.getGenusName(), saveString);
		if (this.hasFocus)
			appendAttribute("has-focus", "yes", saveString);
		saveString.append(">");

		if (!this.label.equals(this.getInitialLabel())
				|| getGenus().getIsLabelEditable()) {
			saveString.append("<Label>");
			saveString.append(escape(label));
			saveString.append("</Label>");
		}

		if (pageLabel != null && !pageLabel.equals("")) {
			saveString.append("<PageLabel>");
			saveString.append(escape(pageLabel));
			saveString.append("</PageLabel>");
		}

		// created by sakai lab 2011/11/20
		if (headerLabel != null && !headerLabel.equals("")) {
			saveString.append("<HeaderLabel>");
			saveString.append(escape(headerLabel));
			saveString.append("</HeaderLabel>");
		}

		if (this.isBad) {
			saveString.append("<CompilerErrorMsg>");
			saveString.append(escape(pageLabel));
			saveString.append("</CompilerErrorMsg>");
		}

		saveString.append("<Location>");
		saveString.append("<X>");
		saveString.append(x);
		saveString.append("</X>");
		saveString.append("<Y>");
		saveString.append(y);
		saveString.append("</Y>");
		saveString.append("</Location>");

		if (isCollapsed) {
			// saveString.append("<Collapsed/>");//2012.10.10 #matsuzawa
			// 何でコメントアウトしていたの？
			saveString.append("<Collapsed/>");
		}

		if (commentSaveString != null) {
			saveString.append(commentSaveString);
		}

		if (this.hasBeforeConnector()
				&& !this.getBeforeBlockID().equals(Block.NULL)) {
			saveString.append("<BeforeBlockId>");
			saveString.append(this.getBeforeBlockID());
			saveString.append("</BeforeBlockId>");
		}

		if (this.hasAfterConnector()
				&& !this.getAfterBlockID().equals(Block.NULL)) {
			saveString.append("<AfterBlockId>");
			saveString.append(this.getAfterBlockID());
			saveString.append("</AfterBlockId>");
		}

		if (plug != null) {
			saveString.append("<Plug>");
			saveString.append(plug.getSaveString("plug"));
			saveString.append("</Plug>");
		}

		if (sockets.size() > 0) {
			saveString.append("<Sockets ");
			appendAttribute("num-sockets", "" + this.getNumSockets(),
					saveString);
			saveString.append(">");
			for (BlockConnector con : getSockets()) {
				saveString.append(con.getSaveString("socket"));
			}
			saveString.append("</Sockets>"); // sockets tricky... because what
												// if
			// one of the sockets is expanded? should the socket keep a
			// reference
			// to their genus socket? and so should the expanded one?
		}

		// save block properties that are not specified within genus
		// i.e. properties that were created/specified during runtime

		if (!properties.isEmpty()) {
			saveString.append("<LangSpecProperties>");
			for (String property : properties.keySet()) {
				saveString.append("<LangSpecProperty ");
				appendAttribute("key", property, saveString);
				appendAttribute("value", properties.get(property), saveString);
				saveString.append("></LangSpecProperty>");
			}
			saveString.append("</LangSpecProperties>");
		}

		saveString.append("</Block>");
		return saveString.toString();
	}

	// #ohata added
	public String getSaveString(int x, int y, String commentSaveString,
			boolean isCollapsed, String comment) {
		StringBuffer saveString = new StringBuffer();

		saveString.append("<Block ");
		appendAttribute("id", this.blockID.toString(), saveString);
		appendAttribute("genus-name", this.getGenusName(), saveString);
		if (this.hasFocus)
			appendAttribute("has-focus", "yes", saveString);
		saveString.append(">");

		if (!this.label.equals(this.getInitialLabel())
				|| getGenus().getIsLabelEditable() || getKind().equals("data")
				|| getKind().equals("command")) {
			saveString.append("<Label>");
			saveString.append(escape(label));
			saveString.append("</Label>");
		}

		if (pageLabel != null && !pageLabel.equals("")) {
			saveString.append("<PageLabel>");
			saveString.append(escape(pageLabel));
			saveString.append("</PageLabel>");
		}

		// created by sakai lab 2011/11/20
		if (headerLabel != null && !headerLabel.equals("")) {
			saveString.append("<HeaderLabel>");
			saveString.append(escape(headerLabel));
			saveString.append("</HeaderLabel>");
		}

		if (javaType != null) {
			saveString.append("<JavaType>");
			saveString.append(escape(javaType));
			saveString.append("</JavaType>");
		}

		if (javaLabel != null) {
			saveString.append("<JavaLabel>");
			saveString.append(escape(javaLabel));
			saveString.append("</JavaLabel>");
		}

		if (this.isBad) {
			saveString.append("<CompilerErrorMsg>");
			saveString.append(escape(pageLabel));
			saveString.append("</CompilerErrorMsg>");
		}
		if (comment != null) {
			saveString.append("<LineComment>");
			saveString.append(comment);
			saveString.append("</LineComment>");
		}

		if (parameterizedTypes != null) {
			saveString.append("<ParameterizedType>");
			for (String type : parameterizedTypes) {
				saveString.append("<Type>" + type + "</Type>");

			}
			saveString.append("</ParameterizedType>");
		}

		saveString.append("<Location>");
		saveString.append("<X>");
		saveString.append(x);
		saveString.append("</X>");
		saveString.append("<Y>");
		saveString.append(y);
		saveString.append("</Y>");
		saveString.append("</Location>");

		if (isCollapsed) {
			// saveString.append("<Collapsed/>");//2012.10.10 #matsuzawa
			// 何でコメントアウトしていたの？
			saveString.append("<Collapsed/>");
		}

		if (commentSaveString != null) {
			saveString.append(commentSaveString);
		}

		if (this.hasBeforeConnector()
				&& !this.getBeforeBlockID().equals(Block.NULL)) {
			saveString.append("<BeforeBlockId>");
			saveString.append(this.getBeforeBlockID());
			saveString.append("</BeforeBlockId>");
		}

		if (this.hasAfterConnector()
				&& !this.getAfterBlockID().equals(Block.NULL)) {
			saveString.append("<AfterBlockId>");
			saveString.append(this.getAfterBlockID());
			saveString.append("</AfterBlockId>");
		}

		if (plug != null) {
			saveString.append("<Plug>");
			saveString.append(plug.getSaveString("plug"));
			saveString.append("</Plug>");
		}

		if (sockets.size() > 0) {
			saveString.append("<Sockets ");
			appendAttribute("num-sockets", "" + this.getNumSockets(),
					saveString);
			saveString.append(">");
			for (BlockConnector con : getSockets()) {
				saveString.append(con.getSaveString("socket"));
			}
			saveString.append("</Sockets>"); // sockets tricky... because what
												// if
			// one of the sockets is expanded? should the socket keep a
			// reference
			// to their genus socket? and so should the expanded one?
		}

		// save block properties that are not specified within genus
		// i.e. properties that were created/specified during runtime

		if (!properties.isEmpty()) {
			saveString.append("<LangSpecProperties>");
			for (String property : properties.keySet()) {
				saveString.append("<LangSpecProperty ");
				appendAttribute("key", property, saveString);
				appendAttribute("value", properties.get(property), saveString);
				saveString.append("></LangSpecProperty>");
			}
			saveString.append("</LangSpecProperties>");
		}

		saveString.append("</Block>");
		return saveString.toString();
	}

	// may put this in a separate XML writing utility class
	private void appendAttribute(String att, String value, StringBuffer buf) {
		buf.append(att);
		buf.append(EQ_OPEN_QUOTE);
		buf.append(escape(value));
		buf.append(CLOSE_QUOTE);
	}

	/**
	 * Loads Block information from the specified node and return a Block
	 * instance with the loaded information
	 *
	 * @param node
	 *            Node cantaining desired information
	 * @return Block instance containing loaded information
	 */
	public static Block loadBlockFrom(Node node, HashMap<Long, Long> idMapping) {
		Block block = null;
		Long id = null;
		String genusName = null;
		String label = null;
//		String headerLabel = null;
		int lineNumber = -1;
		Long parentID = NULL;
		String pagelabel = null;
		String badMsg = null;
		Long beforeID = null;
		Long afterID = null;
		ArrayList<String> parameterizedTypes = new ArrayList<String>();
		BlockConnector plug = null;
		ArrayList<BlockConnector> sockets = new ArrayList<BlockConnector>();
		HashMap<String, String> blockLangProperties = null;
		String javaType = null;
		String javaLabel = null;
		// int numSockets;
		boolean hasFocus = false;

		// stub information if this node contains a stub
		boolean isStubBlock = false;
		String stubParentName = null;
		String stubParentGenus = null;
		Pattern attrExtractor = Pattern.compile("\"(.*)\"");
		Matcher nameMatcher;

		if (node.getNodeName().equals("BlockStub")) {
			isStubBlock = true;
			Node blockNode = null;
			NodeList stubChildren = node.getChildNodes();
			for (int j = 0; j < stubChildren.getLength(); j++) {
				Node infoNode = stubChildren.item(j);
				if (infoNode.getNodeName().equals("StubParentName"))
					stubParentName = infoNode.getTextContent();
				else if (infoNode.getNodeName().equals("StubParentGenus"))
					stubParentGenus = infoNode.getTextContent();
				else if (infoNode.getNodeName().equals("Block"))
					blockNode = infoNode;
			}
			node = blockNode;
		}

		if (node.getNodeName().equals("Block")) {
			// load attributes
			nameMatcher = attrExtractor.matcher(node.getAttributes()
					.getNamedItem("id").toString());
			if (nameMatcher.find()) // will be true
				id = translateLong(Long.parseLong(nameMatcher.group(1)),
						idMapping);
			nameMatcher = attrExtractor.matcher(node.getAttributes()
					.getNamedItem("genus-name").toString());
			if (nameMatcher.find()) // will be true
				genusName = nameMatcher.group(1);
			// load optional items
			Node opt_item = node.getAttributes().getNamedItem("has-focus");
			if (opt_item != null) {
				nameMatcher = attrExtractor.matcher(opt_item.toString());
				if (nameMatcher.find()) // will be true
					hasFocus = nameMatcher.group(1).equals("yes") ? true
							: false;
			}

			// load elements
			NodeList children = node.getChildNodes();
			Node child;
			for (int i = 0; i < children.getLength(); i++) {
				child = children.item(i);
				if (child.getNodeName().equals("Label")) {
					label = child.getTextContent();
				} else if (child.getNodeName().equals("HeaderLabel")) {
//					headerLabel = child.getTextContent();
				} else if (child.getNodeName().equals("LineNumber")) {
					lineNumber = Integer.parseInt(child.getTextContent());
				} else if (child.getNodeName().equals("JavaType")) {
					javaType = child.getTextContent();
				} else if (child.getNodeName().equals("JavaLabel")) {
					javaLabel = child.getTextContent();
				} else if (child.getNodeName().equals("ParentBlock")) {
					parentID = Long.parseLong(child.getTextContent());
				} else if (child.getNodeName().equals("ReturnType")) {
					BlockStub.putParentToPlugType(label + genusName,
							child.getTextContent());
				} else if (child.getNodeName().equals("PageLabel")) {
					pagelabel = child.getTextContent();
				} else if (child.getNodeName().equals("CompilerErrorMsg")) {
					badMsg = child.getTextContent();
				} else if (child.getNodeName().equals("ParameterizedType")) {
					NodeList types = child.getChildNodes();
					for (int j = 0; j < types.getLength(); j++) {
						Node type = types.item(j);
						if (type.getNodeName().equals("Type")) {
							parameterizedTypes.add(type.getTextContent());
						}
					}
				} else if (child.getNodeName().equals("BeforeBlockId")) {
					beforeID = translateLong(
							Long.parseLong(child.getTextContent()), idMapping);
				} else if (child.getNodeName().equals("AfterBlockId")) {
					afterID = translateLong(
							Long.parseLong(child.getTextContent()), idMapping);
				} else if (child.getNodeName().equals("Plug")) {
					NodeList plugs = child.getChildNodes(); // there should only
															// one child
					Node plugNode;
					for (int j = 0; j < plugs.getLength(); j++) {
						plugNode = plugs.item(j);
						if (plugNode.getNodeName().equals("BlockConnector")) {
							String socketLabel = "";
							// nameMatcher =
							// attrExtractor.matcher(node.getAttributes()
							// .getNamedItem("label").toString());
							// if (nameMatcher.find()){ // will be true
							// socketLabel = nameMatcher.group(1);
							// }
							plug = BlockConnector.loadBlockConnector(plugNode,
									idMapping, socketLabel);
						}
					}
				} else if (child.getNodeName().equals("Sockets")) {
					NodeList socketNodes = child.getChildNodes();
					Node socketNode;
					int socketNumber = 0;
					for (int k = 0; k < socketNodes.getLength(); k++) {
						socketNode = socketNodes.item(k);
						if (socketNode.getNodeName().equals("BlockConnector")) {
							BlockGenus blockGenus = BlockGenus
									.getGenusWithName(genusName);
							// #matsuzawa
							// socketLabelがないとnullpointer良く出るので場所が特定できるよう処置
							if (blockGenus == null) {
								throw new RuntimeException(
										"no blockGenus for "
												+ genusName
												+ " attr: "
												+ toStringAttributes(child
														.getAttributes()));
							}
							String socketLabel = getSocketLabel(blockGenus, socketNode, socketNumber);

							sockets.add(BlockConnector.loadBlockConnector(socketNode, idMapping, socketLabel));

							socketNumber++;
						}
					}
				} else if (child.getNodeName().equals("LangSpecProperties")) {
					blockLangProperties = new HashMap<String, String>();
					NodeList propertyNodes = child.getChildNodes();
					Node propertyNode;
					String key = null;
					String value = null;
					for (int m = 0; m < propertyNodes.getLength(); m++) {
						propertyNode = propertyNodes.item(m);
						if (propertyNode.getNodeName().equals(
								"LangSpecProperty")) {
							nameMatcher = attrExtractor.matcher(propertyNode
									.getAttributes().getNamedItem("key")
									.toString());
							if (nameMatcher.find()) // will be true
								key = nameMatcher.group(1);
							opt_item = propertyNode.getAttributes()
									.getNamedItem("value");
							if (opt_item != null) {
								nameMatcher = attrExtractor.matcher(opt_item
										.toString());
								if (nameMatcher.find()) // will be true
									value = nameMatcher.group(1);
							} else {
								value = propertyNode.getTextContent();
							}
							if (key != null && value != null) {
								blockLangProperties.put(key, value);
								key = null;
								value = null;
							}
						}
					}
				}
			}

			assert genusName != null && id != null : "Block did not contain required info id: "
					+ id + " genus: " + genusName;
			// create block or block stub instance
			if (!isStubBlock) {
				try {
					if (label == null) {

						block = new Block(id, genusName, BlockGenus
								.getGenusWithName(genusName).getInitialLabel(),
								true);
					} else {
						block = new Block(id, genusName, label, true);
					}
				} catch (NullPointerException ex) {
					throw new RuntimeException("genusName=" + genusName
							+ ", label=" + label, ex);
				}
			} else {
				assert label != null : "Loading a block stub, but has a null label!";
				block = new BlockStub(id, genusName, label, stubParentName,
						stubParentGenus);
			}

			if (parameterizedTypes.size() > 0) {
				block.parameterizedTypes = parameterizedTypes;
			}

			if (plug != null) {
				// Some callers can change before/after/plug types. We have
				// to synchronize so that we never have both.
				assert beforeID == null && afterID == null;
				block.plug = plug;
				block.removeBeforeAndAfter();
			}

			if (sockets.size() > 0)
				block.sockets = sockets;

			if (beforeID != null)
				block.before.setConnectorBlockID(beforeID);
			if (afterID != null)
				block.after.setConnectorBlockID(afterID);
			if (pagelabel != null)
				block.pageLabel = pagelabel;
			if (badMsg != null) {
				block.isBad = true;
				block.badMsg = badMsg;
			}
			block.hasFocus = hasFocus;

			// load language dependent properties
			if (blockLangProperties != null && !blockLangProperties.isEmpty()) {
				block.properties = blockLangProperties;
			}

			if (javaType != null) {
				block.javaType = javaType;
			}

			if (javaLabel != null) {
				block.javaLabel = javaLabel;
			}

			block.setLineNumber(lineNumber);
			block.setParentBlockID(parentID);

			return block;
		}

		return null;
	}

	public static String getSocketLabel(BlockGenus blockGenus, Node node, int socketNumber){
		if("callerprocedure".equals(blockGenus.getGenusName())){
			return node.getAttributes().getNamedItem("label").getNodeValue();
		}else{
			return blockGenus.getSocketsLabel(socketNumber);
		}
	}

	private static String toStringAttributes(NamedNodeMap attributes) {
		int len = attributes.getLength();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < len; i++) {
			buf.append(attributes.item(i).toString());
		}
		return buf.toString();
	}

	public static Long translateLong(Long input, HashMap<Long, Long> mapping) {
		if (mapping == null)
			return input;
		if (mapping.containsKey(input))
			return mapping.get(input);
		Long newID = Long.valueOf(NEXT_ID++);
		mapping.put(input, newID);
		return newID;
	}

	// Temp code so compiler can see all blocks
	// remove when a more appropriate method exists for the workspace
	public static Iterable<Block> getAllBlocks() {
		return ALL_BLOCKS.values();
	}

	/***********************************
	 * State Saving Stuff for Undo/Redo *
	 ***********************************/

	private class BlockState {
		// basic information
		public String label;
		public String pageLabel;
		public boolean isBad;
		public String badMsg;
		public boolean hasFocus;
		public HashMap<String, String> properties;

		// block connection and genus information
		public String genusName;

		// Block Connector Stuff
		public int numberOfSockets;
		public ArrayList<Object> sockets;
		public Object plug;
		public Object before;
		public Object after;
	}

	public Object getState() {
		BlockState state = new BlockState();

		// Get Basic Stuff
		state.label = this.label;
		state.pageLabel = this.getPageLabel();
		state.isBad = this.isBad();
		state.badMsg = this.getBadMsg();
		state.hasFocus = this.hasFocus();

		// Get Properties
		state.properties = new HashMap<String, String>();
		for (String name : this.properties.keySet()) {
			state.properties.put(name, this.getProperty(name));
		}

		// Get Genus and Connection Stuff
		state.genusName = this.getGenusName();

		// this needs evaluation since I'm not sure if there are knock on
		// effects
		// of setting this memory directly
		if (this.plug != null) {
			state.plug = this.plug.getState();
		} else {
			state.plug = null;
		}

		if (this.before != null) {
			state.before = this.before.getState();
		} else {
			state.before = null;
		}

		if (this.after != null) {
			state.after = this.after.getState();
		} else {
			state.after = null;
		}

		// Get the Sockets
		state.numberOfSockets = this.getNumSockets();
		state.sockets = new ArrayList<Object>();

		for (BlockConnector socket : this.getSockets()) {
			state.sockets.add(socket.getState());
		}

		return state;
	}

	public void loadState(Object memento) {
		if (memento instanceof BlockState) {
			BlockState state = (BlockState) memento;

			// Set the basic properties
			this.setPageLabel(state.pageLabel);
			this.setBlockLabel(state.label);
			// this.setDataTypeLabel(state.)
			this.setBad(state.isBad);
			this.setBadMsg(state.badMsg);
			this.setFocus(state.hasFocus);

			// Properties
			for (String name : state.properties.keySet()) {
				this.setProperty(name, state.properties.get(name));
			}

			// Genus stuff, to avoid more work than necessary
			if (this.genusName != state.genusName) {
				this.changeGenusTo(state.genusName);
			}

			if (state.plug == null) {
				this.plug = null;
			} else {
				this.plug = BlockConnector.instantiateFromState(state.plug);
			}

			if (state.before == null) {
				this.before = null;
			} else {
				this.before = BlockConnector.instantiateFromState(state.before);
			}

			if (state.after == null) {
				this.after = null;
			} else {
				this.after = BlockConnector.instantiateFromState(state.after);
			}

			for (int i = 0; i < state.numberOfSockets; i++) {
				if (i >= this.getNumSockets()) {
					this.sockets.add(BlockConnector
							.instantiateFromState(state.sockets.get(i)));
				} else {
					this.sockets.get(i).loadState(state.sockets.get(i));
				}
			}

			for (int i = state.numberOfSockets; i < this.getNumSockets(); i++) {
				this.removeSocket(i);
			}
		}
	}

	/**
	 * @param lineNumber
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * @return lineNumber
	 */
	public int getLineNumber() {
		return this.lineNumber;
	}

	public void setParentBlockID(Long parent) {
		this.parentID = parent;
	}

	public Long getParentBlockID() {
		return parentID;
	}

	public Long getMethodDefinidionBlockID() {
		if(this.getGenusName().equals("procedure")){
			return this.blockID;
		}
		return parentID;
	}
}
