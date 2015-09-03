package obpro.gamefw;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import obpro.common.BReflection;
import obpro.gui.BCanvas;
import obpro.gui.BWindow;

/*
 * GUIAnimationApplicationEngine.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */

/**
 * GUIゲーム　フレームワーク 
 * 実行エンジン
 * 
 * @author macchan
 * @version 1.0
 */
public class GUIAnimationApplicationEngine {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("起動には引数が必要です");
			return;
		}

		//アプリケーションを生成し，エンジンを起動する
		AbstractGUIAnimationApplication application = (AbstractGUIAnimationApplication) BReflection
				.createInstanceByName(args[0]);
		GUIAnimationApplicationEngine engine = new GUIAnimationApplicationEngine(
				application);
		engine.run();
	}

	//実行するアプリケーション
	private AbstractGUIAnimationApplication application;

	//ウインドウ
	private BWindow window;

	//アニメーションオブジェクトの集合
	private List elements = new ArrayList();

	//タイマー関連
	private Timer timer = new Timer();

	private double stepInterval = 0.03d;

	private Object lockObject = "locker";

	/**
	 * コンストラクタ
	 */
	public GUIAnimationApplicationEngine(
			AbstractGUIAnimationApplication application) {
		this.application = application;
		application.setEngine(this);
	}

	//アプリケーションを実行する
	private void run() {
		openWindow();
		application.prepareAnimationStart();//アニメーションの開始準備をする(オブジェクトなどを初期化する)
		restartTimer();
		doAnimation();
	}

	//ウインドウを開く
	private void openWindow() {
		window = new BWindow();
		application.initializeWindow(window);
		window.show();
	}

	//アニメーションする
	private void doAnimation() {
		//キャンバスを取得する
		BCanvas canvas = window.getCanvas();

		//アニメーションする
		while (true) {
			{//１コマの処理を行う
				//ゲーム全体としての1コマの処理を行う
				application.processOneStepForApplication(canvas);

				//各オブジェクトの1コマの処理を行う
				for (int i = 0; i < elements.size(); i++) {
					AnimationElement element = (AnimationElement) elements
							.get(i);
					element.processOneStep(canvas);
				}
			}

			//図形を描く
			canvas.clear();
			application.drawForApplication(canvas);
			for (int i = 0; i < elements.size(); i++) {
				AnimationElement element = (AnimationElement) elements.get(i);
				element.draw(canvas);
			}
			canvas.update();

			//眠る
			try {
				synchronized (lockObject) {
					lockObject.wait();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * １ステップの時間を設定する
	 */
	public void setStepInterval(double stepInterval) {
		this.stepInterval = stepInterval;
	}

	/**
	 * キャラクターを追加する
	 */
	public void addElement(AnimationElement element) {
		elements.add(element);
	}

	/**
	 * キャラクターを削除する
	 */
	public void removeElement(AnimationElement element) {
		elements.remove(element);
	}

	/**
	 * 全キャラクターを削除する
	 */
	public void removeAllElements() {
		elements.clear();
	}

	/**
	 * 全キャラクターのリストを取得する 
	 */
	public List getAllElements() {
		return new ArrayList(elements);
	}

	/**
	 * タイマーを開始する
	 */
	private void restartTimer() {
		timer.scheduleAtFixedRate(new GUIAnimationTimerTask(), 0,
				(long) (stepInterval * 1000));
	}

	/**
	 * タイマーが1ステップごとに呼ぶ処理を表現するクラス
	 */
	class GUIAnimationTimerTask extends TimerTask {
		public void run() {
			synchronized (lockObject) {
				lockObject.notify();
			}
		}
	}

}