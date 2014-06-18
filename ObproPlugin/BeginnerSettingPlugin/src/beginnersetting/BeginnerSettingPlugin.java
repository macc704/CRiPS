package beginnersetting;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.internal.UIPlugin;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.texteditor.TextEditorPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class BeginnerSettingPlugin extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "jp.ac.shizuoka.inf.cs.ce.beginnersetting";

	// The shared instance
	private static BeginnerSettingPlugin plugin;

	/**
	 * The constructor
	 */
	public BeginnerSettingPlugin() {
	}

	public void earlyStartup() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// investigatePreferenceName();

		// setToBeginnerPreferences(); // SWT Thread Error
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setToBeginnerPreferences();
			}
		});

	}

	/**********************************************
	 * Preference初期設定（自動で強制的に変えてしまう）
	 **********************************************/

	@SuppressWarnings("deprecation")
	private void setToBeginnerPreferences() {
		// Workbench
		IPreferenceStore workbenchPreference = WorkbenchPlugin.getDefault()
				.getPreferenceStore();
		workbenchPreference.setValue("REUSE_OPEN_EDITORS_BOOLEAN", true);
		workbenchPreference.setValue("REUSE_OPEN_EDITORS", 4);
		workbenchPreference.setValue("REUSE_OPEN_EDITORS", 4);

		// 何故以下で出来ないのかは不明
		// IPreferenceStore ideWorkbenchPreference = IDEWorkbenchPlugin
		// .getDefault().getPreferenceStore();
		// ideWorkbenchPreference.setValue("IDE_ENCODINGS_PREFERENCE", "SJIS");

		// Resources Plugin
		ResourcesPlugin.getPlugin().getPluginPreferences().setValue(
				ResourcesPlugin.PREF_ENCODING, "SJIS");

		// Editors
		IPreferenceStore editorsPreference = EditorsPlugin.getDefault()
				.getPreferenceStore();
		editorsPreference.setValue("currentLine", false);
		editorsPreference.setValue("lineNumberRuler", true);

		// Java(JDT)
		IPreferenceStore javaPreference = JavaPlugin.getDefault()
				.getPreferenceStore();
		javaPreference
				.setValue(
						"editor_save_participant_org.eclipse.jdt.ui.postsavelistener.cleanup",
						true);
		javaPreference.setValue("sp_cleanup.format_source_code", true);
		javaPreference.setValue("sp_cleanup.organize_imports", true);
		javaPreference.setValue("closeStrings", false);
		javaPreference.setValue("closeBrackets", false);
		javaPreference.setValue("closeBrces", false);
	}

	/**
	 * 未使用
	 */
	@SuppressWarnings("unused")
	private void investigatePreferenceName() {
		WorkbenchPlugin.getDefault().getPreferenceStore()
				.addPropertyChangeListener(
						new MyPropertyChangeListener("Workbench"));
		EditorsPlugin.getDefault().getPreferenceStore()
				.addPropertyChangeListener(
						new MyPropertyChangeListener("Editors"));
		JavaPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(
				new MyPropertyChangeListener("Java"));
		TextEditorPlugin.getDefault().getPreferenceStore()
				.addPropertyChangeListener(
						new MyPropertyChangeListener("TextEditor"));
		IDEWorkbenchPlugin.getDefault().getPreferenceStore()
				.addPropertyChangeListener(
						new MyPropertyChangeListener("IDEWorkbench"));
		UIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(
				new MyPropertyChangeListener("UI"));
	}

	class MyPropertyChangeListener implements IPropertyChangeListener {
		private String name;

		public MyPropertyChangeListener(String name) {
			this.name = name;
		}

		public void propertyChange(PropertyChangeEvent event) {
			System.out.println(name);
			System.out.println(event.getProperty());
			System.out.println(event.getOldValue());
			System.out.println(event.getNewValue());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static BeginnerSettingPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
