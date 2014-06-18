package obpro.turtle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;

/**
 * 下位互換性のためにあるHolderTurtleです．
 */
@Deprecated
public class HolderTurtle extends ImageTurtle {

	private static final int MARGIN = 5;

	private static final int FONTSIZE = 12;
	private String name;

	private LinkedList<ImageTurtle> children = new LinkedList<ImageTurtle>();
	private int cursor = 0;

	public HolderTurtle() {
		this(null);
	}

	public HolderTurtle(String name) {
		this.name = name;
		resetImage();
	}

	/***************************************************************************
	 * Cursor類
	 **************************************************************************/

	public int カーソル位置() {
		return cursor + 1;
	}

	public void カーソル位置を変える(int newCursor) {
		if (newCursor >= 1 && newCursor <= children.size()) {// 正常
			this.cursor = newCursor - 1;
		} else if (children.size() != 0) {// 範囲外
			this.cursor = (newCursor % children.size()) - 1;
		} else {// 0の場合
			this.cursor = 0;
		}
		resetImage();
	}

	public void カーソルを進める() {
		カーソル位置を変える(cursor + 2);
	}

	public void カーソルを戻す() {
		カーソル位置を変える(cursor);
	}

	public ImageTurtle カーソル位置にあるもの() {
		if (children.size() <= 0) {
			return NullTurtle.NULL_TURTLE;
		}
		return children.get(cursor);
	}

	public ImageTurtle 以下のカーソル位置にあるもの(int i) {
		return children.get(i - 1);
	}

	public int カーソル位置にあるものの数値() {
		try {
			return Integer.parseInt(カーソル位置にあるものの内容());
		} catch (Exception ex) {
			return -1;
		}
	}

	public String カーソル位置にあるものの内容() {
		try {
			ImageTurtle object = children.get(cursor);
			if (object instanceof CardTurtle) {
				return ((CardTurtle) object).text();
			} else {
				return ((TextTurtle) object).text();
			}
		} catch (Exception ex) {
			return "NULL";
		}
	}

	/***************************************************************************
	 * 追加と削除
	 **************************************************************************/

	public void 最後に追加する(ImageTurtle turtle) {
		parentCheck(turtle);
		children.addLast(turtle);
		doPostAddProcess(turtle);
	}

	public void 先頭に追加する(ImageTurtle turtle) {
		parentCheck(turtle);
		children.addFirst(turtle);
		doPostAddProcess(turtle);
	}

	public void カーソル位置に追加する(ImageTurtle turtle) {
		parentCheck(turtle);
		children.add(this.cursor, turtle);
		doPostAddProcess(turtle);
	}

	private void doPostAddProcess(ImageTurtle turtle) {
		turtle.hide();
		turtle.parentHolder = this;
		resetImage();
	}

	private void parentCheck(ImageTurtle turtle) {
		if (turtle.parent != null) {
			turtle.parentHolder.削除する(turtle);
		}
	}

	public void 入っている全てのものを以下の入れ物に移動する(HolderTurtle to) {
		int objectCount = this.入っているものの個数();
		for (int i = 0; i < objectCount; i++) {
			to.最後に追加する(this.カーソル位置にあるもの());
		}
	}

	public void 入っているものを全て捨てる() {
		for (ImageTurtle child : children) {
			child.die();
		}
		children.clear();
		resetImage();
	}

	public void カーソル位置のものを削除する() {
		削除する(カーソル位置にあるもの());
	}

	public void 削除する(ImageTurtle turtle) {
		removeObjectInternal(turtle);
	}

	private ImageTurtle removeObjectInternal(ImageTurtle turtle) {
		if (turtle == null || !children.contains(turtle)) {
			System.err.println("削除できません");
			return null;
		}

		children.remove(turtle);
		turtle.parent = null;
		turtle.show();
		resetImage();
		return turtle;
	}

	/***************************************************************************
	 * その他Public
	 **************************************************************************/

	public int 入っているものの個数() {
		return children.size();
	}

	public void かきまぜる() {
		Collections.shuffle(children);
		resetImage();
	}

	/***************************************************************************
	 * デバッグ用
	 * 
	 * @deprecated
	 **************************************************************************/
	void printChildren() {
		int objectCount = this.入っているものの個数();
		for (int i = 0; i < objectCount; i++) {
			カーソル位置を変える(i + 1);
			System.out.print(this.カーソル位置にあるものの数値() + ",");
		}
		カーソル位置を変える(1);
	}

	/***************************************************************************
	 * 描画Strategy
	 **************************************************************************/

	private synchronized void resetImage() {

		// save original location
		double orgX = x() - (width() / 2d);
		double orgY = y() - (height() / 2d);
		System.out.println(this + "(" + orgX + "," + orgY + ")");

		// calculate size
		int width;
		int height;
		if (children.size() > 0) {
			width = MARGIN;
			height = 0;
			for (ImageTurtle child : children) {
				width += (int) child.image().getWidth() + MARGIN;
				height = height > (int) child.height() ? height : (int) child
						.height();
			}
			height += MARGIN * 2;
		} else {
			// Default Size
			width = 60;
			height = 30;
		}
		if (name != null) {// 名前の分を足す
			height += FONTSIZE + MARGIN;
			int nameW = (int) (name.length() * (FONTSIZE * 6d / 5d));
			if (width < nameW) {
				width = nameW;
			}
		}

		// 準備
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) image.getGraphics();

		// 枠線
		g.setColor(color());
		g.drawRect(1, 1, width - 2, height - 2);

		int x = MARGIN;
		int y = MARGIN;

		// 名前
		if (name != null) {
			y += FONTSIZE;
			g.drawString(name, MARGIN, y);
			y += MARGIN;
		}

		// children
		for (int i = 0; i < children.size(); i++) {
			ImageTurtle child = children.get(i);
			g.drawImage(child.image(), x, y, null);

			// カーソル
			if (cursor == i) {
				g.setColor(Color.red);
				Stroke original = g.getStroke();
				g.setStroke(new BasicStroke(3));
				g.drawRect(x, y, child.image().getWidth(), child.image()
						.getHeight());
				g.setStroke(original);
				g.setColor(color());
			}

			x += child.image().getWidth() + MARGIN;
		}

		g.dispose();

		// set new image
		super.setImage(image);

		// set to original x, y
		int newX = (int) (orgX + (width / 2d) + 0.1);
		int newY = (int) (orgY + (height / 2d) + 0.1);
		warp(newX, newY);
	}
}