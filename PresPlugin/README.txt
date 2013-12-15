2012/06/21 3.7.2
	ファイル作成，削除時に記録をするように変更

2012/04/05 3.7.0
 	macchan Eclipse3.7 Indigo に対応
 	Eclipse RCPテスト環境の挙動がおかしく，-XX:MaxPermSize=512mが必要．
 	3.6と同様のコードで動作確認．
 	ファイル編集ログをDocumentListener経由に変更 (PresExtendedJavaEditor)
 	これにより，PREclipseTextEditLogではなく，PRTextEditLogを吐くように仕様変更
 	version番号の振り方をEclipse対応に合わせる
 		 
2010/05/17 3.6.0
	3.6(Helios)に対応
	1.2.1は3.6では動かなかった．（Undoable）
2010/04/XX 1.2.1
	2010年度最終版 （3.5以下に対応）
2010/04/13 1.1.0
	大幅なPres仕様変更に対応
	プロジェクトを削除し，新たに同名のプロジェクトを作成した場合に（一旦オブジェクトを消去，例外が出ているがひとまずキャッチしてしのいでいる）
	Export時にプロジェクトをrefreshする機能を追加（これでログも含めてExportされる）
2010/04/12 1.0.7
	Save時にCompileするように修正
2010/04/12 1.0.6
	Pleiadesで"Typing"が"入力"に変わってしまい，テキストレコードが出来なくなる問題を修正