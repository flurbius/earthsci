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
package au.gov.ga.earthsci.layer.tree;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.Message;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.DrawContext;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import au.gov.ga.earthsci.common.persistence.Persistent;
import au.gov.ga.earthsci.common.util.IEnableable;
import au.gov.ga.earthsci.common.util.IInformationed;
import au.gov.ga.earthsci.common.util.Util;
import au.gov.ga.earthsci.layer.DummyLayer;
import au.gov.ga.earthsci.layer.IElevationModelLayer;
import au.gov.ga.earthsci.layer.ILayerDelegate;
import au.gov.ga.earthsci.layer.LayerDelegate;
import au.gov.ga.earthsci.worldwind.common.util.AVKeyMore;

/**
 * Layer tree node implementation for layers. Implements the {@link Layer}
 * interface, and delegates all layer methods to a {@link LayerDelegate} object.
 * <p/>
 * Also fires a property change in all setter methods to comply with the Java
 * Bean specification.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
public class LayerNode extends AbstractLayerTreeNode implements ILayerDelegate, IEnableable
{
	protected LayerDelegate layer = new LayerDelegate();

	@Override
	public void setURI(URI uri)
	{
		super.setURI(uri);
		if (layer.getLayer() instanceof DummyLayer)
		{
			layer.setName(uri.toString());
		}
	}

	/**
	 * @return The {@link Layer} that this node delegates to.
	 */
	@Override
	public Layer getLayer()
	{
		return layer.getLayer();
	}

	/**
	 * Set the {@link Layer} that this node delegates to.
	 * 
	 * @param layer
	 */
	@Override
	public void setLayer(Layer layer)
	{
		//set the values from the layer on this node
		setName(layer.getName());

		//get the legend url if it exists (set by the LayerFactory)
		URL legendURL = (URL) layer.getValue(AVKeyMore.LEGEND_URL);
		if (legendURL == null)
		{
			AVList constructionParameters = (AVList) layer.getValue(AVKey.CONSTRUCTION_PARAMETERS);
			if (constructionParameters != null)
			{
				legendURL = (URL) constructionParameters.getValue(AVKeyMore.LEGEND_URL);
			}
		}
		if (legendURL != null)
		{
			setLegendURL(legendURL);
		}

		//set the actual layer (this will copy any changed properties on the old layer to the new layer)
		this.layer.setLayer(layer);

		//TODO rethink this
		//we need to update the root's elevation models if this layer is an elevation model layer
		if (layer instanceof IElevationModelLayer)
		{
			childrenChanged(getChildren(), getChildren());
		}
	}

	@Override
	public URL getInformationURL()
	{
		//if the layer is IInformationed, use that instead
		if (layer.getLayer() instanceof IInformationed)
		{
			URL url = ((IInformationed) layer.getLayer()).getInformationURL();
			if (url != null)
			{
				return url;
			}
		}
		return super.getInformationURL();
	}

	@Override
	public String getInformationString()
	{
		//if the layer is IInformationed, use that instead
		if (layer.getLayer() instanceof IInformationed)
		{
			String information = ((IInformationed) layer.getLayer()).getInformationString();
			if (!Util.isEmpty(information))
			{
				return information;
			}
		}
		return super.getInformationString();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		firePropertyChange(evt);
	}

	//////////////////////
	// Layer delegation //
	//////////////////////

	@Override
	public void dispose()
	{
		layer.dispose();
	}

	@Override
	public void onMessage(Message msg)
	{
		layer.onMessage(msg);
	}

	@Override
	public Object setValue(String key, Object value)
	{
		return layer.setValue(key, value);
	}

	@Persistent(attribute = true)
	@Override
	public boolean isEnabled()
	{
		return layer.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		boolean oldValue = isEnabled();
		layer.setEnabled(enabled);
		firePropertyChange("enabled", oldValue, enabled); //$NON-NLS-1$
	}

	@Override
	public String getName()
	{
		return layer.getName();
	}

	@Override
	public void setName(String name)
	{
		String oldValue = getName();
		layer.setName(name);
		firePropertyChange("name", oldValue, name); //$NON-NLS-1$
	}

	@Override
	public AVList setValues(AVList avList)
	{
		return layer.setValues(avList);
	}

	@Override
	public String getRestorableState()
	{
		return layer.getRestorableState();
	}

	@Persistent(attribute = true)
	@Override
	public double getOpacity()
	{
		return layer.getOpacity();
	}

	@Override
	public void restoreState(String stateInXml)
	{
		layer.restoreState(stateInXml);
	}

	@Override
	public Object getValue(String key)
	{
		return layer.getValue(key);
	}

	@Override
	public void setOpacity(double opacity)
	{
		double oldValue = getOpacity();
		layer.setOpacity(opacity);
		firePropertyChange("opacity", oldValue, opacity); //$NON-NLS-1$
	}

	@Override
	public Collection<Object> getValues()
	{
		return layer.getValues();
	}

	@Override
	public String getStringValue(String key)
	{
		return layer.getStringValue(key);
	}

	@Override
	public boolean isPickEnabled()
	{
		return layer.isPickEnabled();
	}

	@Override
	public Set<Entry<String, Object>> getEntries()
	{
		return layer.getEntries();
	}

	@Override
	public boolean hasKey(String key)
	{
		return layer.hasKey(key);
	}

	@Override
	public Object removeKey(String key)
	{
		return layer.removeKey(key);
	}

	@Override
	public void setPickEnabled(boolean isPickable)
	{
		boolean oldValue = isPickEnabled();
		layer.setPickEnabled(isPickable);
		firePropertyChange("pickEnabled", oldValue, isPickable); //$NON-NLS-1$
	}

	@Override
	public void preRender(DrawContext dc)
	{
		layer.preRender(dc);
	}

	@Override
	public void render(DrawContext dc)
	{
		layer.render(dc);
	}

	@Override
	public void pick(DrawContext dc, Point pickPoint)
	{
		layer.pick(dc, pickPoint);
	}

	@Override
	public boolean isAtMaxResolution()
	{
		return layer.isAtMaxResolution();
	}

	@Override
	public boolean isMultiResolution()
	{
		return layer.isMultiResolution();
	}

	@Override
	public double getScale()
	{
		return layer.getScale();
	}

	@Override
	public boolean isNetworkRetrievalEnabled()
	{
		return layer.isNetworkRetrievalEnabled();
	}

	@Override
	public void setNetworkRetrievalEnabled(boolean networkRetrievalEnabled)
	{
		boolean oldValue = isNetworkRetrievalEnabled();
		layer.setNetworkRetrievalEnabled(networkRetrievalEnabled);
		firePropertyChange("networkRetrievalEnabled", oldValue, networkRetrievalEnabled); //$NON-NLS-1$
	}

	@Override
	public AVList copy()
	{
		return layer.copy();
	}

	@Override
	public void setExpiryTime(long expiryTime)
	{
		long oldValue = getExpiryTime();
		layer.setExpiryTime(expiryTime);
		firePropertyChange("expiryTime", oldValue, expiryTime); //$NON-NLS-1$
	}

	@Override
	public AVList clearList()
	{
		return layer.clearList();
	}

	@Override
	public long getExpiryTime()
	{
		return layer.getExpiryTime();
	}

	@Override
	public double getMinActiveAltitude()
	{
		return layer.getMinActiveAltitude();
	}

	@Override
	public void setMinActiveAltitude(double minActiveAltitude)
	{
		double oldValue = getMinActiveAltitude();
		layer.setMinActiveAltitude(minActiveAltitude);
		firePropertyChange("minActiveAltitude", oldValue, minActiveAltitude); //$NON-NLS-1$
	}

	@Override
	public double getMaxActiveAltitude()
	{
		return layer.getMaxActiveAltitude();
	}

	@Override
	public void setMaxActiveAltitude(double maxActiveAltitude)
	{
		double oldValue = getMaxActiveAltitude();
		layer.setMaxActiveAltitude(maxActiveAltitude);
		firePropertyChange("maxActiveAltitude", oldValue, maxActiveAltitude); //$NON-NLS-1$
	}

	@Override
	public boolean isLayerInView(DrawContext dc)
	{
		return layer.isLayerInView(dc);
	}

	@Override
	public boolean isLayerActive(DrawContext dc)
	{
		return layer.isLayerActive(dc);
	}

	@Override
	public Double getMaxEffectiveAltitude(Double radius)
	{
		return layer.getMaxEffectiveAltitude(radius);
	}

	@Override
	public Double getMinEffectiveAltitude(Double radius)
	{
		return layer.getMinEffectiveAltitude(radius);
	}
}