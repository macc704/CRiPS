package obpro.turtle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import common.resource.CResourceFinder;

/*
 * ImageTurtle.java
 * Created on 2011/12/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */

/**
 * Class ImageTurtle.
 * 
 * @author macchan
 * 
 */
public class ImageTurtle extends Turtle {

	public static String DEFAULT_FONT = "MS Gothic";

	/************************************************
	 * 変数
	 ************************************************/

	private BufferedImage image = null;

	private AffineTransformOp transformOp = null;

	/************************************************
	 * コンストラクタ
	 ************************************************/

	public ImageTurtle() {
		setImage(createTextImage("名称未設定"));
	}

	public ImageTurtle(String filename) {
		image(filename);
	}

	/************************************************
	 * getter
	 ************************************************/

	public BufferedImage image() {
		return image;
	}

	public AffineTransformOp transformOp() {
		return transformOp;
	}

	/************************************************
	 * Image関連
	 ************************************************/

	public void image(String filename) {
		setImage(loadBufferedImage(filename));
	}

	protected void setImage(BufferedImage image) {
		this.image = image;
		super.initializeLooks();
	}

	private BufferedImage loadBufferedImage(String filename) {
		Image image = loadImage(filename);
		if (image != null) {
			return createBufferedImage(image);
		} else {
			return createTextImage(filename);
		}
	}

	private Image loadImage(String filename) {
		try {
			URL url = CResourceFinder.getResource(filename);
			if (applet == null && url == null) {
				print("エラー: 画像が見つかりません " + filename);
				return null;
			}

			MediaTracker mt = new MediaTracker(window);
			Image image = null;
			if (applet == null) {
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				image = toolkit.getImage(url);
			} else {
				image = applet.getImage(applet.getDocumentBase(), filename);
				System.out.println(applet.getDocumentBase());
			}
			mt.addImage(image, 0);
			mt.waitForAll();
			if (mt.isErrorID(0)) {
				if (new File(filename).exists()) {
					print("エラー: 画像が読み込めない形式です " + filename);
				}
				return null;
			}
			return image;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private BufferedImage createBufferedImage(Image image) {
		int width = image.getWidth(null);
		int height = image.getHeight(null);

		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = bufferedImage.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bufferedImage;
	}

	private BufferedImage createTextImage(String text) {
		int width = 100;
		int height = 100;

		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = bufferedImage.getGraphics();
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		g.drawString(text, 20, height / 2);
		g.dispose();
		return bufferedImage;
	}

	/************************************************
	 * start
	 ************************************************/

	@Override
	public void start() {
		if (image != null) {
			int w = image.getWidth();
			int h = image.getHeight();
			for (int i = 0; i < 2; i++) {
				rt(90);
				fd(w);
				rt(90);
				fd(h);
			}
		}
	}

	/************************************************
	 * doTransform の オーバーライド
	 ************************************************/

	protected synchronized void doTransform() {
		super.doTransform();

		if (image != null) {
			AffineTransform transform = new AffineTransform();
			double centerX = image.getWidth() / 2;
			double centerY = image.getHeight() / 2;
			double rotatedCenterX = rotatedWidth() / 2;
			double rotatedCenterY = rotatedHeight() / 2;

			transform.translate(rotatedCenterX, rotatedCenterY);
			transform.rotate(theta());
			transform.scale(scaleX(), scaleY());
			transform.translate(-centerX, -centerY);

			this.transformOp = new AffineTransformOp(transform,
					AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		}
	}

	// temp for Turtle
	// 上と重複コード
	protected AffineTransformOp createTransformOp(double theta, double width,
			double height, double rotatedWidth, double rotatedHeight) {

		AffineTransform transform = new AffineTransform();
		double imageWidth = image.getWidth();
		double imageHeight = image.getHeight();
		double centerX = imageWidth / 2;
		double centerY = imageHeight / 2;
		double scaleX = width / imageWidth;
		double scaleY = height / imageHeight;
		double rotatedCenterX = rotatedWidth / 2;
		double rotatedCenterY = rotatedHeight / 2;

		transform.translate(rotatedCenterX, rotatedCenterY);
		transform.rotate(theta);
		transform.scale(scaleX, scaleY);
		transform.translate(-centerX, -centerY);

		AffineTransformOp transformOp = new AffineTransformOp(transform,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		return transformOp;
	}

	/************************************************
	 * paint
	 ************************************************/

	public void draw(Graphics2D g) {

		if (looks() == this) {
			locus().paint(g);
			if (image != null) {
				g.drawImage(image, transformOp, (int) rotatedMinX(),
						(int) rotatedMinY());
			}
			// shape().paint(g);
			return;
		} else {
			super.draw(g);
			return;
		}

	}

}
