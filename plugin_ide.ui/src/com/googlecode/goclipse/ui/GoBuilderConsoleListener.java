/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package com.googlecode.goclipse.ui;

import java.io.IOException;

import melnorme.lang.ide.ui.build.LangOperationConsole;
import melnorme.lang.ide.ui.build.LangOperationConsoleListener;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessNotifyingHelper;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.RGB;

import com.googlecode.goclipse.builder.IGoBuildListener;

public class GoBuilderConsoleListener extends LangOperationConsoleListener implements IGoBuildListener {
	
	@Override
	protected String getOperationConsoleName(IProject project) {
		return "Go build " + getProjectNameSuffix(project);
	}
	
	public static class GoBuildConsole extends LangOperationConsole {
		
		public GoBuildConsole(String name) {
			super(name, GoPluginImages.GO_CONSOLE_ICON.getDescriptor());
		}
		
		@Override
		protected void ui_initOutputStreamColors() {
			metaOut.setColor(getColorManager().getColor(new RGB(0, 0, 180)));
			stdErr.setColor(getColorManager().getColor(new RGB(200, 0, 0)));
		}
		
	}
	
	@Override
	public void handleBuildStarted(IProject project) {
		String projName = project.getName();
		getOperationConsole(project, true).writeOperationInfo(
			"************  Running Go build for project: " + projName + "  ************\n");
	}
	
	@Override
	public void handleBuildTerminated(IProject project) {
		getOperationConsole(project, false).writeOperationInfo(
			"************  Build terminated.  ************\n");
	}
	
	@Override
	public void handleProcessStarted(ProcessBuilder pb, IProject project, 
			ExternalProcessNotifyingHelper processHelper) {
		GoBuildConsole console = getOperationConsole(project, false);
		
		try {
			writeProcessStartPrefix(pb, console);
			
			processHelper.getOutputListenersHelper().addListener(new ProcessOutputToConsoleListener(console));
		} catch (IOException e) {
			return;
		}
	}
	
	protected void writeProcessStartPrefix(ProcessBuilder pb, GoBuildConsole console) throws IOException {
		console.metaOut.write(StringUtil.collToString(pb.command(), " ") + "\n");
	}
	
	@Override
	public void handleProcessStartFailure(ProcessBuilder pb, IProject project, IOException processStartException) {
		GoBuildConsole console = getOperationConsole(project, false);
		
		try {
			writeProcessStartPrefix(pb, console);
			console.metaOut.write(">>>  Failed to start process: \n");
			console.metaOut.write(processStartException.getMessage());
		} catch (IOException consoleIOE) {
			return;
		}
	}
	
}