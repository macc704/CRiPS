/*
 * PRGUIApplication.java
 * Created on 2010/02/11 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.application.gui;

/**
 * PRGUIApplication
 * 
 * // 複数への拡張の際，即座にデータモデル必要と思いつく
 * // ハッシュ？毎回作る？アルゴリズムを考える．
 * // ファイルチェッキングとlastModified更新をどうするか．
 * 
 * // 　ぬルポが出る
 * // ぬルポが出たとき，どこを見ていくかもポイント．
 * 
 * // 　記録ファイルの改ざん防止．
 * // 初期火事にまた記録されてしまう．（上書き？）
 * 
 * // 日付の扱いを考える．とりあえずMILLSECで．
 */
public class PRGUIApplication {

	public static void main(String[] args) {
		PRRecorderFrame.main(args);
	}

}
