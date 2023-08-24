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

import org.primefaces.event.AbstractAjaxBehaviorEvent;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

public class TabContentLoadEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    private Tab tab;
    private int tabIndex;
    private Object data;

    public TabContentLoadEvent(UIComponent component, Behavior behavior, Tab tab, int tabIndex) {
        super(component, behavior);
        this.tab = tab;
        this.tabIndex = tabIndex;
    }

    public Tab getTab() {
        return tab;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
