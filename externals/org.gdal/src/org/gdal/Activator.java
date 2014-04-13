/*******************************************************************************
 * Copyright 2013 Geoscience Australia
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
package org.gdal;

import java.io.File;
import java.util.logging.Logger;

import org.gdal.GDALDataSetup.DataFileSource;
import org.gdal.gdal.gdalJNI;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Bundle activator for the GDAL plugin.
 * 
 * Responsible for unpacking the GDAL data files and setting up appropriate
 * configurations.
 * 
 * @author James Navin (james.navin@ga.gov.au)
 */
public class Activator implements BundleActivator
{
	private static BundleContext context;

	private static Logger logger = Logger.getLogger(Activator.class.getName());

	static BundleContext getContext()
	{
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception
	{
		Activator.context = bundleContext;

		if (!gdalJNI.isAvailable())
		{
			// May happen within a tycho unit test execution
			logger.severe("Unable to load GDAL");
			return;
		}

		GDALDataSetup.run(new DataFileSource()
		{
			@Override
			public File getDataFolder()
			{
				return context.getDataFile("");
			}

			@Override
			public File getDataFile(String dataFileName)
			{
				return context.getDataFile(dataFileName);
			}
		});
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception
	{
		Activator.context = null;
	}

	public static String getBundleName()
	{
		return context.getBundle().getSymbolicName();
	}
}