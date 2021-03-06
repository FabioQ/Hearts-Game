/**
 * Copyright (C) 2008 Guenther Niess. All rights reserved.
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
package org.frogx.service.api.exception;

public class NotAllowedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public NotAllowedException() {
		super();
	}
	
	public NotAllowedException(String msg) {
		super(msg);
	}
	
	public NotAllowedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public NotAllowedException(Throwable cause) {
		super(cause);
	}
}
