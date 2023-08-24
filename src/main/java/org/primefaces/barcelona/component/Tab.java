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

import javax.faces.component.UIComponentBase;

public class Tab extends UIComponentBase {

	public static final String COMPONENT_TYPE = "org.primefaces.component.BarcelonaTab";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";

	public enum PropertyKeys {
		icon,
        title;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
        }
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getIcon() {
		return (String) getStateHelper().eval(PropertyKeys.icon, null);
	}
	public void setIcon(String _icon) {
		getStateHelper().put(PropertyKeys.icon, _icon);
	}
    
    public String getTitle() {
		return (String) getStateHelper().eval(PropertyKeys.title, null);
	}
	public void setTitle(String _title) {
		getStateHelper().put(PropertyKeys.title, _title);
	}
    
}