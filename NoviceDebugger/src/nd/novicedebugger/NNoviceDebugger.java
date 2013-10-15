/*
 * NNoviceDebugger.java
 * Created on 2013/01/10 by macchan
 * Copyright(c) 2013 Yoshiaki Matsuzawa, Shizuoka Univerisy
 */
package nd.novicedebugger;

import nd.com.sun.tools.example.debug.gui.GUI;

/*
 * NoviceDebugger(仮)
 * 
 * 2012/12/27 version 1.0.0 hakamata	・リリース
 * 2012/12/27 version 1.0.1 hakamata	・分かる範囲で警告を修正
 * 2012/12/27 version 1.1.0 hakamata	・終了時にTurtleも一緒に終了するようにした
 * 2012/12/27 version 1.2.0 hakamata	・ソース上にない命令はスキップ(StepIntoLineのみで実行可能になった)
 * 2013/01/06 version 1.3.0 hakamata	・turtleとそれ以外の初期停止位置自動切り替え
 * 										・ソースコードに行番号を追加
 * 										・フォントをMS UI Gothicに変更
 * 									 	・turtleを閉じたときデバッガも終了するように変更
 * 2013/01/07 version 1.4.0 hakamata	・スキップ中にSource Not Availableが出ないように修正
 * 										・デバッガ初期座標設定、
 *									  	・同一行の場合にスキップが完全でないのを修正
 *										・Turtleの場合の例外に対応
 * 2013/01/07 version 1.5.0 hakamata	・右クリップポップアップメニューを消去、変数表示に暫定対応
 * 2013/01/08 version 0.1.0 matsuzawa	・Look And Feelの変更
 * 										・UIの修正 Menubarの追加, Toolbarの位置変更
 * 										・ソースペインのカラーリング
 * 										・GUIクラスの varToolをstaticでないように修正
 * 										まだ開発バージョンなので，バージョン番号戻しました -> 0.1.0 
 * 2013/01/09 version 0.1.1 matsuzawa   ・コンパイラレベル1.7->1.5
 * 										・tools.jarの中のexampleコードと混ざっていたので，パッケージ名の変更，修正
 * 2013/01/09 version 0.1.2 matsuzawa   ・Macに対応
 * 										・tools.jarをMacのものに入れ替え．（Windowsのものだと，Macで動くが，例外を吐いている．MacのものはWinでも動いている．）
 * 2013/01/12 version 0.1.3 hakamata	・メソッドごとの変数表示ができるようにした
 * 										・一次元配列を変数表示に対応させた
 * 										・GUIクラスのsrcToolをprivateにし、staticでなくても動くようにした
 * 2013/01/12 version 0.1.4 matsuzawa	・メソッドごとの変数表示，リファクタリング
 * 										・メソッドごとの変数表示，バグ修正（currentのStackFrameに変数がないとすべて表示されない）
 * 										・メソッドごとの変数表示，バグ修正（nullの場合の処理）
 * 										・メソッドごとの変数表示，表示方法の変更機能
 * 										・メソッドごとの変数表示，currentFrame以外の変数をグレー表示
 * 2013/01/13 version 0.1.5 hakamata	・VariableToolの例外について修正
 * 2013/01/15 version 0.1.6 hakamata	・配列の初期値nullの場合の処理を追加
 * 										・ステップ実行間隔をスライドバーで設定できるようにした
 * 2013/01/17 version 0.1.7 hakamata	・自動実行中でも実行間隔を変更できるようにした
 * 										・スライドバーを右側が最小、左側が最大になるようにした
 * 2013/01/17 version 0.1.8 hakamata	・自動実行ツールをツールバーに移動し、ステップボタンと統合
 * 										・停止、再生、ステップボタンに文字ではなくアイコンを表示
 * 2013/01/17 verison 0.1.9 hakamata	・自動実行ツールのレイアウトを調整
 * 										・スクロールバーをスライダーに変更
 * 2013/01/22 version 0.1.10 hakamata	・警告を修正
 * 
 * 2013/09/26 version 0.2.0 hakamata	・BlockEditorと連携
 * 										・行間モード
 * 										・GUIクラスのstaticを解除
 * 										・ブレークポイントの復活
 * 										・選択方式の変更と右クリックメニューの仕組み変更
 * 2013/10/03 version 0.2.1 hakamata	・操作ボタンを2つにした
 * 										・速度設定スライダを7段階(仮)にした
 * 										（速度最速だとcontと同等)
 * 2013/10/10 version 0.2.2 hakamata	・ステップ自動実行だとブレークポイントで止まらないのを修正
 * 2013/10/10 version 0.2.3 hakamata	・変更された変数が黄色でハイライトされるようにした
 * 
 * 2013/10/11 version 0.2.4 hakamata	・ブレークポイント，表示モード切り替え，contのログ書き出し
 * 2013/10/11 version 0.2.5 hakamata	・行番号をテキスト直書きからScrollPaneのヘッダに変更
 * 										・標準モード(行モード)を今までのアイコンと行塗りつぶしの形に戻した
 * 2013/10/11 version 0.2.6 hakamata	・ビューの配置を変更(上の左：ソース，上の右：変数，下：コンソール)
 * 2013/10/15 version 0.2.7 hakamata	・Breakpointを一旦OFFにした
 * 										・変更があった変数だけでなく，新しい変数も黄色ハイライトされるようにした
 * 2013/10/15 version 0.2.8 hakamata	・BEから起動時はウィンドウ縮小＆ソースビュー最小化
 *
 * 	・int x = 3;　はＯＫだけど， int y; はダメ．
 * 		Javaの仕様上,宣言のみのプリミティブ型変数はスタックに積まれないのでこれで正しい
 *　　　
 * 
 * */
public class NNoviceDebugger {

	public static final String NAME = "NoviceDebugger";
	public static final String VERSION = "0.2.7";
	public static final String WINDOWTITLE = "DENO";

	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.run(args);
	}

}
