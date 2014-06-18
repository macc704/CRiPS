/*
 * Created on 2007/06/13
 *
 * Copyright (c) 2007 camei.  All rights reserved.
 */
package obpro.plugin.common;

import java.net.URL;

import obpro.plugin.ObproPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * オブプロ用Javaクラス生成用ウィザード
 * 
 * @author camei
 * @version $Id: NewObproClassWizardPage.java,v 1.1 2007/06/13 19:16:01 camei
 *          Exp $
 */
public abstract class NewObproClassWizardPage extends NewClassWizardPage {

	public static final String TEMPLATE_COMMENT = "template/comment";

	public static final String PROJECT_ERROR_MSG = "オブプロプロジェクトでないので，クラスを作成できません．プロジェクトを作り直してください";

	protected StatusInfo isObproProject = new StatusInfo();

	/**
	 * @return 本文テンプレートのURL
	 */
	protected abstract String getTemplatePath();

	/**
	 * @return インポート文
	 */
	protected abstract String getImportText();

	/**
	 * @return クラスコメント
	 */
	protected String getTypeComment() {
		Template template = new Template();
		URL commentURL = ObproPlugin.getDefault().getURL(TEMPLATE_COMMENT);
		String comment = template.generate(commentURL);
		return comment;
	}

	/**
	 * Fileコメントは吐かないようにオーバーライド
	 */
	protected String getFileComment(ICompilationUnit parentCU,
			String lineDelimiter) throws CoreException {
		return null;
	}

	/**
	 * Typeコメントをオブプロ形式にオーバーライド
	 */
	protected String getTypeComment(ICompilationUnit parentCU,
			String lineDelimiter) {
		return getImportText() + lineDelimiter + getTypeComment();
	}

	/**
	 * クラスのメンバを作成 中身（コンストラクタ、main、フィールド）はここで全部作ってしまうことにする。
	 */
	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		Template template = new Template();

		// 変数値の設定
		String className = type.getElementName();
		template.setVariable("className", className);
		template
				.setVariable("instanceName", Template.toInstanceName(className));

		// テンプレートから本文生成
		URL contentURL = ObproPlugin.getDefault().getURL(getTemplatePath());
		String content = template.generate(contentURL);

		// 本文を設定する
		type.createMethod(content, null, false, null);
	}

	// ------ バリデーション変更のためのオーバーライド --------
	/**
	 * The wizard owning this page is responsible for calling this method with
	 * the current selection. The selection is used to initialize the fields of
	 * the wizard page.
	 * 
	 * @param selection
	 *            used to initialize the fields
	 */
	public void init(IStructuredSelection selection) {
		super.init(selection);

		// if (!ObproPlugin.isObproProject(getJavaProject())) {
		// IStatus status = new Status(IStatus.ERROR, ObproPlugin.PLUGIN_ID,
		// PROJECT_ERROR_MSG);
		// ErrorDialog.openError(getShell(), null, null, status);
		// throw new RuntimeException("Is Not ObproProject");
		// }

		doStatusUpdate();
	}

	/*
	 * @see NewContainerWizardPage#handleFieldChanged
	 */
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		doStatusUpdate();
	}

	private void doStatusUpdate() {
		// オブプロライブラリチェック
		if (!ObproPlugin.isObproProject(getJavaProject())) {
			isObproProject.setError(PROJECT_ERROR_MSG);
		}

		// 小文字からはじまるクラス名は許さない．
		StatusInfo typeNameStatus = ((StatusInfo) fTypeNameStatus);
		if (typeNameStatus.isWarning()) {
			typeNameStatus.setError(typeNameStatus.getMessage());
		}

		// status of all used components
		IStatus[] status = new IStatus[] {
				fContainerStatus,
				isObproProject, // ←追加
				// ↓ここはデフォルトパッケージでもＯＫにするので無効化．
				// isEnclosingTypeSelected() ? fEnclosingTypeStatus :
				// fPackageStatus,
				fTypeNameStatus, fModifierStatus, fSuperClassStatus,
				fSuperInterfacesStatus };

		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		updateStatus(status);

	}
}
