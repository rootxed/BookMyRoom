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

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class TabMenuRenderer extends CoreRenderer {
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        TabMenu tabMenu = (TabMenu) component;
        ResponseWriter writer = context.getResponseWriter();  
        List<UIComponent> children = tabMenu.getChildren();
        String clientId = tabMenu.getClientId(context);
        
        writer.startElement("div", tabMenu);
        writer.writeAttribute("id", clientId , "id");
        writer.writeAttribute("class", "layout-tabmenu", null);
        
        writer.startElement("ul", tabMenu);
        writer.writeAttribute("class", "layout-tabmenu-nav", null);
        
        for(UIComponent child : children ){
            if(child.isRendered() && child instanceof Tab) {
                Tab tab = (Tab) child;
                UIComponent header = tab.getFacet("header");
                
                writer.startElement("li", tab);
                writer.writeAttribute("id", tab.getClientId(context) , "id");
                    if (header != null) {
                        header.encodeAll(context);
                    }
                    else {
                        writer.startElement("a", tab);
                        writer.writeAttribute("href", "#", null);
                        writer.writeAttribute("class", "ripplelink tabmenuitem-link", null);
                            writer.startElement("i", tab);
                            String icon = tab.getIcon();
                            if(icon.contains("fa fa-") || icon.contains("pi pi-")) {
                                writer.writeAttribute("class", icon, null);
                            }
                            else {
                                writer.writeAttribute("class", "material-icons", null);
                                writer.writeText(icon, null);
                            }
                            writer.endElement("i");
                        writer.endElement("a");
                    
                        writer.startElement("div", null);
                            writer.writeAttribute("class", "layout-tabmenu-tooltip", null);
                            writer.startElement("div", null);
                            writer.writeAttribute("class", "layout-tabmenu-tooltip-arrow", null);
                            writer.endElement("div");
                            writer.startElement("div", null);
                            writer.writeAttribute("class", "layout-tabmenu-tooltip-text", null);
                            writer.writeText(tab.getTitle(), null);
                            writer.endElement("div");
                        writer.endElement("div");
                    }
                writer.endElement("li");
            }
        }
        
        writer.endElement("ul");
        
        writer.startElement("div", tabMenu);
        writer.writeAttribute("class", "layout-tabmenu-contents", null);
        for(int i = 0; i < children.size(); i++) {
            Tab tab = (Tab) children.get(i);
            
            if(tab.isRendered()) {
                writer.startElement("div", tabMenu);
                writer.writeAttribute("class", "layout-tabmenu-content", null);

                    writer.startElement("div", tabMenu);
                    writer.writeAttribute("class", "layout-submenu-title clearfix", null);
                        writer.startElement("span", tab);
                        writer.writeText(tab.getTitle(), null);
                        writer.endElement("span");

                        writer.startElement("a", tab);
                        writer.writeAttribute("href", "#", null);
                        writer.writeAttribute("class", "menu-button pi pi-bars", null);
                        writer.endElement("a");
                    writer.endElement("div");

                    writer.startElement("div", tabMenu);
                    writer.writeAttribute("class", "layout-submenu-content", null);
                        writer.startElement("div", null);
                        writer.writeAttribute("class", "menu-scroll-content", null);
                            tab.encodeAll(context);
                        writer.endElement("div");
                    writer.endElement("div");


                writer.endElement("div");
            }
        }
        writer.endElement("div");
        
        writer.endElement("div");
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Barcelona", tabMenu.resolveWidgetVar(), clientId)
                .callback("onTabChange", "function(index)", tabMenu.getOnTabChange())
                .callback("onTabClose", "function(index)", tabMenu.getOnTabClose())
                .attr("stateful", tabMenu.isStateful())
                .attr("activeIndex", tabMenu.getActiveIndex());

        encodeClientBehaviors(context, tabMenu);
        wb.finish();
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }
    
    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
