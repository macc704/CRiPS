
package obpro.gamefw;

import obpro.gui.BCanvas;
import obpro.gui.BWindow;

/*
 * AbstractGUIAnimationApplication.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */

/**
 * GUIゲーム　フレームワーク 
 * 抽象アプリケーションクラス（スーパークラス）
 * 
 * @author macchan
 * @version 1.0
 */
public class AbstractGUIAnimationApplication {

	private GUIAnimationApplicationEngine engine;

	/**
	 * アプリケーションを駆動するエンジンを取得する(final:オーバーライド禁止)
	 */
	public final GUIAnimationApplicationEngine getEngine() {
		return this.engine;
	}

	/**
	 * アプリケーションを駆動するエンジンを設定する(final:オーバーライド禁止)
	 */
	public final void setEngine(GUIAnimationApplicationEngine engine) {
		this.engine = engine;
	}

	/**
	 * ウインドウの初期化をする
	 */
	public void initializeWindow(BWindow window) {
		window.setLocation(100, 100);
		window.setSize(640, 480);
	}

	/**
	 * アニメーション開始の準備をする
	 */
	public void prepareAnimationStart() {
	}

	/**
	 * 1ステップの処理をする
	 */
	public void processOneStepForApplication(BCanvas canvas) {
	}

	/**
	 * 描画する
	 */
	public void drawForApplication(BCanvas canvas) {
	}

}