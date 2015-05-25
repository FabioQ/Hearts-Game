/**
 * Copyright (C) 2009 Guenther Niess. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.frogx.service.component.util;

import java.util.ResourceBundle;

import org.frogx.service.api.util.LocaleUtil;

public class DefaultLocaleUtil implements LocaleUtil {
	
	public final static String bundleBaseName = "frogx-service_i18n";
	
	private ResourceBundle bundle;
	
	public DefaultLocaleUtil() {
		bundle = ResourceBundle.getBundle(bundleBaseName); 
	}
	
	public String getLocalizedString(String key) {
		return bundle.getString(key);
	}

}
