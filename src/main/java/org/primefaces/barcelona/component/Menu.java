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

import org.primefaces.component.menu.AbstractMenu;
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import org.primefaces.component.api.Widget;

@ListenerFor(sourceClass = Menu.class, systemEventClass = PostAddToViewEvent.class)
public class Menu extends AbstractMenu implements Widget,ComponentSystemEventListener {

    public static final String COMPONENT_TYPE = "org.primefaces.component.BarcelonaMenu";
    public static final String COMPONENT_FAMILY = "org.primefaces.component";
    private static final String DEFAULT_RENDERER = "org.primefaces.component.BarcelonaMenuRenderer";
    
    protected enum PropertyKeys {

        widgetVar, model, style, styleClass;

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

    public Menu() {
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

    public org.primefaces.model.menu.MenuModel getModel() {
        return (org.primefaces.model.menu.MenuModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(org.primefaces.model.menu.MenuModel _model) {
        getStateHelper().put(PropertyKeys.model, _model);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public String resolveWidgetVar() {
        FacesContext context = getFacesContext();
        String userWidgetVar = (String) getAttributes().get("widgetVar");

        if (userWidgetVar != null) {
            return userWidgetVar;
        } else {
            return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
        }
    }
}
