package obpro.gamefw;
/**
 * ゲームのキャラクターを表現するクラス
 */
public class GameCharacter extends AnimationElement {

	//属性
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;

	/**
	 * コンストラクタ
	 */
	public GameCharacter(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * 高さを取得する
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * 幅を取得する
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * X座標を取得する
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Y座標を取得する
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * 位置を再設定する
	 */
	public void setLocation(int newX, int newY) {
		x = newX;
		y = newY;
	}

	/**
	 * 動かす
	 */
	public void move(int moveX, int moveY) {
		x = x + moveX;
		y = y + moveY;
	}

	/**
	 * 他のキャラクターとの衝突判定をする
	 */
	public boolean intersects(GameCharacter another) {
		int self_leftX = this.getX();
		int self_rightX = this.getX() + this.getWidth();
		int another_leftX = another.getX();
		int another_rightX = another.getX() + another.getWidth();
		int self_topY = this.getY();
		int self_bottomY = this.getY() + this.getHeight();
		int another_topY = another.getY();
		int another_bottomY = another.getY() + another.getHeight();

		return (another_leftX < self_rightX && another_rightX > self_leftX
				&& another_topY < self_bottomY && another_bottomY > self_topY);
	}
}