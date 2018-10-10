//    RED - A Java Editor Library
//    Copyright (C) 2003  Robert Lichtenberger
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Lesser General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 
package org.rtefx;



/** This class serves for text -> view communication.
  * A view stretch is a piece of text that can be displayed by the REDView in one step.
  * @invariant fType == TAB || fType == LINEBREAK || fType == TEXT
  * @author rli@chello.at
  * @tier system
  */
class REDViewStretch 
{
	public static final int TAB = 0;
	public static final int LINEBREAK = 1;
	public static final int TEXT = 2;
	public static final int EOF = 3;

	public int fType; // TAB, LINEBREAK or TEXT
	public int fLength; // length of stretch
	public REDStyle fStyle; // style of stretch
	public REDText.REDRunSpec fRunSpec;	// run spec used for finding without memory turnaround
}
