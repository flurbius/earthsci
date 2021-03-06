/*******************************************************************************
 * Copyright 2012 Geoscience Australia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package au.gov.ga.earthsci.application.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Handler called when quitting the application.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
public class QuitHandler
{
	@Execute
	public void execute(IWorkbench workbench, IEclipseContext context,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) throws InvocationTargetException, InterruptedException
	{
		if (MessageDialog.openQuestion(shell, Messages.QuitHandler_DialogTitle, Messages.QuitHandler_DialogMessage))
		{
			workbench.close();
		}
	}
}
