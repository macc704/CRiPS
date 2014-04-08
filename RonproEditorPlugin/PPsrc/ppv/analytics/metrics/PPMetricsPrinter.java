/*
 * PPMetricsPrinter.java
 * Created on 2011/06/30
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.analytics.metrics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import ppv.app.datamanager.PPProjectSet;
import pres.loader.model.PLFile;
import pres.loader.model.PLPackage;
import pres.loader.model.PLProject;
import clib.common.utils.ICProgressMonitor;

/**
 * @author macchan 作り中
 */
public class PPMetricsPrinter {

	private static String CAMMA = ",";

	private PrintWriter pw;

	// private NumberFormat formatter = new DecimalFormat("0.00");

	public PPMetricsPrinter() {
	}

	public void printMetrics(PPProjectSet projectSet, OutputStream out,
			ICProgressMonitor monitor) throws Exception {
		// PrintWriter
		pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out,
				"sjis")));
		pw.println(PPMetrics.createHeader());
		pw.flush();

		// Projectの出力
		monitor.setWorkTitle("Metrics Writing...");
		List<PLProject> projects = projectSet.getProjects();
		monitor.setMax(projects.size());
		int count = 0;
		for (PLProject project : projects) {
			try {
				monitor.progress(1);
				printOneProject(project);
				count++;
				if (count % 100 == 0) {
					System.gc();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		pw.close();
	}

	private void printOneProject(PLProject project) throws IOException {
		boolean loaded = project.isLoaded();
		if (!loaded) {
			project.load();
		}
		PLPackage pack = project.getRootPackage();
		String pjName = pack.getName();		
		for (PLFile plFile : pack.getFilesRecursively()) {
			printOneUnit(pjName, plFile);
		}
		if (!loaded) {
			project.unLoad();
		}

	}

	private void printOneUnit(String pjName, PLFile plFile) {
		StringBuffer buf = new StringBuffer();
		buf.append(pjName);
		buf.append(CAMMA);
		PPMetrics metrics = new PPMetrics(plFile);
		buf.append(metrics.getMetricsPrintString());
		pw.println(buf.toString());
		pw.flush();
	}

}
