/*
 * PPresVisualizer.java
 * Created on 2012/02/18
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ppv.app.datamanager.PPDataManager;
import ppv.view.frames.PPDataManagerFrame;
import clib.common.compiler.CJavaCompilerFactory;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

/*
 * PPresVisualizer (通称 PPV)
 * @author macchan
 * 
 * <既知の問題点>
 * @ ファイルの削除に対応したい（コンパイル時に問題となる）
 * @ bug06 コンパイルの対象をどうするべきか．　一覧から実行したとき困る！
 * @ RUNは，ひとまず，直近のパスでFQCNを決めるようにしている，が，それでよいか（PRESできっちりとれてたっけ？）
 * 
 * 1.6.3 2013.10.13 matsuzawa
 * ・Java Information
 * 
 * 1.6.2 2013.09.24 matsuzawa
 * ・PLPackageがload時にsubdirectoryをloadしない問題を修正（Ronproでやっていたので問題なかった）
 * ・MetricsPrinterが1レベルのファイルしか書き出さない問題を修正（Ronproでやっていたので問題なかった）

 * 1.6.1 2013.05.16 matsuzawa
 * 	・zipファイルで読み込めないものがあると止まってしまうバグを修正
 * 
 * 1.5.17 (1.6.0) 2013.03.xx matsuzawa
 * 	・BlockEditor論文のためのいろいろな修正，仕様変更
 * 
 * 1.5.17 2012.12.05 sakakibara
 * 		・選択問題のバグを修正
 * 		・メトリクス解析の時に必要なファイルがない場合、ダイアログを表示するように変更
 * 
 * 1.5.16 2012.12.05 sakakibara
 * 		・コンパイルエラーメッセージ分析のバグを修正
 * 
 * 1.5.15 2012.12.04 sakakibara
 * 		・TrialAndErrorAnalyticsプロジェクトが読み込めてないバグを修正（build.xmlを変更）
 * 
 * 1.5.14 2012.12.04
 * 		・JVMエラーチェック
 * 
 * 1.5.13 2012.12.04
 * 		・libを変更
 * 		・重い．メモリが足りない．->1500MB
 * 		・BlockEditorのステータスを表示
 * 
 * 1.5.12 2012/07/25 macchan
 * 		・ bug#11 fix(応急処置)
 * 
 * 1.5.12 2012/07/25 macchan
 * 		・ bug#11 fix(応急処置)
 * 
 * 1.5.11 2012/07/19 macchan
 * 		・ bug#10 fix
 * 
 * 1.5.10 2012/07/05 macchan
 * 		・ bug#8 fix
 * 
 * 1.5.9 2012/06/21 macchan
 * 		・ cashフォルダがProjectSet毎になっておらず，名前が重複する可能性があったのを修正
 * 		・ cash clear時に進捗ダイアログ表示
 * 
 * 1.5.8 2012/06/21 macchan
 * 		・授業中 bug#07 応急処置 zip単品で読み込んだ場合の問題 　
 * 
 * 1.5.7 2012/06/21 macchan
 * 		・bug#03 応急処置 PLStampedProject, //srcフォルダがなければ作る　
 * 
 * 1.5.6 2012/06/21 macchan
 * 		・bug#06 コンパイル対象ファイルと，次回実行時のファイルのみコンパイルするよう変更（これで当分ＯＫ）．
 * 
 * 1.5.5 2012/06/21 macchan
 * 		・bug#06 最後のコンパイル時は，srcから最終版をとってくるように変更（コンパイル出来ないファイルを消されると，実行出来ないので，一時的な処置）
 * 
 * 1.5.4 2012/06/20 macchan
 * 		・bug#06 やっぱり駄目だったので，wildcardでコンパイルするように変更　（削除されたものもコンパイルされてしまう，という問題が残る．）
 * 
 * 1.5.3 2012/06/20 macchan
 * 		・bug#06 fix (classファイルをコピーしないようにする．　コピーして，コンパイル対象外ソースが変更された場合の問題のため，　うまく動くか様子見)
 * 		・obpro.jarを最新版に変更
 * 
 * 1.5.2 2012/06/20 macchan
 * 		・保管したファイルが見られないバグを修正
 * 
 * 1.5.1 2012/06/20 macchan
 * 		・最終的なファイルがあるのに，履歴ファイルがない場合は補完する機能の修正　（1.5.0では正常動作しない）
 * 			(Eclipse, Ronpro両対応)
 * 
 * 1.5.0 2012/06/20 macchan
 * 		・最終的なファイルがあるのに，履歴ファイルがない場合は補完する機能を追加
 * 			（PPConverterを復活させ，PPMissingFileManager.javaとして追加）
 * 			（Eclipse読み込み時のみ）
 * 
 * 1.4.6 2012/06/20 macchan
 * 		・複数ファイルのコンパイルエラー区間表示のバグを修正　PPCompileErrorStateLineView
 * 
 * 1.4.5 2012/06/20 macchan
 * 		・プロジェクトビューアにプロジェクト名を表示（unit名だけでなく）
 * 		・複数ファイルのコンパイルエラー区間表示のバグを修正　PPCompileErrorStateLineView 途中
 * 
 * 1.4.4 2012/06/20 macchan
 * 		・prune()などでエラーを出し，対象外となったプロジェクトはsyserrに表示する．
 * 
 * 1.4.3 2012/06/20 macchan
 * 		・データディレクトリを変更できるように修正（開発用が.pres2の対象になり，負荷がかかりすぎる為）
 * 
 * 1.4.2 2012/06/20 macchan
 * 		・プロジェクト単体でエラーが出たとき，全体の処理は続行するように仕様変更
 * 
 * 1.4.1 2012/06/20 macchan
 * 		・bug#4 一時的なfix
 * 
 * 1.4.0 2012/06/20 macchan
 * 		・作業項目バー, 実装完了
 * 		・欠陥記録ログ，第一版　実装完了
 * 
 * 1.3.5 2012/06/18 macchan
 * 		・作業項目バー（実装途中）
 * 		・bug#2 fix
 *  
 * 1.3.4 2012/05/17 macchan
 * 		・授業中，学生の指摘に対応
 * 			・範囲外に赤線，青線を移動できないようにする（スクロールはする）
 * 			・MacでTaskViewerを×で消し，Addボタンを押すとゾンビTaskViewerが現れる．（TaskViewerのModel-Viewを分離し，Windowを毎回生成するようにする．）
 * 
 * 1.3.3 2012/05/17 macchan
 * 		・授業中の問題に対応
 * 			・PPTask(作業記録)にアサインされているPPCETask(見積記録)を削除すると，ゾンビ参照の作業記録が出てExport出来ない問題を解決
 *
 * 1.3.1 2012/05/17 macchan
 * 		以下の要望に対応
 * 		・TaskViwerの実績欄の＋ボタンを押しても、青いバーの位置が変わらない（addボタンの場合は変わってくれる）
 * 		・TaskをAddする時、最初に「対応見積もり」を選択すると「作業内容」が変わってくれるが、２回目以降は変わってくれない
 *  
 * 1.3.0 2012/05/16 macchan
 * 		・予実機能の追加
 * 		・HTML-Exporting
 * 
 * 1.2.4 2012/05/10 macchan
 * 		・授業中　#bug01 fix
 * 		・授業中　累積時間Exportの問題を修正
 * 
 * 1.2.3 2012/05/09 macchan
 * 		・ナビゲータの順序を変更，Run->Compile->Editの順
 * 
 * 1.2.2 2012/05/09 macchan
 * 		・右クリックisMetaDown()でも青線を動かせる
 * 
 * 1.2.1 2012/05/09 macchan
 * 		・出力微調整(計を出力)
 * 
 * 1.2.0 2012/05/09 macchan
 * 		・タスク定義機能の追加
 * 
 * 1.1.4 2012/05/08 macchan
 * 		・実行時文字化けの解決
 * 		・CEncoding.getVMEncoding();->CEncoding.getSystemEncoding();
 * 
 * 1.1.3 2012/05/08 macchan
 * 		・srcdir.txtが設定されておらず，Eclipse読み込みしたプロジェクトが実行出来ない問題を解決 bugfix
 * 
 * 1.1.2 2012/05/08 macchan
 * 		・Ronproデータも変更ビューに反映出来るようにする
 * 			（読み込み時にTextEdit-Eclipse形式に変更してしまう方式）
 * 
 * 1.1.1 2012/05/08 macchan
 * 		・zipファイル読み込みで日付フォルダを自動で作るように仕様変更
 * 
 * 1.1.0 2012/05/08 macchan
 * 		・ テキスト変更ビューの追加．（TEXTEDIT, ECLIPSEのみ）
 * 			・ 改行コードASIS読み込み（indexがずれるので）
 * 		・ 一番上のナビゲータをTextEditナビゲータに変更
 * 		・パッケージのビューで，ソース名クリックでソース表示ホールド，ダブルクリックでアンホールド
 * 
 * 1.0.6 2012/03/1 macchan
 * 		・ タイムライン表示のファイル並び順をstart時間順に並び替える
 * 			・PPProjectViewerFrame
 * 		・単体srcフォルダの場合，自動で中身を展開する．（仮の機能）・
 * 			・PPProjectViewerFrame
 * 
 * 1.0.5 2012/03/1 macchan
 *		・ソースViewアルゴリズムで，x座標が右端によってしまう問題を修正（現状の位置に戻すようにする）．
 *
 * 1.0.4 2012/03/1 macchan
 *		・ronproのデータ動かない問題のbugfix (ソース設定のadhoc実装に問題有り．"ronpro"フォルダのみsrcフォルダが""にされていた)
 *		・新仕様，eclipseは読み込み時にsrcフォルダを設定，デフォルトを""
 *		・Eclipse未テスト
 * 
 * 1.0.3 2012/02/20 macchan
 *		・フォルダ構成の変更
 *
 * 1.0.2 2012/02/20 macchan
 *		・ProjectSet削除機能
 *		・キャッシュクリア機能
 *   
 * 1.0.1 2012/02/18 macchan
 *		・時間レーンが32000ピクセル以上に達すると，コンパイル，実行ラインが消えるバグをfix
 *		・UTF8のプロジェクトが文字化けする問題をfix
 *		・ドラッグアンドドロップでzipファイル，またはfolderを読み込める機能．
 *		・ProjectSetを開いてから，Analyticsメニューで各種Analysisができる．
 *   
 * 1.0.0 2011/xx/xx macchan
 *   ・コンパイラがないJREで実行したときにJava Compiler Interfaceでエラーが出る問題を修正
 *   ・最終的なファイルが，.pres2フォルダに記録されていない？
 * 
 */
public class PPresVisualizer {

	public static final String VERSION = "1.6.3";

	protected PPresVisualizer() {
	}

	void run() {
		run(CFileSystem.getExecuteDirectory());
	}

	void run(CDirectory dir) {
		String path = System.getProperty("sun.boot.library.path");
		if (!CJavaCompilerFactory.hasEmbededJavaCompiler()) {
			JOptionPane.showMessageDialog(null, new JLabel(""
					+ "<html><pre>Your JVM has no Embedded JavaCompiler.\n"
					+ "The system uses external javac compiler but it works extremely slow.\n"
					+ "Your JVM path = "+ path
					+ "</pre></html>"
					));

		}
		PPDataManager manager = new PPDataManager(dir);
		PPDataManagerFrame frame = new PPDataManagerFrame(manager);
		frame.setBounds(100, 100, 300, 300);
		frame.setVisible(true);
	}
}
