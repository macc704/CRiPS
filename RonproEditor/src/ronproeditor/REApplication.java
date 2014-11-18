/*
 * JavaEditorApplication.java
 * Created on 2007/09/14 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import nd.com.sun.tools.example.debug.gui.CommandInterpreter;
import nd.com.sun.tools.example.debug.gui.GUI;
import nd.novicedebugger.NDebuggerListener;
import nd.novicedebugger.NDebuggerManager;
import pres.core.model.PRCommandLog;
import pres.core.model.PRLog;
import pres.core.model.PRTextEditLog;
import ronproeditor.bytecode.REBytecodeViewerGenerator;
import ronproeditor.dialogs.RECommentInputDialog;
import ronproeditor.dialogs.RECreateFileNameDialogForCopy;
import ronproeditor.dialogs.RECreateFileNameDialogWithType;
import ronproeditor.dialogs.RECreateNameDialog;
import ronproeditor.dialogs.RECreateProjectNameDialog;
import ronproeditor.dialogs.REDirtyOptionDialog;
import ronproeditor.dialogs.RERefactoringFileNameDialog;
import ronproeditor.dialogs.RERefactoringProjectNameDialog;
import ronproeditor.ext.REBlockEditorManager;
import ronproeditor.ext.RECheCoProManager;
import ronproeditor.ext.RECocoViewerManager;
import ronproeditor.ext.RECreateCocoDataManager;
import ronproeditor.ext.REFlowViewerManager;
import ronproeditor.ext.REGeneRefManager;
import ronproeditor.ext.REPresVisualizerManager;
import ronproeditor.helpers.FileSystemUtil;
import ronproeditor.helpers.JavaEnv;
import ronproeditor.helpers.NewZipUtil;
import ronproeditor.helpers.RECommandExecuter;
import ronproeditor.views.DummyConsole;
import ronproeditor.views.REFrame;
import ronproeditor.views.RESourceEditor;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CFilename;
import clib.common.filesystem.CPath;
import clib.common.system.CJavaSystem;
import clib.preference.app.CPreferenceManager;
import clib.view.dialogs.CErrorDialog;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

/*
 * Ronpro Editor Application
 * 
 * 
 * 2007/09/21 version 1.0.0 リリース 
 * 2007/09/21 version 1.0.1 -cp が動かない環境があるのを修正 -classpathに 
 * 2007/09/21 version 1.0.2 コンパイル時にコンソールをクリアするように修正
 * 2007/09/21 version 1.0.3 Mac Runtime#exec() でclasspathに""をつけると動かない問題を修正 
 * 2007/09/21 version 1.0.4 コンソールでCtrl-Rで起動したときの入力の不具合解消 
 * 2007/09/21 version 1.1.0 コンソールのErrを赤く表示（テキストペインに変更） 
 * 2007/09/22 version 1.2.0 キーワードハイライト追加, エディタをテキストペインに変更 
 * 2007/09/22 version 1.3.0 フォーマットをタブ主体に切り替え , テキストペインのタブ4文字に
 * 2007/09/22 version 1.4.0 削除機能を追加，リファクタリング方法の統一化 
 * 2007/09/22 version 1.5.0 TreeViewerで，ルートが見えるようにして，使い勝手を良くする デフォルトルートをMyProjectに変更 
 * 2007/09/22 version 1.5.1 ダイアログの見た目の修正
 * 2007/09/22 version 1.5.2 サンプルテンプレートを拡充 
 * 2007/09/22 version 1.5.3 TreeがScrollになっていなくて多くなるとしたが切れてしまったのでScrollバーをつけた 
 * 2007/09/22 version 1.5.4 Treeが何も選択されていないときにもう一度選択してしまうと例外が出ていたのを修正 
 * 2007/09/22 version 1.6.0 パッケージを利用できるようにした．（RonproEditor自身をコンパイルしたかったので）
 * 2007/09/22 version 1.6.1 Macでスペースが使えない問題をcommand発行の仕組みを変えることで修正 
 * 2007/09/22 version 1.6.2 MacでCtrlキーがAppleキーにならない問題を修正 
 * 2007/09/22 version 1.6.3 MacでCtrl-Qが終了してしまうのでコンパイルのショートカットをCtrl-Eに変更 
 * 2007/10/03 version 1.6.4 Windowsでスペースが使えない問題を修正（「"」をつけなくてよかった） 
 * 2007/10/07 version 1.6.5 テンプレートにCVS（フォルダ）が表示されてしまっていて，選択するとエラーになってしまうのを修正 
 * 2007/10/08 version 1.6.6 ライブラリをblib.jarに変更．テンプレートをデフォルトパッケージののTurtle.javaを使うように変更 
 * 2007/10/08 version 1.6.7 ライブラリのバージョンアップ→blib101.jar 
 * 2007/12/14 version 1.7.0 ファイルコピー機能を追加
 * 2007/12/14 version 1.7.1 コンパイルに成功しないと，実行できないように変更（コンパイル時にクラスファイルを削除)
 * 2007/12/14 version 1.8.0 バイトコード学習機能の追加(Beta Windowsのみ) 
 * 2007/12/14 version 1.8.1 保存したときに，クラスファイルを削除するように仕様変更 
 * 2007/12/20 version 1.8.2 .jarファイル(Macで自動的に作成される)を読まないように修正, 一旦バイトコード学習機能を無効化 
 * 2007/12/21 version 1.8.3 コンパイラのエラーの出し方にあわせて，コンソールのタブのサイズを調整（コンパイルエラーの位置がずれないようにした） 
 * 2007/12/21 version 1.8.4 ライブラリのバージョンアップ→blib105.jar, japa.jar(version1)に変更，バイトコード学習機能有効化
 * 2007/12/26 version 1.8.5 コマンド実行時にそのコマンドがないとエラーを出力するように修正（javacのパスが通ってない場合への対処） 
 * 2008/11/11 version 1.8.6 ファイル読み込みで化ける場合があり，読み込み方法をJISAutoDetectに変更 
 * 2008/11/17 version 1.8.7 ファイル読み込みで化ける場合があり，書き込み方法をSJISに変更 
 * 2009/11/17 version 1.8.8 コンパイルごとにファイルをlogフォルダに保存する機能、logフォルダをzipでまとめる機能を追加（by turkey） 
 * 2010/01/06 version 1.8.9 MacOS10.5環境でコンパイル失敗するので、javacオプションに -encoding SJIS を追加（by turkey）
 * 2010/01/07 version 未定 コンソールで日本語入力すると、次からフォーカスが得られなくなるバグを修正（by turkey）
 * 2010/11/05 version 1.9.1 PRES機能追加
 * 2010/11/05 version 1.9.2 .から始まるテンプレートを読み込まないようにする
 * 2011/09/29 version 2.0.0 静岡大学情報学部対応版（ソースwarning修正のみ） 
 * 2011/10/10 version 2.1.0 BlockEditorを組み込み
 * 2011/10/10 version 2.1.1 batファイルを同梱（静大授業で，起動できない学生がいたため）
 * 2011/10/15 version 2.1.2 プロジェクト越しのファイルコピー機能追加
 * 2011/10/20 version 2.1.3 スマートブレース機能を削除
 * 							スマートインデント機能を追加
 * 							Formatアルゴリズムの変更（}}→}\n}にする）
 * 2011/10/22 version 2.1.4 メディアファイルがツリービューアから見られるようにする（実装は適当なので後で修正項目）
 * 2011/10/22 version 2.1.5 blibを120へ入替
 * 2011/10/23 version 2.1.6 Formatアルゴリズムの変更（{{→{\n{にする）
 * 							スマートインデントアルゴリズムを変更し、{はいくつあってもインデント進めは１つまで，}はインデント戻し無し．
 * 
 * 2011/12/18 version 2.1.7 ・BlockEditorがバージョンアップ（保井）
 * 							・kana/FlowViewer作成開始
 * 							・blibを128へ入替
 * 2012/01/24 version 2.1.8 ・.csvファイル見れるようにする．
 * 2012/02/04 version 2.1.9 ・FlowViewerの自動更新
 * 2012/09/27 version 2.2.0 ・BlockEditorのバージョンアップ
 * 							・GeneRefシステム組込
 * 							・Preference
 * 2012/09/27 version 2.2.1 ・Preferenceのバグ修正
 * 2012/09/28 version 2.2.2 ・blibを123->129
 * 							・updaterを組込
 * 2012/10/02 version 2.2.3 sakakibara
 * 							・GeneRefをバージョンアップ 
 * 							・compileとrun時にコマンド実行時間を表示させるよう変更
 * 2012/10/03 version 2.2.4 matsuzawa	・BlockEditor2.1.0
 * 							・BlockEditorの文字コード問題を解決 
 * 2012/10/03 version 2.2.5 matsuzawa	・updater.jarのバージョンを1.1.0に
 * 2012/10/03 version 2.2.6 matsuzawa	・BlockEditor2.1.1
 * 2012/10/03 version 2.2.7 matsuzawa	・BlockEditor2.1.3
 * 2012/10/03 version 2.2.8 matsuzawa	・BlockEditor2.1.4
 * 2012/10/03 version 2.2.9 matsuzawa	・BlockEditor2.1.5
 * 2012/10/03 version 2.2.10 sakakibara	・GeneRef1.0.4
 * 2012/10/03 version 2.2.11 matsuzawa	・BlockEditor2.1.6
 * 2012/10/04 version 2.2.12 sakakibara	・GeneRef1.0.5
 * 2012/10/04 version 2.2.13 sakakibara	・GeneRef1.0.6
 * 							・Preferenceでcancelしても状態が残ってしまう不具合を修正
 * 2012/10/09 version 2.2.14 matsuzawa	・BlockEditor2.1.9
 * 							・ロギング周りの修正（SourceEditor系，動作していなかった）
 * 2012/10/10 version 2.2.15 matsuzawa
 * 							・SourceEditor, SourceViewerの分離 リファクタリング
 * 2012/10/10 version 2.2.16 matsuzawa	・BlockEditor2.1.10
 * 2012/10/10 version 2.3.0 matsuzawa	・ロギングの追加，FORMAT_START, END, BLOCK_LOADING_START, END
 * 2012/10/10 version 2.3.1 matsuzawa	・BlockEditor2.1.11
 * 2012/10/14 version 2.3.2 sakakibara	・GeneRef1.0.7
 * 2012/10/14 version 2.3.3 sakakibara	・GeneRef1.0.8
 * 2012/10/15 version 2.3.4 sakakibara	・GeneRef1.0.9
 * 2012/10/16 version 2.4.0 matsuzawa ・BlockEditor 2.1.12
 * 									・Fontの変更が出来る．
 * 2012/10/16 version 2.4.1 matsuzawa Fontの変更, バグの修正
 * 2012/10/18 version 2.4.2 matsuzawa ・BlockEditor 2.2.0 重大なバグ修正
 * 									・Preference Windowタイトル
 * 									・Java Informationダイアログ
 * 2012/10/19 version 2.4.3 sakakibara	・GeneRef1.0.10
 * 2012/10/18 version 2.4.4 matsuzawa ・BlockEditor 2.3.0 SS
 * 2012/10/18 version 2.4.5 matsuzawa ・BlockEditor 2.3.1
 * 2012/10/18 version 2.4.6 matsuzawa ・BlockEditor 2.3.2
 * 2012/10/18 version 2.4.7 matsuzawa ・FlowViewer SS
 * 2012/10/18 version 2.5.0 matsuzawa ・コメント機能追加
 * 2012/10/22 version 2.5.1 sakakibara	・BEを開いた状態だとGeneRefが表示されないバグを修正
 * 									・BEを開いた時、開いた状態でファイルを変更した時、セーブをした時のコンパイル動作を非表示に変更
 * 2012/10/23 version 2.5.2 matsuzawa　・BE 2.5.0 -SS, BEのdirty状態を反映，他BEバグ修正		
 * 2012/10/24 version 2.5.3 sakakibara  ・コンパイル処理を通常とBE，GeneRef用に分割
 * 										・GeneRef 1.1.0
 * 2012/10/29 version 2.6.1 matsuzawa  ・BE 2.6.1
 * 2012/10/30 version 2.6.2 matsuzawa  ・BE 2.6.2
 *							sakakibara	・GeneRef1.1.1
 * 2012/11/1 version 2.7.0  matsuzawa  ・BE 2.7.0
 * 2012/11/4 version 2.8.0  matsuzawa  ・BE 2.8.0
 * 2012/11/6 version 2.9.0  matsuzawa  ・BE 2.9.0
 * 2012/11/6 version 2.9.1  matsuzawa  ・BE 2.9.1
 * 										・blib1.5.13 (SoundTurtleの仕様変更)
 * 2012/11/6 version 2.9.2  matsuzawa  ・BE 2.9.2
 * 2012/11/7 version 2.9.4  matsuzawa  ・BE 2.9.4
 * 										・blib1.5.14 (setShow()の追加など)
 * 2012/11/7 version 2.9.5  matsuzawa  ・BE 2.9.5
 * 2012/11/7 version 2.9.6  matsuzawa  ・BE 2.9.6
 * 2012/11/8 version 2.9.7  matsuzawa  ・BE 2.9.7
 * 2012/11/13 version 2.10.0  sakaki ・GeneRef 1.1.2 （重大なバグ修正）
 * 							matsuzawa  ・BE 2.10.0
 * 2012/11/13 version 2.10.2  matsuzawa  ・BE 2.10.2
 * 2012/11/14 version 2.10.3  matsuzawa  ・BE 2.10.3
 * 2012/11/14 version 2.10.4  matsuzawa  ・Format時のUndoがまとめてできない問題を修正
 * 2012/11/14 version 2.10.5  matsuzawa	・BE 2.10.5 
 * 										・CUI, Turtleでメニューが切り替わるように設定
 * 2012/11/14 version 2.10.7  matsuzawa	・BE 2.10.7 
 * 2012/11/14 version 2.10.9  matsuzawa	・BE 2.10.9 
 * 2012/11/14 version 2.10.10  matsuzawa	・BE 2.10.10
 * 2012/11/15 version 2.10.11  matsuzawa	・BE 2.10.11 授業中
 * 2012/11/15 version 2.10.12  matsuzawa	・BE 2.10.12 授業中
 * 2012/11/15 version 2.10.13  matsuzawa	・BE 2.10.13 授業中
 * 2012/11/23 version 2.10.14  matsuzawa	・BE 2.10.14
 * 											・Javaファイルからプロジェクトを選択するとFocusLostのログが正しくとれないバグを修正
 * 2012/11/23 version 2.10.16  matsuzawa	・BE 2.10.16
 * 2012/11/24 version 2.11.1  matsuzawa		・BE 2.11.1 関数
 * 											・Java->Block時の実行スレッドをThreadManager方式に変更
 * 2012/11/24 version 2.11.2  matsuzawa		・BE 2.11.2 関数
 * 2012/11/24 version 2.11.5  matsuzawa		・BE 2.11.5 関数
 * 2012/11/24 version 2.11.6  matsuzawa		・BE 2.11.6 関数 重要なバグ修正
 * 2012/11/24 version 2.11.7  matsuzawa		・BE 2.11.7 FlowViewerに対応．が見られるよう回帰テストでバグ取り
 * 2012/11/25 version 2.11.8  matsuzawa		・BE 2.11.8
 * 2012/11/29 version 2.11.9  sakakibara	・GeneRef 1.1.4
 * 2012/12/03 version 2.11.10 sakakibara	・古いJDKでBEが動かないバグを修正
 * 2012/12/04 version 2.11.11 matsuzawa		・Blockの引数の文言を修正
 * 											・doCompile2 RSErrorMessage が戻り値になっていたのを修正
 * 2012/12/27 version 2.12.1 hakamata		・NoviceDebugger(仮)追加　v1.0.0
 * 2012/12/27 version 2.12.2 hakamata		・NoviceDebugger(仮) v1.1.0
 * 2012/12/27 version 2.12.3 hakamata		・NoviceDebugger(仮) v1.2.0
 * 2013/01/06 version 2.12.4 hakamata		・NoviceDebugger(仮) v1.3.0, debuggerも未コンパイル時は実行できないように修正
 * 2013/01/07 version 2.12.5 hakamata		・NoviceDebugger(仮) v1.5.0
 * 2013/01/08 version 2.13.0 matsuzawa		・NoviceDebuggerを正式リリース v 0.1.0
 * 2013/01/09 version 2.13.1 matsuzawa		・BlockEditor 2.11.9
 * 2013/01/09 version 2.14.0 matsuzawa		・BlockEditor 2.12.0
 * 2013/01/09 version 2.15.0 matsuzawa		・BlockEditor 2.13.0
 * 											blib1514.jar -> blib1524.jar
 * 2013/01/09 version 2.15.1 matsuzawa		・BlockEditor 2.13.1
 * 2013/01/09 version 2.15.2 matsuzawa		・BlockEditor warpByTopLeft(), bgColor()タイル追加
 * 											・BlockEditor DebugRunボタン追加
 * 											・NoviceDebugger ログ記録
 * 2013/01/09 version 2.15.3 matsuzawa		・Java1.7が必要だったのを1.5以上に統一した．
 * 2013/01/09 version 2.15.4 matsuzawa		・ToolsのNoviceDebuggerが上がってしまうことがあるのを修正．
 * 2013/01/09 version 2.15.5 matsuzawa		・NoviceDebugger0.1.1 パッケージ名の変更．
 * 2013/01/10 version 2.15.6 matsuzawa		・NoviceDebugger0.1.2 Macに対応（パスの設定がWindows依存だった）
 * 2013/01/13 version 2.16.0 matsuzawa		・NoviceDebugger0.1.4
 * 											・ClassFileAnalyzerを利用してバイトコード機能を復活
 * 2013/01/15 version 2.16.1 matsuzawa		・blib1524.jar->blib1525.jar
 * 											・ButtonTurtle, InputTurtleのブロックに対応
 * 2013/01/15 version 2.16.2 matsuzawa		・ListTurtle#setCursor()のブロック不具合に対応
 * 2013/01/22 version 2.16.3 matsuzawa		・NovieDebuggerのバージョンアップ
 * 2013/01/23 version 2.16.4 matsuzawa		・NovieDebuggerのログを追加．
 * 2013/03/14 version 2.16.5 matsuzawa		・Fontの大きさ変更機能
 * 2013/04/13 version 2.16.6 matsuzawa		・mac snow leopardでのjavac文字化け問題を解消（javacのオプションで解決）
 * 											・MacでデフォルトのFontをOsakaにする．
 * 2013/04/13 version 2.16.7 matsuzawa		・mac snow leopardでのjavac文字化け問題を解消していなかった問題を解決
 * 											（jarファイルクリックで起動時，file.encodingがUS-ASCIIになってしまうのを強引に上書）
 * 2013/04/13 version 2.16.8 matsuzawa		・16.7でうまくいっていなかったので，解決  
 * 2013/04/18 version 2.16.9 matsuzawa		・macでのデバッガの文字化け．file.encodingを起動してからかえるのは無意味．
 * 											.command起動ファイル導入により根本的な解決を図る．  
 * 
 * 2013/09/26 version 2.17.0 hakamata		・DENO version0.2.0と統合
 * 
 * 2013/09/26 version 2.18.0 ohata			・BE version 2.14.0と結合
 * 
 * 2013/10/11 version 2.18.1 hakamata		・DENO version0.2.4と統合
 * 											・DENOのBreakpoint, 実行位置表示モードの切り替え, cont, Focusのログ書き出し 
 * 2013/10/16 version 2.19.0 matsuzawa		・上記新バージョンを統合したweek3用バージョン．
 * 2013/10/22 version 2.19.1 matsuzawa		・ファイルコピーの不具合修正
 * 											・BE スコープ判定機能
 * 											・DENO Blockエディタ版を削除
 * 2013/10/30 version 2.20.0 matsuzawa		・PPV組み込み
 * 											・BE ハイライトbugfix
 * 											・行番号のfontが追従しない問題を修正
 * 											・全角の「{」を変換しようとするとソース全体がおかしくなる問題を修正
 * 											・半角の{を入力したときに}を自動入力する機能を削除
 * 2013/12/4 version 2.21.0 matsuzawa		・詳細はgitログを参照のこと
 * 												・いくつかbugfix
 * 												・BlockEditor微調整
 * 												・全角スペース表示など
 * 2013/12/15 version 2.22.0 matsuzawa		・blibの更新　blib.jar 1.5.26
 * 											・Debuggerでupdate()が反映されるのが遅い問題を解決
 * 												・blibの更新 waitrepaintモード
 * 												・waitrepaint引数
 * 												・Turtleテンプレ変更 argsを引数とする
 * 2013/12/17 version 2.23.0 matsuzawa		・git参照のこと
 * 2013/12/17 version 2.23.1 matsuzawa		・git参照のこと 19日バージョン
 * 2013/12/19 version 2.24.0 matsuzawa		・git参照のこと Cocoviewer巻き戻し
 * 2014/01/08 version 2.25.0 matsuzawa		・git参照のこと
 * 
 * 2014/10/01 version 2.27.0 ohata			・2014プログラミング社会学科用
 * 2014/10/11 version 2.27.1 ohata			・軽微なバグを修正
 * 
<<<<<<< HEAD
 * 2014/10/01 version 2.27.2 ohata			・コンソールのフォントをエディタのフォントと統一
=======
 * 2014/10/18 version 2.27.2 ohata			・コンソールのフォントをエディタのフォントと統一
>>>>>>> ronpro_plugin_master
 * 											・フォントの文字幅によるエラー指摘メッセージのズレを修正
 * 2014/10/24 version 2.27.3 ohata			・sizeメソッドのBlock>>Java変換のエラーを修正
 * 											・Turtleを継承した自作クラスブロックを右クリックしたときのコンテキストメニューに，タートルメニューを追加
 * 											・List,Image,TextTurtleなどのメソッド呼び出しブロックを隠蔽
 * 2014/10/24 version 2.27.4 ohata			・コンテキストメニュー変更
 * 2014/10/24 version 2.27.5 ohata			・メソッドコール矢印の描画処理を修正
 * 											・Block>>Javaのエラーを修正 
 * 2014/10/24 version 2.27.6 ohata			・メソッドコール矢印の修正,テスト
 * 											・参照ブロックのハイライト処理を修正
 * ＜懸案事項＞
 * ・doCompile2()の設計が冗長なので再設計すること．
 * ・"}"を押したときのスマートインデント
 * ・manager群のinitializeの位置，割れ窓修正すること
 *　・フォルダをクリックしたとき，下記のエラーがでている．
 *at ronproeditor.REApplication.writePresLog(REApplication.java:647)
 *at ronproeditor.views.RESourceEditor$2.focusLost(RESourceEditor.java:93)
 */
public class REApplication implements ICFwApplication {

	/***********************
	 * Static Variables.
	 ***********************/

	// Application's Information.
	public static final String APP_NAME = "Ronpro Editor";
	public static final String VERSION = "2.27.6";
	public static final String BUILD_DATE = "2014/11/10";
	public static final String DEVELOPERS = "Yoshiaki Matsuzawa & CreW Project & Sakai Lab";
	public static final String COPYRIGHT = "Copyright(c) 2007-2014 Yoshiaki Matsuzawa & CreW Project & Sakai Lab. All Rights Reserved.";

	public static final String SRC_ENCODING = "SJIS";
	// public static final String SRC_ENCODING = "UTF-8"; // for test
	public static final boolean COMMENT = true;
	public static final String COMMENT_FILE = ".comment.txt";

	public static final String FILE_EXTENSION = "java";
	public static final String RUNNABLE_EXTENSION = "class";
	public static final String DEFAULT_ROOT = "MyProjects";
	public static final String TEMPLATE_FOLDER = "templates";
	public static final String LIB_FOLDER = "lib";
	public static final String EXTENSION_FOLDER = "ext";
	public static final String TRASH_FOLDER = ".Trash";

	public static final int WHITESPACE_COUNT_FOR_TAB = 2;

	/***********************
	 * Main Method
	 ***********************/

	public static void main(String args[]) {
		REApplication application = new REApplication();
		application.main();
	}

	/***********************
	 * Variables.
	 ***********************/

	private String compileCommand;
	private String runCommand;

	private RESourceManager sourceManager = new RESourceManager();
	private RELibraryManager libraryManager = new RELibraryManager(LIB_FOLDER);
	private RESourceTemplateManager templateManager = new RESourceTemplateManager(
			TEMPLATE_FOLDER);
	private CPreferenceManager preferenceManager;

	private REFrame frame;

	private RECreateFileNameDialogWithType createFileDialog;
	private RECreateProjectNameDialog createProjectDialog;
	private REDirtyOptionDialog dirtyOptionDialog;
	private RERefactoringProjectNameDialog refactorProjectNameDialog;
	private RERefactoringFileNameDialog refactorFileNameDialog;
	private RECreateFileNameDialogForCopy copyFileNameDialog;

	private PresProjectManager presManager;
	private REBlockEditorManager blockManager;
	private REFlowViewerManager flowManager;
	private REGeneRefManager generefManager;
	private REPresVisualizerManager ppvManager;
	private RECheCoProManager checoproManager; // CheCoPro(kato)
	private GUI deno;
	private RECreateCocoDataManager createCocoDataManager;
	private RECocoViewerManager cocoViewerManager;

	/***********************
	 * Construct & Start
	 ***********************/

	private void main() {
		initializeLookAndFeel();
		initializeCommands();
		initializeAndOpen(DEFAULT_ROOT);
	}

	private void initializeAndOpen(String rootDirName) {
		prepareRootDirectory(rootDirName);
		createAndOpenWindow();
		prepareDialogs();

		presManager = new PresProjectManager();
		presManager.initialize();
		blockManager = new REBlockEditorManager(this);
		flowManager = new REFlowViewerManager(this);
		generefManager = new REGeneRefManager(this);
		ppvManager = new REPresVisualizerManager(this);
		createCocoDataManager = new RECreateCocoDataManager(this);
		cocoViewerManager = new RECocoViewerManager(this);
		checoproManager = new RECheCoProManager(this);

		this.sourceManager.setFileFilter(CFileFilter.ACCEPT_BY_NAME_FILTER(
				"*.java", "*.hcp", "*.c", "*.cpp", "Makefile", "*.oil", "*.rb",
				"*.bat", "*.tex", "*.jpg", "*.gif", "*.png", "*.wav", "*.mp3",
				"*.csv"));
		// this.sourceManager.setDirFilter(CFileFilter.IGNORE_BY_NAME_FILTER(".*",
		// "CVS", "bin"));
		// @TODO きちんと実装すること 2011/11/22
	}

	private void initializeLookAndFeel() {
		if (CJavaSystem.getInstance().isWindows()) {
			try {
				UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void initializeCommands() {
		if (CJavaSystem.getInstance().hasCommand("java")) {
			this.runCommand = "java";
		} else {
			JOptionPane.showMessageDialog(frame, "javaコマンドが見つかりません",
					"起動時チェックにひっかかりました", JOptionPane.ERROR_MESSAGE);
			// System.exit(0);
		}

		this.compileCommand = CJavaSystem.getInstance().getJavacCommand();
		if (this.compileCommand == null) {
			JOptionPane.showMessageDialog(frame, "javacコマンドが見つかりません",
					"起動時チェックに引っかかりました", JOptionPane.ERROR_MESSAGE);
			// System.exit(0);
		}
	}

	private void prepareDialogs() {
		createProjectDialog = new RECreateProjectNameDialog(this);
		createFileDialog = new RECreateFileNameDialogWithType(this);
		dirtyOptionDialog = new REDirtyOptionDialog(this);
		refactorProjectNameDialog = new RERefactoringProjectNameDialog(this);
		refactorFileNameDialog = new RERefactoringFileNameDialog(this);
		copyFileNameDialog = new RECreateFileNameDialogForCopy(this);
		copyFileNameDialog.setTitle("ファイル（クラス）のコピー");
	}

	private void prepareRootDirectory(String rootDirName) {
		File root = new File(rootDirName);
		if (!root.exists()) {
			root.mkdir();
		}
		sourceManager.setRootDirectory(root);

		sourceManager.setFileFilter(CFileFilter
				.ACCEPT_BY_EXTENSION_FILTER("java"));
		sourceManager.setDirFilter(CFileFilter.IGNORE_BY_NAME_FILTER(".*"));

		CFile preferenceFile = CFileSystem.findDirectory(DEFAULT_ROOT)
				.findOrCreateFile(".pref/preference");
		preferenceManager = new CPreferenceManager(preferenceFile);
	}

	private void createAndOpenWindow() {
		frame = new REFrame(this);
		frame.initialize();
		frame.setVisible(true);
	}

	/***********************
	 * Getters
	 ***********************/

	public RESourceManager getSourceManager() {
		return sourceManager;
	}

	public RESourceTemplateManager getTemplateManager() {
		return templateManager;
	}

	public REFrame getFrame() {
		return frame;
	}

	public RELibraryManager getLibraryManager() {
		return libraryManager;
	}

	// public PresProjectManager getPresManager() {
	// return presManager;
	// }

	public CPreferenceManager getPreferenceManager() {
		return preferenceManager;
	}

	/***********************
	 * Application Method
	 ***********************/

	public void doCreateProject() {
		doClose();

		createProjectDialog.open();
		if (createProjectDialog.getState() == RECreateNameDialog.State.INPUTTED) {
			getSourceManager().createProject(
					createProjectDialog.getInputtedName());
		}
	}

	public void doCreateFile() {
		doClose();

		if (getSourceManager().getProjectDirectory() == null) {
			JOptionPane.showMessageDialog(frame, "プロジェクトが選択されていません",
					"ファイル（クラス）を作れません", JOptionPane.ERROR_MESSAGE);
			return;
		}

		createFileDialog.open();
		if (createFileDialog.getState() == RECreateNameDialog.State.INPUTTED) {
			getSourceManager().createFile(createFileDialog.getInputtedName(),
					createFileDialog.getSelectedTemplate());
		}
	}

	public void doOpen(File file) {
		doClose();

		if (file.getName().endsWith("java")) {// @TODO きちんと実装すること 2011/11/22
			getSourceManager().open(file);
		}
	}

	public void doSave() {
		if (getSourceManager().hasCurrentFile()) {
			getFrame().getEditor().doSave();

			blockManager.doCompileBlock(); // 要：ファイル削除の前に実行

			deleteRunnable(getSourceManager().getCurrentFile());

			// TODO
			writePresLog(PRCommandLog.SubType.SAVE);// TODO

			// blockManager.doRefleshBlock();
			flowManager.refreshChart();

		}
	}

	public void doBlockToJavaSave() {
		if (getSourceManager().hasCurrentFile()) {
			getFrame().getEditor().doSave();
			deleteRunnable(getSourceManager().getCurrentFile());

			// TODO
			writePresLog(PRCommandLog.SubType.SAVE);// TODO

			// TODO 上と重複
			flowManager.refreshChart();
		}
	}

	public void doRefactoring() {
		if (getSourceManager().hasCurrentFile()) {
			doRefactorFileName();
		} else {
			doRefactorProjectName();
		}
	}

	private void doRefactorProjectName() {
		if (getSourceManager().getProjectDirectory() == null) {
			JOptionPane.showMessageDialog(frame, "プロジェクトが選択されていません",
					"プロジェクト名を変更できません", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			JOptionPane.showMessageDialog(frame, "ソースがセーブされていません",
					"プロジェクト名を変更できません", JOptionPane.ERROR_MESSAGE);
			return;
		}

		doClose();

		refactorProjectNameDialog
				.open(getSourceManager().getProjectDirectory());
		if (refactorProjectNameDialog.getState() == RECreateNameDialog.State.INPUTTED) {
			getSourceManager().refactorProjectName(
					refactorProjectNameDialog.getInputtedName());
		}
	}

	private void doRefactorFileName() {
		if (!getSourceManager().hasCurrentFile()) {
			JOptionPane.showMessageDialog(frame, "ソースが選択されていません",
					"ファイル名を変更できません", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			JOptionPane.showMessageDialog(frame, "ソースがセーブされていません",
					"ファイル名を変更できません", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File file = getSourceManager().getCurrentFile();

		doClose();

		refactorFileNameDialog.open(file);
		if (refactorFileNameDialog.getState() == RECreateNameDialog.State.INPUTTED) {
			getSourceManager().refactorFileName(file,
					refactorFileNameDialog.getInputtedName());
		}
	}

	public void doFileCopy() {
		if (!getSourceManager().hasCurrentFile()) {
			JOptionPane.showMessageDialog(frame, "ソースが選択されていません",
					"ファイルをコピーできません", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			JOptionPane.showMessageDialog(frame, "ソースがセーブされていません",
					"ファイルをコピーできません", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File file = getSourceManager().getCurrentFile();

		doClose();

		File recommendedFile = new File(file.getParentFile(), "CopyOf"
				+ FileSystemUtil.cutExtension(file));

		copyFileNameDialog.open(recommendedFile);
		if (copyFileNameDialog.getState() == RECreateNameDialog.State.INPUTTED) {
			getSourceManager().copyFile(file,
					copyFileNameDialog.getInputtedProject(),
					copyFileNameDialog.getInputtedName());
		}
	}

	public void doDelete() {
		if (getSourceManager().hasCurrentFile()) {
			doDeleteFile();
		} else {
			doDeleteProject();
		}
	}

	private void doDeleteProject() {
		if (getSourceManager().getProjectDirectory() == null) {
			JOptionPane.showMessageDialog(frame, "プロジェクトが選択されていません",
					"プロジェクトを削除できません", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int res = JOptionPane.showConfirmDialog(frame, "本当に"
				+ getSourceManager().getProjectDirectory().getName()
				+ "を削除してよいですか？ 以下のファイルもすべて削除されます", "最終確認",
				JOptionPane.WARNING_MESSAGE);
		if (res == JOptionPane.OK_OPTION) {
			doClose();
			File file = getSourceManager().getProjectDirectory();
			file.renameTo(new File(getSourceManager().makeTrashFolder(), file
					.getName()));
			getSourceManager().fireRefreshedEvent();
		}
	}

	private void doDeleteFile() {
		if (!getSourceManager().hasCurrentFile()) {
			JOptionPane.showMessageDialog(frame, "ソースが選択されていません",
					"ファイルを削除できません", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File file = getSourceManager().getCurrentFile();

		int res = JOptionPane.showConfirmDialog(frame, "本当に" + file.getName()
				+ "を削除してよいですか？", "最終確認", JOptionPane.WARNING_MESSAGE);
		if (res == JOptionPane.OK_OPTION) {
			doClose();
			file.renameTo(new File(getSourceManager().makeTrashFolder(), file
					.getName()));
			getSourceManager().fireRefreshedEvent();
		}
	}

	public void doClose() {
		if (getSourceManager().hasCurrentFile()) {
			dirtyCheck();
			getSourceManager().close();
		}
	}

	private void dirtyCheck() {
		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			dirtyOptionDialog.open();
		}
	}

	public void doSetProjectDirectory(File file) {
		doClose();

		getSourceManager().setProjectDirectory(file);
	}

	public void doRefresh() {
		doClose();
		getSourceManager().fireRefreshedEvent();
	}

	public void doRefreshCurrentEditor() {
		RESourceEditor editor = frame.getEditor();
		if (editor != null) {
			editor.refresh();
		}
	}

	public void doExit() {
		try {
			dirtyCheck();
			presManager.terminate();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	// public void doMakeLogArchive() {
	// try {
	// String zipName = "log_"
	// + ronproeditor.helpers.TimeUtil.getDateString() + ".zip";
	// ronproeditor.helpers.ZipUtil.createZip(zipName, new String[] {
	// "log", "MyProjects" });
	// } catch (IOException e) {
	// e.printStackTrace(frame.getConsole().err);
	// frame.getConsole().setText(
	// frame.getConsole().getText() + "申し訳ありませんがエラーが発生しました。\n");
	// frame.getConsole().setText(
	// frame.getConsole().getText() + "開発者まで御連絡下さい。\n");
	// }
	// }

	// turkeyのコード
	// public void doSaveCompileLog() {
	//
	// File logRoot = new File("log");
	// if (logRoot.exists() == false)
	// logRoot.mkdir();
	//
	// File project = new File(logRoot, sourceManager.getProjectDirectory()
	// .getName());
	// if (project.exists() == false)
	// project.mkdir();
	//
	// String name = getSourceManager().getCurrentFile().getName();
	// File file = new File(project, name + "."
	// + ronproeditor.helpers.TimeUtil.getNowString());
	// FileSystemUtil.save(file, getFrame().getEditor().getText());
	//
	// }

	public void doCompile() {
		doCompileNonBlocking();
	}

	// public void doCompileBlocking() {
	// doCompile(true);
	// }

	public void doCompileNonBlocking() {
		doCompile(false);
	}

	/**
	 * コンパイルをします
	 * 
	 * @param blocking
	 *            コンパイルをブロッキングするか
	 */
	private void doCompile(boolean blocking) {
		// doSaveCompileLog(); turkeyのコード

		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			JOptionPane.showMessageDialog(frame, "ソースがセーブされていません",
					"コンパイルできません", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File target = getSourceManager().getCurrentFile();
		if (hasRunnableFile(target)) {
			deleteRunnable(target);
		}

		frame.getConsole().setText("");

		JavaEnv env = FileSystemUtil.createJavaEnv(getSourceManager()
				.getRootDirectory(), getSourceManager().getCurrentFile());
		String cp = libraryManager.getLibString();

		ArrayList<String> commands = new ArrayList<String>();
		commands.add(compileCommand);
		if (CJavaSystem.getInstance().isMac()) {
			commands.add("-J-Dfile.encoding="
					+ RECommandExecuter.commandEncoding);
		}
		commands.add("-g");
		commands.add("-encoding");
		commands.add(REApplication.SRC_ENCODING);
		commands.add("-classpath");
		commands.add(cp);
		commands.add(env.source);

		writePresLog(PRCommandLog.SubType.COMPILE);// TODO
													// BlockEditorの時もコンパイルログが記録されてしまう．

		// if (blocking) {
		// // （BlockEditorのためのコンパイル）　ブロッキングする．
		// try {
		// CommandExecuter.executeCommandWait(commands, env.dir,
		// frame.getConsole());
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// } else {
		// // 通常コンパイル．　ブロッキングしない．
		// CommandExecuter.executeCommand(commands, env.dir,
		// frame.getConsole());
		// }

		RECommandExecuter.executeCommand(commands, env.dir, frame.getConsole(), frame.getConsole().getFontMetrics(frame.getConsole().getFont()));

		generefManager.handleCompileDone();
	}

	/*
	 * BlockEditorとGeneRefのためのコンパイル処理 2012.12.04 この設計は仮なので再設計せよ
	 * 
	 * @return
	 */
	public String doCompile2(boolean verbose) {
		JavaEnv env = FileSystemUtil.createJavaEnv(
				sourceManager.getRootDirectory(),
				sourceManager.getCurrentFile());
		String cp = libraryManager.getLibString();

		ArrayList<String> commands = new ArrayList<String>();
		commands.add(compileCommand);
		if (CJavaSystem.getInstance().isMac()) {
			commands.add("-J-Dfile.encoding="
					+ RECommandExecuter.commandEncoding);
		}
		commands.add("-g");
		if (verbose) {
			commands.add("-verbose");
		}
		commands.add("-encoding");
		commands.add(REApplication.SRC_ENCODING);
		commands.add("-classpath");
		commands.add(cp);
		commands.add(env.source);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DummyConsole console = new DummyConsole();
		console.setErr(new PrintStream(out));

		try {
			RECommandExecuter.executeCommandWait(commands, env.dir, console, getFrame().getConsole().getFontMetrics(getFrame().getConsole().getFont()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out.toString();
	}

	private CFile lastFile;

	// TODO
	public void writePresLog(PRCommandLog.SubType subType, Object... args) {
		try {
			CFile file = getSourceManager().getCCurrentFile();
			if (file == null) {
				file = lastFile;
			} else {
				lastFile = file;
			}
			CPath path = file.getRelativePath(getSourceManager()
					.getCCurrentProject());
			PRLog log = new PRCommandLog(subType, path, args);
			writePresLog(log);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void writePresTextEditLog(PRTextEditLog.SubType subType, int offset,
			int len, String text) {
		try {
			CPath path = getSourceManager().getCCurrentFile().getRelativePath(
					getSourceManager().getCCurrentProject());
			PRLog log = new PRTextEditLog(subType, path, offset, len, text);
			writePresLog(log);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void writePresLog(PRLog log) {
		try {
			presManager.getRecordingProject(
					getSourceManager().getCCurrentProject()).record(log);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void doRun() {
		File target = getSourceManager().getCurrentFile();
		if (!hasRunnableFile(target)) {
			JOptionPane.showMessageDialog(frame, "コンパイルに成功していません", "実行できません",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JavaEnv env = FileSystemUtil.createJavaEnv(getSourceManager()
				.getRootDirectory(), getSourceManager().getCurrentFile());
		String cp = libraryManager.getLibString();
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(runCommand);
		commands.add("-classpath");
		commands.add(cp);
		commands.add(env.runnable);
		
		RECommandExecuter.executeCommand(commands, env.dir, frame.getConsole(), frame.getConsole().getFontMetrics(frame.getConsole().getFont()));
		writePresLog(PRCommandLog.SubType.START_RUN);// TODO
	}

	public void doDebugRun() {

		File target = getSourceManager().getCurrentFile();
		if (!hasRunnableFile(target)) {
			JOptionPane.showMessageDialog(frame, "コンパイルに成功していません", "実行できません",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (deno != null && deno.isRunning()) {
			JOptionPane.showMessageDialog(frame, "前のデバッグ画面が開きっぱなしです",
					"実行できません", JOptionPane.ERROR_MESSAGE);
			return;
			// CFrameUtils.toFront(deno.getFrame());
			// return;
		}

		// パス等取得
		JavaEnv env = FileSystemUtil.createJavaEnv(getSourceManager()
				.getRootDirectory(), getSourceManager().getCurrentFile());
		String args[] = new String[6];
		// ソースパス
		args[0] = "-sourcepath";
		args[1] = env.dir.getAbsolutePath();
		// クラスパス
		args[2] = "-classpath";
		String libString = libraryManager.getLibString();
		libString = env.dir.getAbsolutePath() + FileSystemUtil.PATH_SEPARATOR
				+ libString;
		if (CJavaSystem.getInstance().isWindows()) {
			libString = "\"" + libString + "\"";
		}
		args[3] = libString;
		// クラス名
		args[4] = env.runnable;
		// waitrepaint
		args[5] = "waitrepaint";

		// xml
		// String[] libs = getLibraryManager().getLibsAsArray();
		// try {
		// new JavaToBlockMain().run(getSourceManager().getCurrentFile(),
		// REApplication.SRC_ENCODING, libs);
		// } catch (Exception e) {
		// e.printStackTrace();
		// CErrorDialog.show(getFrame(), "Block変換時のエラー", e);
		// }

		NDebuggerManager.registerListener(new NDebuggerListener() {
			public void stepPressed() {
				writePresLog(PRCommandLog.SubType.STEP);
			}

			public void debugStarted() {
				writePresLog(PRCommandLog.SubType.START_DEBUG);
			}

			public void debugFinished() {
				writePresLog(PRCommandLog.SubType.STOP_DEBUG);
			}

			public void playPressed() {
				writePresLog(PRCommandLog.SubType.DEBUG_PLAY);
			}

			public void stopPressed() {
				writePresLog(PRCommandLog.SubType.DEBUG_STOP);
			}

			public void speedSet(int speed) {
				writePresLog(PRCommandLog.SubType.DEBUG_SPEED, speed);
			}

			public void contPressed() {
				writePresLog(PRCommandLog.SubType.DEBUG_CONT);
			}

			public void breakpointSet() {
				writePresLog(PRCommandLog.SubType.DEBUG_BPSET);
			}

			public void breakpointClear() {
				writePresLog(PRCommandLog.SubType.DEBUG_BPCLR);
			}

			public void changeAPMode(String mode) {
				writePresLog(PRCommandLog.SubType.DEBUG_CHANGEMODE, mode);
			}
		});
		deno = new GUI();
		deno.run(args);
		deno.getFrame().addWindowFocusListener(new WindowFocusListener() {
			public void windowLostFocus(WindowEvent e) {
				writePresLog(PRCommandLog.SubType.FOCUS_LOST, "DENO");
			}

			public void windowGainedFocus(WindowEvent e) {
				writePresLog(PRCommandLog.SubType.FOCUS_GAINED, "DENO");
			}
		});
		CommandInterpreter cmdint = new CommandInterpreter(deno.getEnv());
		// deno.getEnv().setBlockEditor(blockManager.getBlockEditor());
		// if(blockManager.getBlockEditor() != null) {
		// deno.beMode();
		// }
		cmdint.executeCommand("run");
	}

	// Helper
	public boolean hasRunnableFile(File source) {
		if (source == null) {
			return false;
		}
		File file = getRunnableFile(source);
		return file != null && file.exists();
	}

	public boolean deleteRunnable(File source) {
		if (hasRunnableFile(source)) {
			return getRunnableFile(source).delete();
		}
		return false;
	}

	private File getRunnableFile(File source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		File dir = source.getParentFile();
		File runnableFile = new File(dir, FileSystemUtil.cutExtension(source)
				+ "." + RUNNABLE_EXTENSION);
		return runnableFile;
	}

	// Java -version
	// CommandExecuter.executeCommandWait("java -version",
	// getSourceManager().getProjectDirectory(), frame
	// .getConsole());

	public void doKillAll() {
		RECommandExecuter.killAll();
	}

	public void doFormat() {
		getFrame().getEditor().format();
	}

	public void doShowBytecode() {
		if (!getSourceManager().hasCurrentFile()) {
			JOptionPane.showMessageDialog(frame, "ソースが選択されていません",
					"バイトコードを閲覧できません", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			JOptionPane.showMessageDialog(frame, "ソースがセーブされていません",
					"バイトコードを閲覧できません", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File target = getSourceManager().getCurrentFile();
		if (!hasRunnableFile(target)) {
			JOptionPane.showMessageDialog(frame, "コンパイルに成功していません",
					"バイトコードを閲覧できません", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			File classFile = getRunnableFile(target);
			String className = FileSystemUtil.cutExtension(classFile);
			REBytecodeViewerGenerator.showViewer(classFile, className, frame);
		} catch (Exception ex) {
			CErrorDialog.show(getFrame(), "Handle Exception", ex);
			return;
		}
	}

	/****************************
	 * 一時の繋ぎ
	 ****************************/

	public void doOpen(CFileElement file) {
		this.doOpen(file.toJavaFile());
	}

	public void doSetProjectDirectory(CDirectory directory) {
		if (directory == null) {
			this.doSetProjectDirectory((File) null);
		} else {
			this.doSetProjectDirectory(directory.toJavaFile());
		}
	}

	public ICFwResourceRepository getResourceRepository() {
		return sourceManager;
	}

	private JFileChooser chooser = new JFileChooser();

	public void doExport() {
		try {
			CDirectory project = getSourceManager().getCCurrentProject();
			if (project == null) {
				JOptionPane.showMessageDialog(frame, "プロジェクトが選択されていません",
						"Exportできません", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!COMMENT) {// CONFIRM ONLY
				int res = 0;
				res = JOptionPane.showConfirmDialog(frame,
						"「" + project.getName() + "」" + "をExportします．よろしいですね？",
						"プロジェクト名確認", JOptionPane.OK_CANCEL_OPTION);
				if (res != JFileChooser.APPROVE_OPTION) {
					return;
				}
			} else {// COMMENT
				RECommentInputDialog input = new RECommentInputDialog(project);
				int res = 0;
				res = JOptionPane.showConfirmDialog(frame, input,
						"プロジェクト名確認とコメント入力", JOptionPane.OK_CANCEL_OPTION);
				input.save();
				if (res != JFileChooser.APPROVE_OPTION) {
					return;
				}
			}

			// datファイルのコピー
			copyDatFileToProject();

			chooser.setSelectedFile(new File(CFileSystem.getExecuteDirectory()
					.getAbsolutePath() + "/" + project.getName() + ".zip"));
			int res = chooser.showSaveDialog(getFrame());
			if (res != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File f = chooser.getSelectedFile();
			CFilename name = new CFilename(f.getName());
			name.setExtension("zip");
			CDirectory dir = CFileSystem.findDirectory(f.getParentFile()
					.getAbsolutePath());
			CFile zip = dir.findOrCreateFile(name);
			NewZipUtil.createZip(zip, project, project);

			JOptionPane
					.showConfirmDialog(frame, name.toString()
							+ "としてzipファイルをExportしました．", "成功しました",
							JOptionPane.OK_OPTION);

		} catch (Exception ex) {
			ex.printStackTrace(frame.getConsole().getErr());
			CErrorDialog.show(frame, "Export中にエラーが発生しました．", ex);
			// JOptionPane.showConfirmDialog(frame, "Export中にエラーが発生しました．",
			// "エラー",
			// JOptionPane.ERROR_MESSAGE);
		} finally {
			// コピーしたdatファイルを削除
			deleteDatFileFromProject();
		}
	}

	public void doOpenPreferencePage() {
		this.preferenceManager.openPreferenceFrame();
	}

	public void doOpenBlockEditor() {
		blockManager.doOpenBlockEditor();
		// 20130926 DENOがBEを直接参照する　暫定対応
		if (deno != null && deno.isRunning()) {
			deno.getEnv().setBlockEditor(blockManager.getBlockEditor());
		}
	}

	public void doOpenFlowViewer() {
		flowManager.doOpenFlowViewer();
	}

	public void doOpenGeneRefBrowser() {
		generefManager.openGeneRefBrowser();
	}

	// CheCoPro(kato)
	public void doStartCheCoPro() {
		checoproManager.startCheCoPro();
	}

	private void copyDatFileToProject() {
		try {
			// TODO 応急処置　macだとNullPointerExceptionが出る
			generefManager.copyDatFileToProject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteDatFileFromProject() {
		try {
			generefManager.deleteDatFileFromProject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doOpenPPV() {
		try {
			ppvManager.openPresVisualizer();
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, "OpenPPV中にエラーが発生しました．", ex);
		}
	}

	public void doCreateCocoData() {
		try {
			createCocoDataManager.createCocoData();
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, "Create CocoData中にエラーが発生しました．", ex);
		}
	}

	public void doOpenCocoViewer() {
		try {
			cocoViewerManager.openCocoViewer(createCocoDataManager
					.getPPProjectSet());
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, "Open CocoViewer中にエラーが発生しました．", ex);
		}
	}

	public void doOpenClearCash() {
		try {
			ppvManager.clearCash();
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, "Clear Cash中にエラーが発生しました．", ex);
		}
	}

	public REApplication doOpenNewRE(String dirPath) {
		REApplication application = new REApplication();
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		application.initializeAndOpen(dirPath);
		// 返り値追加（kato）
		return application;
	}

	// private void sourceColoringTest(){
	// try {
	// List<JTextPane> panes = new ArrayList<JTextPane>();
	// DefaultStyledDocument doc = (DefaultStyledDocument) (getFrame()
	// .getEditor().getViewer().getTextPane().getDocument());
	// int len = doc.getLength();
	// String text = doc.getText(0, len);
	// int start = 0;
	// for (int i = 0; i < len; i++) {
	// char c = text.charAt(i);
	// if (c == '\n') {
	// int end = i;
	// DefaultStyledDocument copy = CCopyUtil.copyDeep(doc);
	// copy.remove(end, len - end);// 順番重要
	// copy.remove(0, start);// 順番重要
	// JTextPane pane = new JTextPane(copy);
	// panes.add(pane);
	// start = i + 1;
	// }
	// }
	//
	// JFrame f = new JFrame();
	// f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	// f.setVisible(true);
	// JList<JTextPane> list = new JList<JTextPane>(new Vector<JTextPane>(
	// panes));
	// list.setCellRenderer(new ListCellRenderer<JTextPane>() {
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * javax.swing.ListCellRenderer#getListCellRendererComponent
	// * (javax.swing.JList, java.lang.Object, int, boolean, boolean)
	// */
	// public Component getListCellRendererComponent(
	// JList<? extends JTextPane> list, JTextPane value,
	// int index, boolean isSelected, boolean cellHasFocus) {
	// return value;
	// }
	// });
	// f.getContentPane().add(list);
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

}
