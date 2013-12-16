/*
 * PRRecorder.java
 * Created on 2010/02/12 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.testing;

import java.io.File;

/**
 * PRRecorder
 * 
 * ＜機能＞
 * ・メインクラス
 * ・レベル1操作，プロジェクト毎のディレクトリセッティング
 * ・レベル2操作，基本操作履歴
 * 
 * ＜操作レベル＞
 * ・レベル１，ファイル操作：ファイルの保存，作成，削除
 * ・レベル２，Java操作：コンパイル，実行，
 * ・レベル３，開発環境：開発環境の起動と終了，編集操作（挿入，削除，カット，コピー，ペースト，Undo），リファクタリング（作成と削除）
 */
public class PRRecorder {

  public static final String RECORDING_DIR_NAME = ".pres";

  // private String recordingDirName = RECORDING_DIR_NAME;

  public void start() {
    System.out.println("start!");
  }

  public void stop() {
    System.out.println("stop!");
  }

  public void focusGain() {
    System.out.println("Focus Gain!");
  }

  public void focusLost() {
    System.out.println("Focus Lost!");
  }

  public void refactoring(File src, File dst) {
    System.out.println("Refactoring!");
  }

  public void compile(File file) {
    System.out.println("Compile!");
  }

  public void run(File file) {
    System.out.println("Run!");
  }

  public void save(File file) {
    System.out.println("Save!");
    System.out.println(file.getPath());
  }
}
