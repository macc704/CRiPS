package a.slab.blockeditor.extent;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import renderable.RenderableBlock;
import codeblocks.BlockConnector;
import codeblocks.BlockConnectorShape;
import codeblocks.BlockShape;
import codeblocks.rendering.BlockShapeUtil;

/**
 * 
 * created by sakai lab 2011/11/19
 * @author yasui
 *
 */
public class SAbstractionBlockShape extends BlockShape {

	/** height when block is collapsed */
	private final int collapsedBlockHeight = 40;
	/** spacer on top of bottom sockets to give continuous top for abstraction block*/
	public static final float ABSTRACTION_SOCKET_UPPER_SPACE = 6.5f;

	public SAbstractionBlockShape(RenderableBlock rb) {
		super(rb);
	}

	@Override
	protected void setupDimensions() {
		super.setupDimensions();
		botLeftCorner.setLocation(botLeftCorner.getX(), botLeftCorner.getY() - ABSTRACTION_SOCKET_UPPER_SPACE);
		botRightCorner.setLocation(botRightCorner.getX(), botRightCorner.getY() - ABSTRACTION_SOCKET_UPPER_SPACE);
	}

	@Override
	protected int getTotalHeightOfSockets() {
		int heightSum = 0;

		for (BlockConnector socket : block.getSockets()) {

			Dimension socketDimension = rb.getSocketSpaceDimension(socket);

			// if the socket has been assigned a dimension...
			if (socketDimension != null) {
				heightSum += socketDimension.height;

				// if command, then add other command parts
				if (BlockConnectorShape.isCommandConnector(socket)) {
					heightSum += BlockConnectorShape.ABSTRACTION_INPUT_BAR_HEIGHT + 2 * CORNER_RADIUS;
				}
				continue;
			}

			// else use default dimension
			heightSum += BlockConnectorShape.DEFAULT_COMMAND_INPUT_HEIGHT
					+ BlockConnectorShape.ABSTRACTION_INPUT_BAR_HEIGHT + 2 * CORNER_RADIUS;
			// else normal data connector, so add height

		}

		heightSum += BlockConnectorShape.ABSTRACTION_BLOCK_SOCKET_HEIGHT;
		return heightSum;
	}

	@Override
	protected int determineBlockHeight() {
		if (rb.isCollapsed()) {
			return collapsedBlockHeight;
		}
		return super.determineBlockHeight();
	}

	@Override
	protected void makeTopSide() {

		// starting point of the block
		setEndPoint(gpTop, topLeftCorner, botLeftCorner, true);

		// curve up and right
		BlockShapeUtil.cornerTo(gpTop, topLeftCorner, topRightCorner, blockCornerRadius);

		// params: path, distance to center of block, going right
		// Old center-aligned ports
		// Trying left-aligned ports for now
		Point2D p = BCS.addControlConnectorShape(gpTop, (float) COMMAND_PORT_OFFSET + blockCornerRadius, true);

		rb.updateSocketPoint(block.getBeforeConnector(), p);

		// curve down
		BlockShapeUtil.cornerTo(gpTop, topRightCorner, botRightCorner, blockCornerRadius);

		// end topside
		setEndPoint(gpTop, topRightCorner, botRightCorner, false);
	}

	@Override
	protected void makeRightSide() {
		// move to the end of the TopSide
		setEndPoint(gpRight, topRightCorner, botRightCorner, true);
		// if collapsed
		if (rb.isCollapsed()) {
			setEndPoint(gpRight, botRightCorner, topRightCorner, false);
			return;
		}
		// // ADD SOCKETS ////
		// for each socket in the iterator
		BlockConnector curSocket = block.getSocketAt(0);
		// add the socket shape to the gpRight
		float defaultHeight = BlockConnectorShape.DEFAULT_COMMAND_INPUT_HEIGHT;
		int spacerHeight = getSocketSpacerHeight(curSocket, defaultHeight);
		// draw the command socket bar and such
		Point2D p = BCS.addAbstractionBlockSocket(gpRight, spacerHeight);
		rb.updateSocketPoint(curSocket, p);
		// line to the bottom right
		setEndPoint(gpRight, botRightCorner, topRightCorner, false);
	}

	@Override
	protected void makeBottomSide() {

		// start bottom-right
		setEndPoint(gpBottom, botLeftCorner, topLeftCorner, true);

		// curve down and right
		BlockShapeUtil.cornerTo(gpBottom, botLeftCorner, botRightCorner, blockCornerRadius);

		// CONTROL CONNECTOR
		Point2D p = BCS.addControlConnectorShape(gpBottom, (float) COMMAND_PORT_OFFSET + blockCornerRadius, true);
		rb.updateSocketPoint(block.getAfterConnector(), p);

		// curve right and up
		BlockShapeUtil.cornerTo(gpBottom, botRightCorner, topRightCorner, blockCornerRadius);

		// end bottom
		setEndPoint(gpBottom, botRightCorner, topRightCorner, false);

	}

}
