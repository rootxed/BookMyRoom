/*
   Copyright 2009-2022 PrimeTek.

   Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.primefaces.barcelona.component;

import javax.faces.component.*;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.UITabPanel;
import org.primefaces.util.Constants;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MapBuilder;

import java.util.Collection;
import java.util.Map;

@ListenerFor(sourceClass = TabMenu.class, systemEventClass = PostAddToViewEvent.class)
public class TabMenu extends UIPanel implements org.primefaces.component.api.Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_TYPE = "org.primefaces.component.BarcelonaTabMenu";
    public static final String COMPONENT_FAMILY = "org.primefaces.component";
    public static final String DEFAULT_RENDERER = "org.primefaces.component.BarcelonaTabMenuRenderer";

    private static final String[] LEGACY_RESOURCES = new String[]{"primefaces.css", "jquery/jquery.js", "jquery/jquery-plugins.js", "primefaces.js"};
    private static final String[] MODERN_RESOURCES = new String[]{"components.css", "jquery/jquery.js", "jquery/jquery-plugins.js", "core.js"};

    public enum PropertyKeys {

        widgetVar,
        activeIndex,
        onTabChange,
        onTabClose,
        stateful;

        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("tabChange", TabChangeEvent.class)
            .put("tabContentLoad", TabContentLoadEvent.class)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

    public TabMenu() {
        setRendererType(DEFAULT_RENDERER);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public int getActiveIndex() {
        return (Integer) getStateHelper().eval(PropertyKeys.activeIndex, 0);
    }

    public void setActiveIndex(int _activeIndex) {
        getStateHelper().put(PropertyKeys.activeIndex, _activeIndex);
    }

    public String getOnTabChange() {
        return (String) getStateHelper().eval(PropertyKeys.onTabChange, null);
    }

    public void setOnTabChange(String _onTabChange) {
        getStateHelper().put(PropertyKeys.onTabChange, _onTabChange);
    }
    
    public String getOnTabClose() {
        return (String) getStateHelper().eval(PropertyKeys.onTabClose, null);
    }

    public void setOnTabClose(String _onTabClose) {
        getStateHelper().put(PropertyKeys.onTabClose, _onTabClose);
    }

    public boolean isStateful() {
        return (Boolean) getStateHelper().eval(PropertyKeys.stateful, true);
    }

    public void setStateful(boolean _stateful) {
        getStateHelper().put(PropertyKeys.stateful, _stateful);
    }

    @Override
    public String resolveWidgetVar() {
        String userWidgetVar = getWidgetVar();

        if (!LangUtils.isValueBlank(userWidgetVar)) {
            return userWidgetVar;
        }
        else {
            FacesContext context = getFacesContext();
            String clientId = getClientId(context);
            return "widget_" + clientId.replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
        }
    }

    public Tab findTab(String tabClientId) {
        for (UIComponent component : getChildren()) {
            if (component.getClientId().equals(tabClientId)) {
                return (Tab) component;
            }
        }

        return null;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (eventName.equals("tabChange")) {
                String tabClientId = params.get(clientId + "_newTab");
                int tabIndex = Integer.parseInt(params.get(clientId + "_tabindex"));
                TabChangeEvent changeEvent = new TabChangeEvent(this, behaviorEvent.getBehavior(), findTab(tabClientId), tabIndex);

                changeEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(changeEvent);
            } else if (eventName.equals("tabContentLoad")) {
                String tabClientId = params.get(clientId + "_newTab");
                int tabIndex = Integer.parseInt(params.get(clientId + "_tabindex"));
                TabContentLoadEvent contentLoadEvent = new TabContentLoadEvent(this, behaviorEvent.getBehavior(), findTab(tabClientId), tabIndex);

                contentLoadEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(contentLoadEvent);
            }
        } else {
            super.queueEvent(event);
        }
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        if (event instanceof PostAddToViewEvent) {
            FacesContext context = getFacesContext();
            UIViewRoot root = context.getViewRoot();

            boolean isPrimeConfig;
            try {
                isPrimeConfig = Class.forName("org.primefaces.config.PrimeConfiguration") != null;
            } catch (ClassNotFoundException e) {
                isPrimeConfig = false;
            }

            String[] resources = (isPrimeConfig) ? MODERN_RESOURCES : LEGACY_RESOURCES;

            for (String res : resources) {
                UIComponent component = context.getApplication().createComponent(UIOutput.COMPONENT_TYPE);
                if (res.endsWith("css")) {
                    component.setRendererType("javax.faces.resource.Stylesheet");
                } else if (res.endsWith("js")) {
                    component.setRendererType("javax.faces.resource.Script");
                }

                component.getAttributes().put("library", "primefaces");
                component.getAttributes().put("name", res);

                root.addComponentResource(context, component);
            }
        }
    }
}
