/*******************************************************************************
 * Copyright (C) 2013 - 2014, Girfa eSuite
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
 * 
 * Author : Afrig Aminuddin <my@girfa.com>
 ******************************************************************************/
package com.girfa.apps.teamtalk4mobile.api.bitflags;

public abstract class BitFlags {
	private int flags;
	
	public BitFlags(int value) {
		setFlags(value);
	}
	
	public final void setFlags(int flags) {
		this.flags = flags;
	}
	
	public final boolean is(int flag) {
		return (flag & flags) == flag;
	}
	
	public final int getFlags() {
		return flags;
	}
	
	@Override
	public final String toString() {
		return getFlags() + "";
	}
}
