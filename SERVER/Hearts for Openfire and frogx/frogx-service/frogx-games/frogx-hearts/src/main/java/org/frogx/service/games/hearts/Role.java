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
package org.frogx.service.games.hearts;

public enum Role {
	
	none(0),
	north(10),
	east(20),
	south(30),
	west(40);

	
	private int value;

	
	Role(int value) {
		this.value = value;
	}

	
	public int getValue() {
		return value;
	}

	
	public static Role valueOf(int value) {
		switch (value) {
			case 10: return north;
			case 20: return east;
			case 30: return south;
			case 40: return west;
			default: return none;
		}
	}
}
