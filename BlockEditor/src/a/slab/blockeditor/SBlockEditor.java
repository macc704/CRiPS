package a.slab.blockeditor;

import javax.swing.UIManager;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import controller.WorkspaceController;

/*
 * BlockEditor Application
 * 
 * 2011/10/25 version 1.0.0 リリース
 * 2011/10/25 version 1.0.1 論プロエディタでProjectを選択したとき、 フレームの名前に以前選択していたJavaファイルが表示される不具合を修正。
 * 2011/10/25 version 1.0.2 論プロエディタでProjectを選択したとき、BlockEditorｓのSaveボタンもしくはRunボタンを押したときNullPointerのエラーが表示される不具合を修正。 
 * 2011/10/25 version 1.0.3 論プロエディタでJavaのコンパイルが失敗したとき、BlockEditorのタイトルにJavaのコンパイルが失敗したことを表示するようにした。
 * 2011/10/25 version 1.0.4 連結されていないブロックにBlockエラーがあるときにもエラーが表示されてしまう不具合を修正。
 * 2011/10/25 version 1.0.5 BlockにSyntaxエラーがあるときに表示されるダイアログの文言を修正。 
 * 2011/10/25 version 1.0.6 中身がない節をもつとき、JavaからBlockに変換されない不具合を修正。 
 * 2011/10/25 version 1.0.7 BlockからJavaソースに変換するたびに、空行が増えていく不具合を修正。 
 * 2011/10/25 version 1.0.8 BlockEditorの「進める」ブロックと「戻す」ブロックの初期値を50に修正。 
 * 2011/10/25 version 1.0.9 BlockEditorの「戻す」ブロックのラベルを「戻る」に修正。 
 * 2011/10/25 version 1.0.10 BlockEditorの「右へ回る」ブロックと「左へ回す」ブロックのパレットの位置を修正。 
 * 2011/10/25 version 1.0.11 BlockEditorの「右へ回る」ブロックのラベルを「右を回す」に修正。 
 * 2011/10/25 version 1.0.12 BlockEditorの「ペンの色を変える」ブロックの引数のラベルを「色」にした。 
 * 2011/10/25 version 1.0.13 BlockEditorの「ペンの色を変える」ブロックの初期値のラベルが英語になっているのを修正。 
 * 2011/10/25 version 1.0.14 BlockEditorの「コンソールに出力する」ブロックの初期値のラベルを「あいうえお」に修正。 
 * 2011/10/25 version 1.0.15 BlockEditorの「Save」ボタンを「Save→Java出力」に、「Run」ボタンを「Save→Java出力して実行」に修正。
 * 2011/10/25 version 1.0.16 JavaからBlockに変換したとき、ブロックの位置が右に寄りすぎているのを修正。
 * 2011/10/25 version 1.0.17 BlockEditorのワークスペースにあるOverviewを非表示にした。 
 * 2011/10/25 version 1.1.18 「ペンの色を変える」ブロックに色指定以外のブロックが入らないようにした。 
 * 2011/10/25 version 1.1.0 BlockEditorの分岐ブロック（if）は使わないように修正。JavaからBlockに変換後、else節を使いたくなる可能性があるため。
 * 2011/10/25 version 1.1.1 BlockEditorの変数の代入ブロックで代入する値がJavaソースコードに反映されない問題を修正。
 * 2011/10/25 version 1.1.2 JavaからBlockに変換するとき、マイナスの値が変換されない問題を修正。 
 * 2011/10/25 version 1.1.3 JavaからBlockに変換するとき、random関数の形がおかしくなる問題と、引数がブロック化されない問題を修正。
 * 2011/10/25 version 1.1.4 JavaからBlockに変換するとき、else if(・・・)が解析されない問題を修正。
 * 2011/10/25 version 1.1.5 BlockEditorのボタンで、「Save→Java出力」を「Java出力」に、「Save→Java出力して実行」を「Java出力して実行」にする
 * 2011/10/25 version 1.1.6 ファクトリーのカテゴリで、「変数」を削除し、「変数の定義」と「変数の読み書き」を追加した。
 * 2011/10/25 version 1.1.7 比較ブロックで、ブロックが完全に組まれていないときのエラーダイアログの文言が表示されない問題を修正した。
 * 2011/10/25 version 1.1.8 JavaからBlockに変換するとき、BlockEditorでローカル変数ブロックの後ろにあるブロックが生成されない問題を修正した。 
 * 2011/10/25 version 1.2.0 Postfixブロック（「変数」を増やすブロック）を追加した。 
 * 2011/10/25 version 1.2.1 remainderブロックがjavaに変換されない問題を修正した。 
 * 2011/10/29 version 1.3.0 ブロックの折りたたみ機能追加。
 * 2011/10/29 version 1.3.1 ブロックの複製機能追加。
 * 2011/10/29 version 1.3.2 二つ以上の初期引数が設定できるように修正。 
 * 2011/10/29 version 1.3.3 変数名に空白、記号が使えないように修正。
 * 2011/10/29 version 1.3.4 文字列に『'』、『"』、『\』があるとエスケープするように修正。
 * 2011/11/17 version 1.3.5 ブロックの複製機能の不具合修正。
 * 2011/11/18 version 1.3.6 変数の値ブロックを変数定義ブロックから生成するように修正。
 * 2011/11/18 version 1.3.7 変数ブロックを修正。
 * 2011/11/19 version 1.4.0 オブジェクト変数ブロックを追加。
 * 2011/11/19 version 1.4.1 オブジェクト生成ブロックを追加。
 * 2011/11/20 version 1.4.2 オブジェクトのメソッド参照ブロックを追加。
 * 2011/11/23 version 1.5.0 論プロエディタに組み込み
 * 2011/11/23 version 1.5.1 授業でのお試し版
 * 2012/09/27 version 2.0.0 バグ修正(松澤)
 * 							新機能追加．(elseif文のリバース，for文のリバース)
 * 2012/09/27 version 2.0.1 文字コード問題でmacで動かない問題を修正中
 * 2012/10/03 version 2.1.0 文字コード問題を解決
 * 							BlockEditor <--> Converter間のXMLファイルはUTF-8で統一する．Javaファイルは任意で，外部から設定する．
 * 2012/10/03 version 2.1.1 文字コード問題のバグをfix <-修正されていない
 * 							CommentGetterで行数を数える際の機種依存改行コードの問題 
 * 2012/10/03 version 2.1.2 ウインドウタイトル生成の不具合を修正（松）
 * 2012/10/03 version 2.1.3 文字コード問題のバグを再fix CommentGetterのアルゴリズムを変更
 * 2012/10/03 version 2.1.4 文字コード問題のバグを再々fix block->JavaでXMLを読み込む際の文字コード指定忘れ
 * 2012/10/03 version 2.1.5 文字コード問題のバグを第3fix 文字コード指定方法変更 （Mountain Lion, JDK1.7の組み合わせで動作しない）
 * 							macで変数が出ない問題は解決した．
 * 2012/10/03 version 2.1.6 lightGray, darkGrayでエラーが出る問題を修正．
 * 2012/10/03 version 2.1.7 接続音が重なったときに消えてしまうバグを修正．（Soundクラス）
 * 2012/10/03 version 2.1.8 
 * 問題1　j->bしてブロックをブロック間に挿入すると例外がでる問題の応急処置．
 * Exception in thread "AWT-EventQueue-0" java.lang.RuntimeException: trying to link a plug that's already connected somewhere.
	at codeblocks.BlockLink.connect(BlockLink.java:159)
　　 現象=>2回目以降の構築で，間にブロックを挿入しようとすると出る．
   blockEditor.resetWorkspace();は呼ばれてる
   // blockEditor.resetLanguage();
   // blockEditor.setLangDefDirty(true);
      を呼ぶと，１回目から出る．
   workspaceのlistenerが消去されておらず，  resetLanguage()で，２つめのハンドラが登録されてしまうことが問題．
 *　問題2 プルダウンメニューをドラッグすると例外，->はかないように応急処置
 * 2012/10/07 version 2.1.9 抽象化ブロックを閉じたときに親の大きさが変わらない問題の修正．
 * 	・空の抽象化ブロックを開いたときの動作も修正された．
 * 2012.10.10 version 2.1.10
 * ・Javaでコメントにスペースがあると\が入る．
 * 		XMLはＯＫ，BlockまでＯＫ，BlockLabelまでＯＫ，BlockWidgetOK
 * 	    LabelWidget#updateLabelText() にて解決
 * ・抽象化ブロックの開閉状態が元にもどる．
 * 	Block#getSaveStringコメントアウトされていた．解決
 * 	2012.10.10 version 2.1.11
 * 		以下のバグを修正
 * 		・color(java.awt.Color.lightGray);　（色の変更）　がある状態で、OpenBlockEditorをすると色ブロックがうまく反映されない（int型の値ブロックになる）
 * 		・boolean型の変数を作り・・・というブロックを使用するとデフォルトで入っている初期値がtrue（値ブロックだと真偽）
 *　	2012.10.16 version 2.1.12
 * 		・ ！？などの記号が入るようにする
 * 		・LabelWidget#BlockLabelTextField
 *  2012.10.18 version 2.2.0
 *  	・授業中に発見　閉じた抽象化ブロックの中のブロックに，他の抽象化ブロックがくっついてしまうバグを修正
 * 		・BlockLinkChecker#getLink
 *  2012.10.21 version 2.3.0
 *  	・スクリーンショット機能
 *  2012.10.21 version 2.3.1
 *  	・スペースから始まる抽象化ブロックLabelを変更できない問題を修正
 *  2012.10.21 version 2.3.2
 *  	・スクリーンショット機能，ブロックを閉じているときも，中身をスペース計算してしまう問題を修正
 *  2012.10.23 version 2.4.0
 *  	・toJavaRunを廃止 compileを別個に作成
 *  	・toJavaでコンパイルしていたのを廃止
 *  2012.10.23 version 2.4.1
 *  	・if(); while(); の処理　（途中）
 *  2012.10.23 version 2.5.0
 *  	・ while();が出来ていなかった問題を修正．
 *    	・dirty状態の追加（仮），変数の初期値ラベルが，変化しなかったときもnotifyされる問題を修正．
 *    	・変数が宣言されていないエラーで，名前を考慮していなかったので修正．
 *  2012.10.29 version 2.6.0
 *  	・instanceのmethod call時，J->Bで接続がおかしくなる問題を修正 ExCallMethodModel#print()　
 *  2012.10.29 version 2.6.1
 *  	・ lang_def.xmlをオブジェクト指向版に修正．
 *  2012.10.30 2.6.2
 *  	・ オブジェクトブロックのコンテキストメニューの文言変更
 *  2012.10.31 2.6.3
 *  	・SSメニューで復活
 *  2012.11.1 2.7.0
 *  	・ J->B ConnectorType実装の変更，int以外の接続コネクタがおかしくなるbugfix
 *  	・Objectの右メニュークリックで書き込み，値
 *  	・lang_def.xmlを書き換え（Objectの書き込み，値）に対応，looks対応
 *  2012.11.4 2.8.0
 *  	・メソッド呼出しの戻り値の計算の新設計（今まではnumber型のみだった）
 *  2012.11.4 2.8.1
 *  	・メソッド登録
 *  2012.11.4 2.8.2
 *  	・signatureの異なるメソッドの扱い，引数の数で対応
 *  2012.11.6 2.9.0
 *  	・Block->Javaに対応．大幅な変更（アドホックな実装もあり）
 *  	・Java->Blockのnumber以外の戻り値に対応．大幅な変更(CallGetterMethodModel)（アドホックな実装もあり）
 *  2012.11.6 2.9.1
 *  	・SoundTurtleに対応
 *  2012.11.6 2.9.2
 *  	・多くの不具合を修正，ブロックカテゴリの整理
 *  2012.11.7 2.9.3
 *  	・抽象化ブロックの中にある変数をコピーしたときの問題の回避（根本的ではない）
 *  	・tt.text(tt.getText());で，最初のCallGetterMethodModelのコネクタがおかしくなる問題の修正 (CallGetterMethod#getType()メソッドの修正)
 *  	・ブロックカテゴリの調整
 *  2012.11.7 2.9.4
 *  	・SpecialBlockに対応
 *  2012.11.7 2.9.5
 *  	・9, 10章のテストにおけるバグ修正
 *  		・isShow, show問題の改善 setShow追加
 *  		・warp(mouseX())でエラーのバグを修正
 *  2012.11.7 2.9.6
 *  	・B->Jでエラーが出たときのダイアログを変更(Descriptionが見られる)
 *  	・コンストラクタ引数が無いときの無名エラー（エラーが出ず，引数を無視するしようにしたが，完成されてないエラーの方が良いか）
 *  	・SoundTurtleのすべてのメソッドに対応．
 *  2012.11.8 2.9.7　授業中
 *  	・else ifブロックで出力できるように改良．
 *  2012.11.13 2.10.0 CUIへの対応
 *  	・Special-Expressionブロック
 *  	・double型の導入
 *  	・各種変換ブロック
 *  2012.11.13 2.10.1 CUIへの対応(2)
 *  	・CUIカテゴリ，Sysoutなどのブロックと変換
 *  	・Scannerブロックと変換
 *  2012.11.13 2.10.2 CUIへの対応(3)
 *  	・castのJ->Bがうまくできていなかったので修正
 *  	・Scannerブロックと変換
 *  2012.11.13 2.10.2 CUIへの対応(4)
 *  	・doubleの演算ブロックを追加
 *  2012.11.14 2.10.3 CUIへの対応(5)
 *  	・全体的なバグ修正
 *  	・System.out.println()等の特別対応
 *  	・scanner.getInt()の実装
 *  2012.11.14 2.10.4 CUIへの対応(6)
 *  	・t.fd(100)をforwardしたとき，余計な;がつく．  ->修正
 *		・window.warp()を戻すと余計な；とwarpが入る．  ->修正
 *		・;（空ブロック）があると，乱れる．  ->修正
 *	2012.11.14 2.10.6 CUIへの対応(6)
 *		・ xmlファイル分割->Turtle, CUI自動変換
 *		・scanner.nextString()ではなく，next()->対応
 *		・文字列に変換，poly版ばメニューに残っていた．
 *		・数学関数に多少対応．
 *	2012.11.14 2.10.7 CUIへの対応(7)
 *		・ キャストのJ->Bに対応
 *	2012.11.14 2.10.8 CUIへの対応(8)
 *		・文字列へのキャスト時にdouble型がおかしかったのを修正（ElementModelのtype解決プログラムのバグ）
 *		・next()ではなく，nextLine()． nextDouble()追加で教科書に対応．
 *		・メソッド実行ブロック（double型）を追加
 *	2012.11.14 2.10.9 CUIへの対応(9)
 *		・多項式演算ができていなかった問題を修正
 *		・double型への変換を含む中置演算ができなかった問題を修正．
 *		・文字列連結式への誘導(ExInfixModel)の不具合を修正
 *	2012.11.14 2.10.10 CUIへの対応(9)
 *		・カテゴリの整理
 *	2012.11.14 2.10.11 CUIへの対応(10)
 *		・<=がでない．<だけしかでない　解決
 *		・double型のインクリメント演算子　Block->Java, Java->Block　ができない 解決
 *	2012.11.14 2.10.12 CUIへの対応(11)
 *		・デフォルトのID番号が足りず，復帰不可能なエラーとなる問題を修正
 *	2012.11.14 2.10.13 CUIへの対応(12)
 *		・callDoubleMethod()が実装されておらず，scanner.nextDouble()のB->Jが出来ない問題を解決
 *	2012.11.23 2.10.14 CUI二週目 
 *		・CUIカテゴリに，CUIでは利用できないrandom()があったので削除．
 *		・genus関係のxml定義ファイルの整理
 *		・hashCode()の実装
 *	2012.11.23 2.10.15 CUI二週目 
 *		・Not　(!) の実装
 *		・SpecialExpressionの作成場所を変更し，Statement処理時の例外ではなく，Expression処理時の例外時にSpecialExpressionにするようにした．
 *	2012.11.23 2.10.16 CUI二週目 
 *		・equals-boolean, not-equals-boolean の実装
 *		・equals-string の実装．equalsで比較するように工夫してある．
 *  2012.11.23 2.11.0 メソッド
 *  	・コンテキストメニューのクリエイター系の整理とコード共通化
 *  	・メソッド関係のブロックの整備
 *  	・メソッドを呼び出せるコンテキストメニュー
 *  	・引数が参照できるコンテキストメニュー
 *  	・引数の吐き出し，新しいメソッドの吐き出し
 *  2012.11.24 2.11.1 メソッド
 *  	・引数に書き込みができるようにした（行き帰り）
 *  	・メソッド（引数）からの帰りを出来るようにした．
 *  2012.11.24 2.11.2 メソッド
 *  	・引数が内場合のJ->Bが出来ていなかったのでbugfix
 *  	・戻り値に対応（行き帰りＯＫ）
 *  2012.11.24 2.11.3 メソッド
 *  	・メソッドの位置 100ドットずつ右にずらす
 *  2012.11.24 2.11.4 メソッド
 *  	・メソッドを消したとき，Javaも消す．
 *  2012.11.24 2.11.5 メソッド
 *  	・bugfix メソッドを消したとき，Javaも消す． で，static mainも消えてしまっていた．
 *  	・bugfix B->JでユーザメソッドをExpressionとして呼んだ場合も;がついてしまう．
 *  	・引数のJ->Bで引数名が消えてしまう(2.11.4で対策したつもりだが動かない状態)
 *  2012.11.24 2.11.6 回帰テストでバグ取り
 *  	・bugfix StringToDoubleの型が違っていた．単純ミス
 *  	・bugfix 文字列連結が全然出来なくなっていた．==, !=をはねるコードが+でも効いてしまっていた． 
 *  	・bugfix double型へのキャストに()がついておらず，B->Jで意味が変わってしまっていた．
 *  	・bugfix scanner->next()で動いていなかった
 *  2012.11.24 2.11.7 
 *  	・bugfix 色が使えなくなっていたので修正．
 *  	・FlowViewer出見えるように getLabel()を実装
 *  2012.11.25 2.11.8 
 *  	・bugfix double型の引数が出力されない問題を修正
 *  	・bugfix "a" + "b" .hashCode()が出来ない問題を修正
 *  	・bugfix CUIにturtleのrandomがある問題を修正
 *  2013.01.08 2.11.9
 *  	・ListTurtleなどのパラメタライズドクラスに準対応
 *  	・ListTurtleに対応中
 *  2013.01.09 2.12.0
 *  	・ListTurtleに対応
 *  	・オブジェクトシステムを大幅刷新
 *  2013.01.09 2.13.0
 *  	・doWhile, break, continueに対応　
 *  2013.01.09 2.13.1
 *  	・「増やす」の値が飛ぶバグの修正，
 *  	・ListTurtleのほとんどのメソッドに対応
 *  	・CardTurtleのメソッドに対応　
 *  2013.09.26 2.14.0 Ohata
 *  	・インスタンス変数の追加
 *  	・位置情報の追加
 *  	・メソッドの開閉状態の追加（未完　バグあり）
 *  	・変数ハイライトの追加
 *  	・ゲッター/セッター/コンストラクタの追加
 *  2013.09.28 2.14.1 Ohata
 *  	・ハイライトされないブロックが発生する問題を修正
 *  	・抽象化ブロック生成時にNull Pointer Exceptionが発生する問題を修正
 * 	 	・javaからブロックを生成時、private変数のラインナンバーをxmlに書き出す際の問題を修正
 *  						
 * <TODO>
 * ・コメントにxmlのタグが入るとエラー．
 * ・引数の使用時にスコープのチェック
 * ・Objectの引数
 * ・引数のJ->Bで引数名が消えてしまう(2.11.4で対策したつもりだが動かない状態)
 * 
 * ・ ExCallMethodModel#print() getConnectorId()周りのコードを修正すること．概念を整理する必要があり．
 * ・if(); while(); の処理（完全に） ->大体ＯＫ
 * ・dirty状態の追加（完全に） ->大体ＯＫ
 * ・文字コード関連，パッケージ間依存とクラス構成もう少し整理すること
 * ・複製関係の動作チェックすること． 
 * ・java version "1.6.0_35"　日本語の文字化け（プログラム内・エラー本文）はありませんでした。
 * ・java version "1.7.0_07"　日本語の文字化けは RonproEditor.jar をダブルクリックして起動したときに見られました。コマンドプロンプトから実行したときは文字化けしませんでした。
 * 
 */
public class SBlockEditor {

	// frame name and version infomation
	public final static String APP_NAME = "Block Editor";
	public final static String VERSION = "2.14.1";

	public static final String ENCODING_BLOCK_XML = "UTF-8";
	public static final boolean DEBUG = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SBlockEditor().test();
	}

	void test() {
		initializeLookAndFeel();
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Create a new WorkspaceController
				WorkspaceController wc = new WorkspaceController(
						"support/images/");

				wc.setLangDefFilePath("support/lang_def.xml");
				wc.loadFreshWorkspace();
				wc.createAndShowGUIForTesting(wc, "SJIS");
			}
		});
	}

	private void initializeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
