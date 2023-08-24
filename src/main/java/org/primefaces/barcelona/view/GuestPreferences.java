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
package org.primefaces.barcelona.view;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named
@SessionScoped
public class GuestPreferences implements Serializable {

    private String theme = "blue-grey";

    private String layout = "blue-grey";
            
    private boolean overlayMenu;
    
    private boolean darkMenu;
    
    private boolean orientationRTL;

    private boolean compactMode = false;

    private boolean ripple = true;

    private String inputStyle = "outlined";

    private List<ComponentTheme> componentThemes = new ArrayList<ComponentTheme>();

    @PostConstruct
    public void init() {
        componentThemes.add(new ComponentTheme("Blue", "blue","#1976d2"));
        componentThemes.add(new ComponentTheme("Blue Grey", "blue-grey","#607D8B"));
        componentThemes.add(new ComponentTheme("Cyan", "cyan","#0097a7"));
        componentThemes.add(new ComponentTheme("Dark Blue", "dark-blue","#3e464c"));
        componentThemes.add(new ComponentTheme("Dark Green", "dark-green","#2f4050"));
        componentThemes.add(new ComponentTheme("Deep Purple", "deep-purple","#673ab7"));
        componentThemes.add(new ComponentTheme("Green", "green","#43A047"));
        componentThemes.add(new ComponentTheme("Indigo", "indigo","#3f51b5"));
        componentThemes.add(new ComponentTheme("Light Blue", "light-blue","#03A9F4"));
        componentThemes.add(new ComponentTheme("Teal", "teal","#009688"));

    }
    
	public String getTheme() {		
		return theme;
	}
    
	public void setTheme(String theme) {
		this.theme = theme;
        this.layout = theme;
	}

    public String getLayout() {		
		return layout;
	}
        
    public boolean isDarkMenu() {
        return this.darkMenu;
    }
    
    public void setDarkMenu(boolean value) {
        this.darkMenu = value;
    }
    
    public boolean isOverlayMenu() {
        return this.overlayMenu;
    }
    
    public void setOverlayMenu(boolean value) {
        this.overlayMenu = value;
    }

    public boolean isRipple() {
        return ripple;
    }

    public void setRipple(boolean ripple) {
        this.ripple = ripple;
    }

    public boolean isOrientationRTL() {
        return orientationRTL;
    }

    public void setOrientationRTL(boolean orientationRTL) {
        this.orientationRTL = orientationRTL;
    }

    public boolean isCompactMode() {
        return compactMode;
    }

    public void setCompactMode(boolean compactMode) {
        this.compactMode = compactMode;
    }

    public String getInputStyle() {
        return inputStyle;
    }

    public void setInputStyle(String inputStyle) {
        this.inputStyle = inputStyle;
    }

    public String getInputStyleClass() {
        return this.inputStyle.equals("filled") ? "ui-input-filled" : "";
    }

    public List<ComponentTheme> getComponentThemes() {
        return componentThemes;
    }

    public class ComponentTheme {
        String name;
        String file;
        String color;

        public ComponentTheme(String name, String file, String color) {
            this.name = name;
            this.file = file;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public String getFile() {
            return this.file;
        }

        public String getColor() {
            return this.color;
        }
    }
}
