//    RTEFX - Rich Text Editor for JavaFX
//    Copyright (C) 2003, 2018  Robert Lichtenberger
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

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.rtefx.util.REDGLog;
import org.rtefx.util.REDTracer;
import org.rtefx.xml.REDXMLCallbackError;
import org.rtefx.xml.REDXMLHandlerReader;
import org.rtefx.xml.REDXMLHandlerWriter;
import org.rtefx.xml.REDXMLManager;
import org.rtefx.xml.REDXMLReadable;

/** REDStyle objects contain foreground and background color, font and lining information
  * @author rli@chello.at
  * @tier API
  * @see REDStyleManager
  * @invariant fForeground != null || fSuper != null
  * @invariant fBackground != null || fSuper != null
  * @invariant fLining != null || fSuper != null
  * @invariant fFontFace != null || fSuper != null
  * @invariant fFontSize != INHERITED || fSuper != null
  * @invariant fFontStyle != INHERITED || fSuper != null
  */
public class REDStyle implements REDXMLReadable {
	/** Inherited from superstyle. */
	final public static int INHERITED = -1;

	private TreeMap<String, ThemeEntry> themes;
	private ThemeEntry curTheme;
	private HashMap<Object, Object> mappings;	
	private String fName, fDisplayName, fDescription;
	private REDStyleManagerImpl fManager;
	
	/** Create a style.
	  * You should not call this constructor directly. Ask the REDStyleManager for a shared style.
	  * @param foreground The foreground color of the style.
	  * @param background The background color of the style.
	  * @param lining The lining of the style (cf. the LC_* constants).
	  * @param font The font of the style.
	  * @param superStyle The style this style is derived from. May be <Code>null</Code>.
	  * @pre (foreground != null && background != null && lining != INHERITED && font != null) || superStyle != null
	  */
	public REDStyle(Color foreground, Color background, REDLining lining, String fontFace, String fontStyle, int fontSize, REDStyle superStyle) {
		fName = "";
		fManager = REDStyleManager.fgDevNull;
		mappings = new HashMap<>();
		themes = new TreeMap<>();
		curTheme = getOrCreateThemeEntry("Default");
		curTheme.foreground = foreground; 
		curTheme.background = background;
		curTheme.lining = lining;
		curTheme.fontFace = fontFace;
		curTheme.fontSize = fontSize;
		curTheme.superStyle = superStyle;
	}
	
	/** Create empty style.
	  * You should not call this constructor directly. It is used for reading in styles from .xml - files.
	  */
	public REDStyle() {
		this(null, null, null, null, null, INHERITED, null);
	}
	
	REDStyle copy() {
		REDStyle s = new REDStyle();
		Iterator<String> iter = themes.keySet().iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			ThemeEntry e = getThemeEntry(name);
			ThemeEntry eCopy = e.copy();
			s.themes.put(name, eCopy);
		}
		s.fDisplayName = fDisplayName;
		s.fDescription = fDescription;
		return s;
	}
	
	public String toString() {
		return "REDStyle (" + getName() + "): \n  Foreground: " + getForeground() + "\n  Background: " + getBackground() + "\n  Lining: " + getLining() + "\n  Font: " + getFont();
	}
	
	/** Get name of style. 
	  * @return The name under which this style is registered in REDStyleManager, or <Code>""</Code> if it is not registered at all.
	  */
	public String getName() {
		return fName;
	}
	
	/** Set name of style. This method must be called by REDStyleManager only.
	  * @param name The name of the style.
	  */
	void setName(String name) {
		fName = name;
	}
	
	public void setMappings(REDXMLHandlerReader handler) throws REDXMLCallbackError {
		handler.mapStart("Foreground", "setForeground('Default', (int) #red, (int) #green, (int) #blue)");
		handler.mapStart("Background", "setBackground('Default', (int) #red, (int) #green, (int) #blue)");
		handler.mapEnd("Lining", "setLining('Default', (" + REDLining.class.getName() + ") #[" + REDLining.class.getName() + ".])");
		handler.mapEnd("Super", "setSuper('Default', #)");
		handler.mapEnd("FontFace", "setFontFace('Default', #)");
		handler.mapEnd("FontSize", "setFontSize('Default', (int) # = '-1')");
		handler.mapEnd("FontStyle", "setFontStyle('Default', #)");
		handler.mapEnd("DisplayName", "setDisplayName(#)");
		handler.mapEnd("Description", "setDescription(#)");
		handler.mapEnd("#", "registerStyle(#&, #$)");
		handler.mapStart("#", "setId(#id, #theme='Default', #&, #$)");
	}
	
	public void innerProduction(REDXMLReadable obj, REDXMLHandlerReader inner, REDXMLHandlerReader outer) {
	}

	/** Set foreground color.  */
	public boolean setForeground(String theme, int red, int green, int blue) {
		return setForeground(theme, new Color(red, green, blue, 1));
	}
	
	/** Set foreground color.  */
	public boolean setForeground(String theme, Color c) {
		ThemeEntry e = getOrCreateThemeEntry(theme);
		fManager.doSendBeforeStyleChange(this);
		e.foreground = c;
		fManager.doSendAfterStyleChange(this);
		return true;		
	}
	
	/** Set background color.  */
	public boolean setBackground(String theme, int red, int green, int blue) {
		return setBackground(theme, new Color(red, green, blue, 1));
	}
	
	/** Set background color.  */
	public boolean setBackground(String theme, Color c) {
		ThemeEntry e = getOrCreateThemeEntry(theme);
		fManager.doSendBeforeStyleChange(this);
		e.background = c;
		fManager.doSendAfterStyleChange(this);
		return true;
	}
	
	
	/** Set lining. */
	public boolean setLining(String theme, REDLining lining) {
		ThemeEntry e = getOrCreateThemeEntry(theme);
		fManager.doSendBeforeStyleChange(this);
		e.lining = lining;
		fManager.doSendAfterStyleChange(this);
		return true;
	}
	
	private boolean setSuper(String theme, REDStyle newSuper) {
		ThemeEntry e = getOrCreateThemeEntry(theme);
		if (newSuper != e.superStyle) {
			fManager.doSendBeforeStyleChange(this);
			e.superStyle = newSuper;
			e.fontCache = null;
			fManager.doSendAfterStyleChange(this);
		}
		return true;
	}
	
	/** Set super-style. */
	public void setSuper(String theme, String superStyle) {
		if (!REDStyleManager.hasStyle(superStyle)) {
			REDTracer.error("red", "REDStyle", "Unknown superstyle '" + superStyle + "' ignored. Using 'Default' instead.");
		}
		setSuper(theme, REDStyleManager.getStyle(superStyle));
	}
	
	/** Set font face. */
	public boolean setFontFace(String theme, String fontFace) {
		ThemeEntry e = getOrCreateThemeEntry(theme);
		if (fontFace != e.fontFace) {
			fManager.doSendBeforeStyleChange(this);
			e.fontFace = fontFace;
			e.fontCache = null;
			fManager.doSendAfterStyleChange(this);
		}
		return true;
	}
	
	/** Set font size. */
	public boolean setFontSize(String theme, int fontSize) {
		ThemeEntry e = getOrCreateThemeEntry(theme);
		if (fontSize != e.fontSize) {
			fManager.doSendBeforeStyleChange(this);
			e.fontSize = fontSize;
			e.fontCache = null;
			fManager.doSendAfterStyleChange(this);
		}
		return true;
	}

	/** Set font posture. */
	public boolean setFontPosture(String theme, FontPosture fontPosture) {
		ThemeEntry e = getOrCreateThemeEntry(theme);
		if (fontPosture != e.fontPosture) {
			fManager.doSendBeforeStyleChange(this);
			e.fontPosture = fontPosture;
			e.fontCache = null;
			fManager.doSendAfterStyleChange(this);
		}
		return true;
	}

	/** Set font posture. */
	public boolean setFontWeight(String theme, FontWeight fontWeight) {
		ThemeEntry e = getOrCreateThemeEntry(theme);
		if (fontWeight != e.fontWeight) {
			fManager.doSendBeforeStyleChange(this);
			e.fontWeight = fontWeight;
			e.fontCache = null;
			fManager.doSendAfterStyleChange(this);
		}
		return true;
	}

	/** Set style id (name). */
	public void setId(String id, String theme, REDXMLHandlerReader handler, REDXMLManager manager) {
		handler.putClientData("id", id);
		handler.putClientData("theme", theme);
	}
	
	/** Set display name.
	  * The display name is a short, concise description of the style to be used as label for editing purposes.
	  * @param displayName The display name to be used.
	  */
	public void setDisplayName(String displayName) {
		fDisplayName = displayName;
	}
	
	/** Get display name.
	  * The display name is a short, concise description of the style to be used as label for editing purposes.
	  * @return The display name to be used for this style. If no display name has been specified, the name of the style will be returned.
	  */
	public String getDisplayName() {
		if (fDisplayName != null) {
			return fDisplayName;
		}
		return getName();
	}
	
	/** Set description.
	  * The description is a verbose, but single-lined definition of the purpose of a style.
	  * @param description The description to be set.
	  */
	public void setDescription(String description) {
		fDescription = description;
	}
	
	/** Get description.
	  * The description is a verbose, but single-lined definition of the purpose of a style.
	  * @return The description be used for this style. If no description has been specified, <Code>""</Code> will be returned.
	  */
	public String getDescription() {
		if (fDescription != null) {
			return fDescription;
		}
		return "";
	}
	
	
	private ThemeEntry getOrCreateThemeEntry(String theme) {
		ThemeEntry e = themes.get(theme);
		if (e == null) {
			if (theme.equals("Default")) {
				e = new ThemeEntry();
			}
			else {
				e = getThemeEntry("Default").copy();
			}
			themes.put(theme, e);
		}
		return e;
	}
	
	private ThemeEntry getThemeEntrySafe(String theme) {
		ThemeEntry e = themes.get(theme);
		if (e == null) {
			e = themes.get("Default");
		}
		return e;
	}
	
	private ThemeEntry getThemeEntry(String theme) {
		return themes.get(theme);
	}
	

	private void merge(String theme, ThemeEntry source) {
		ThemeEntry e = getOrCreateThemeEntry(theme);
		e.foreground = source.foreground;
		e.background = source.background;
		e.lining = source.lining;
		e.fontFace = source.fontFace;
		e.fontPosture = source.fontPosture;
		e.fontWeight = source.fontWeight;
		e.fontSize = source.fontSize;
		e.superStyle = source.superStyle;
	}
	
	public void installTheme(String theme) {
		curTheme = getThemeEntrySafe(theme);		
	}
	
	/** Set backing store for theme of style. If the given theme name does not exist, the method has no effect.
	  * @param theme The name of the theme to set backing store for.
	  * @param backingStore The file to write this theme back, if requested.
	  */
	public void setBackingStore(String theme, File backingStore) {
		ThemeEntry e = getThemeEntry(theme);
		if (e != null) {
			e.backingStore = backingStore;
		}
	}
	
	/** Get backing store. 
	  * @param theme The name of the theme to get backing store for.
	  * @return The file this style/theme is stored into, or <Code>null</Code> if the given theme name does not exist.
	  */
	public File getBackingStore(String theme) {
		ThemeEntry e = getThemeEntry(theme);
		if (e != null) {
			return e.backingStore;
		}
		return null;
	}
	
	private boolean styleOk() {
		return curTheme.superStyle != null ||
			(curTheme.fontFace != null && curTheme.fontSize != INHERITED && curTheme.fontPosture != null && curTheme.fontWeight != null
				&& curTheme.lining != null && curTheme.foreground != null && curTheme.background != null);
	}
	
	/** Register style at REDStyleManager. XML callback method. */
	public void registerStyle(REDXMLHandlerReader handler, REDXMLManager manager) {
		String id = (String) handler.getClientData("id");
		String theme = "" + handler.getClientData("theme");
		if (styleOk()) {
			if (!REDStyleManager.hasStyle(id)) {
				REDStyleManager.addStyle(id, this);
			}
			REDStyle target = REDStyleManager.getStyle(id);
			target.merge(theme, this.curTheme);
			target.setBackingStore(theme, (File) manager.getClientData("backingStore"));
			handler.removeClientData("id");
			handler.removeClientData("theme");
			if (theme.equals("Default")) {
				target.fDisplayName = fDisplayName;
				target.fDescription = fDescription;
			}
			else if (fDisplayName != null || fDescription != null) {
				REDGLog.warning("RED", "Display name and description are ignored in theme '" + theme + "' of style '" + id + "'");
			}
		}
		else {
			REDGLog.error("RED", "Incomplete style '" + id + "' (theme '" + theme + "') ignored.");
		}
	}

	/** Get foreground color of style. 
	  * @param theme The theme to look foreground color up for. If the given theme does not exist, the default theme is used.
	  * @return Foreground color object.
	  */
	public Color getForeground(String theme) {
		ThemeEntry e = getThemeEntrySafe(theme);
		if (e.foreground == null && e.superStyle != null) {
			return e.superStyle.getForeground(theme);
		}
		return e.foreground;
	}

	/** Get foreground color of style for currently active theme. 
	  * @return Foreground color object.
	  */
	public Color getForeground() {
		if (curTheme.foreground == null && curTheme.superStyle != null) {
			return curTheme.superStyle.getForeground();
		}
		return curTheme.foreground;
	}

	/** Get background color of style.
	  * @param theme The theme to look in. If the theme does not exist, the default theme is used.
	  * @return Background color object.
	  */
	public Color getBackground(String theme) {
		ThemeEntry e = getThemeEntrySafe(theme);
		if (e.background == null && e.superStyle != null) {
			return e.superStyle.getBackground(theme);
		}
		return e.background;
	}
	
	/** Get background color of style for active theme.
	  * @return Background color object.
	  */
	public Color getBackground() {
		if (curTheme.background == null && curTheme.superStyle != null) {
			return curTheme.superStyle.getBackground();
		}
		return curTheme.background;
	}

	/** Get lining of style.
	  * @param theme The theme to look lining up for. If the theme does not exist, the default theme is used.
	  * @return A REDLining object representing the lining of the style.
	  */
	public REDLining getLining(String theme) {
		ThemeEntry e = getThemeEntrySafe(theme);
		if (e.lining == null && e.superStyle != null) {
			return e.superStyle.getLining(theme);
		}
		return e.lining;
	}
	
	/** Get lining of style of active theme.
	  * @return A REDLining object representing the lining of the style.
	  */
	public REDLining getLining() {
		if (curTheme.lining == null && curTheme.superStyle != null) {
			return curTheme.superStyle.getLining();
		}
		return curTheme.lining;
	}
	
	/** Get font face of style. 
	  * @param theme The theme to look font face up for. If the theme does not exist, the default theme is used.
	  * @return The string representing the font face of the style.
	  */
	public String getFontFace(String theme) {
		ThemeEntry e = getThemeEntrySafe(theme);
		if (e.fontFace == null || e.fontFace.equals("")) {
			return e.superStyle.getFontFace(theme);
		}
		return e.fontFace;
	}
	
	/** Get font face of style for active theme.
	  * @return The string representing the font face of the style.
	  */
	public String getFontFace() {
		if (curTheme.fontFace == null || curTheme.fontFace.equals("")) {
			return curTheme.superStyle.getFontFace();
		}
		return curTheme.fontFace;
	}
	
	/** Get font posture of style. 
	  * @param theme The theme to look up font style for. If the theme does not exist, the default theme is used.
	  * @return The font posture of the style.
	  */
	public FontPosture getFontPosture(String theme) {
		ThemeEntry e = getThemeEntrySafe(theme);
		if (e.fontPosture == null && e.superStyle != null) {
			return e.superStyle.getFontPosture(theme);
		}
		return e.fontPosture;
	}

	/** Get font posture of style for active theme. 
	  * @return The font posture of the style.
	  */
	public FontPosture getFontPosture() {
		if (curTheme.fontPosture == null && curTheme.superStyle != null) {
			return curTheme.superStyle.getFontPosture();
		}
		return curTheme.fontPosture;
	}

	/** Get font posture of style. 
	  * @param theme The theme to look up font style for. If the theme does not exist, the default theme is used.
	  * @return The font posture of the style.
	  */
	public FontWeight getFontWeight(String theme) {
		ThemeEntry e = getThemeEntrySafe(theme);
		if (e.fontWeight == null && e.superStyle != null) {
			return e.superStyle.getFontWeight(theme);
		}
		return e.fontWeight;
	}

	/** Get font Weight of style for active theme. 
	  * @return The font weight of the style.
	  */
	public FontWeight getFontWeight() {
		if (curTheme.fontWeight == null && curTheme.superStyle != null) {
			return curTheme.superStyle.getFontWeight();
		}
		return curTheme.fontWeight;
	}

	
	/** Get font size of style. 
	  * @param theme The theme to look up font size for. If the theme does not exist, the default theme is used.
	  * @return The font size of the style.
	  */
	public int getFontSize(String theme) {
		ThemeEntry e = getThemeEntrySafe(theme);
		if (e.fontSize == INHERITED) {
			return e.superStyle.getFontSize(theme);
		}
		return e.fontSize;
	}
	
	/** Get font size of style for active theme. 
	  * @return The font size of the style.
	  */
	public int getFontSize() {
		if (curTheme.fontSize == INHERITED) {
			return curTheme.superStyle.getFontSize();
		}
		return curTheme.fontSize;
	}
	
	/** Get font of style. */
	public Font getFont() {
		if (curTheme.fontCache == null) {
			curTheme.fontCache = Font.font(getFontFace(), getFontWeight(), getFontPosture(), getFontSize());
		}
		return curTheme.fontCache;
	}

	/** Get superstyle. 
	  * @param theme The theme to look up superstyle for. If the theme does not exist, the default theme is used.
	  * @return The superstyle of this style or <Code>null</Code> if this style has no superstyle.
	  */
	REDStyle getSuperStyle(String theme) {
		return getThemeEntrySafe(theme).superStyle;
	}

	/** Get superstyle. 
	  * @return The superstyle of this style or <Code>null</Code> if this style has no superstyle.
	  */
	public REDStyle getSuperStyle() {
		return curTheme.superStyle;
	}
	
	/** Fixup superstyle. After making a copy of a style hierarchy (REDStyleManager.deepCopy()), super styles must be fixed.
	  * This method will change the super styles of all its themes by looking up the current super style as key in the passed hashmap and 
	  * setting the super style to the found value.
	  * @param map A map containing oldStyle => newStyle mappings.
	  */
	void fixupSuperstyle(HashMap<REDStyle, REDStyle> map) {
		Iterator<ThemeEntry> iter = themes.values().iterator();
		while (iter.hasNext()) {
			ThemeEntry e = iter.next();
			if (e.superStyle != null) {
				REDStyle newSuper = map.get(e.superStyle);
				if (newSuper != null) {
					e.superStyle = newSuper;
					e.fontCache = null;
				}
			}
		}
	}
	
	/** Get superstyle relationship.
	  * @param s The style to check against.
	  * @return <Code>true</Code> if this is equal or a substyle of <Code>s</Code>
	  */
	public boolean isA(REDStyle s) {
		return s == this || curTheme.superStyle != null && curTheme.superStyle.isA(s);
	}
	
	/** Check for theme entry.
	  * @param theme The theme to check for.
	  * @param return <Code>true</Code>, if this style has the given Theme defined. False otherwise.
	  */
	public boolean hasTheme(String theme) {
		return themes.get(theme) != null;
	}
	
	/** Check for definition of foreground.
	  * @param theme The theme to check for. If the theme does not exist, the default theme is used.
	  * @return <Code>true</Code> if the given theme defines the foreground color without using the superstyle.
	  */
	public boolean definesForeground(String theme) {
		return getThemeEntrySafe(theme).foreground != null;
	}
	
	/** Check for definition of background.
	  * @param theme The theme to check for. If the theme does not exist, the default theme is used.
	  * @return <Code>true</Code> if the given theme defines the background color without using the superstyle.
	  */
	public boolean definesBackground(String theme) {
		return getThemeEntrySafe(theme).background != null;
	}
	
	/** Check for definition of font face.
	  * @param theme The theme to check for. If the theme does not exist, the default theme is used.
	  * @return <Code>true</Code> if the given theme defines the font face without using the superstyle.
	  */
	public boolean definesFontFace(String theme) {
		return getThemeEntrySafe(theme).fontFace != null;
	}
	
	/** Check for definition of font size.
	  * @param theme The theme to check for. If the theme does not exist, the default theme is used.
	  * @return <Code>true</Code> if the given theme defines the font size without using the superstyle.
	  */
	public boolean definesFontSize(String theme) {
		return getThemeEntrySafe(theme).fontSize != INHERITED;
	}
	
	/** Check for definition of font posture.
	  * @param theme The theme to check for. If the theme does not exist, the default theme is used.
	  * @return <Code>true</Code> if the given theme defines the font style without using the superstyle.
	  */
	public boolean definesFontPosture(String theme) {
		return getThemeEntrySafe(theme).fontPosture != null;
	}
	
	
	/** Check for definition of font weight.
	  * @param theme The theme to check for. If the theme does not exist, the default theme is used.
	  * @return <Code>true</Code> if the given theme defines the font style without using the superstyle.
	  */
	public boolean definesFontWeight(String theme) {
		return getThemeEntrySafe(theme).fontWeight != null;
	}	
	
	/** Check for definition of lining.
	  * @param theme The theme to check for. If the theme does not exist, the default theme is used.
	  * @return <Code>true</Code> if the given theme defines the lining without using the superstyle.
	  */
	public boolean definesLining(String theme) {
		return getThemeEntrySafe(theme).lining != null;
	}
	
	/** Check for definition of superstyle.
	  * @param theme The theme to check for. If the theme does not exist, the default theme is used.
	  * @return <Code>true</Code> if the given theme has a superstyle.
	  */
	public boolean definesSuperStyle(String theme) {
		return getThemeEntrySafe(theme).superStyle != null;
	}
	
	/** Iterate alphabetically over defined theme names.
	  * @return An iterator which will return String objects in ascending order, representing the defined themes of this style.
	  */
	public Iterator<String> themeIterator() {
		return themes.keySet().iterator();
	}
	
	/** Put key <-> value mapping into style.
	  * @param key The key of the mapping.
	  * @param value The value of the mapping.
	  */
	void put(Object key, Object value) {
		mappings.put(key, value);
	}
	
	/** Remove key <-> value mapping from style.
	  * @param key The key of the mapping to remove.
	  */
	void remove(Object key) {
		mappings.remove(key);
	}
	
	/** Get value from style. 
	  * @param key The key to get mapped value for.
	  * @return The value associated with the given key, or <Code>null</Code> if it has not got a value for the given key.
	  */
	Object get(Object key) {
		return mappings.get(key);
	}
		
	/** Auxiliary XML writing method. */
	private void writeContentEntity(REDXMLHandlerWriter handler, String tagName, Object content) throws IOException {
		if (content != null) {
			handler.writeEntity(tagName, null, "" + content);
		}
	}
	
	/** Auxiliary XML writing method. */
	private void writeColorEntity(REDXMLHandlerWriter handler, String tagName, Color color) throws IOException {
		if (color != null) {
			handler.writeEntity(tagName, "red=\"" + color.getRed() + "\" green=\"" + color.getGreen() + "\" blue=\"" + color.getBlue() + "\"", null);
		}
	}
	
	public void writeTheme(String theme, REDXMLHandlerWriter handler) throws IOException {
		ThemeEntry e = (ThemeEntry) getThemeEntry(theme);
		if (theme.equals("Default")) {
			handler.openTag("Style", "id=\"" + getName() + "\"");
		}
		else {
			handler.openTag("Style", "id=\"" + getName() + "\" theme=\"" + theme + "\"");
		}
				
		writeContentEntity(handler, "FontFace", e.fontFace);
		if (e.fontSize != INHERITED) {
			writeContentEntity(handler, "FontSize", "" + e.fontSize);
		}
		writeContentEntity(handler, "FontPosture", e.fontPosture);
		writeContentEntity(handler, "FontWeight", e.fontWeight);
		writeContentEntity(handler, "Lining", e.lining);	
		writeColorEntity(handler, "Foreground", e.foreground);
		writeColorEntity(handler, "Background", e.background);
		if (e.superStyle != null) {
			writeContentEntity(handler, "Super", e.superStyle.getName());
		}
		
		handler.closeTag();
	}
	
	
	void setManager(REDStyleManagerImpl manager) {
		fManager = manager;
	}
	
	public boolean equalsTheme(String theme, REDStyle that) {
		ThemeEntry thisTheme = getThemeEntry(theme);
		ThemeEntry thatTheme = that.getThemeEntry(theme);
		return thisTheme != null && thatTheme != null && thisTheme.equalsEntry(thatTheme);
	}

	class ThemeEntry {
		private Color foreground;
		private Color background;
		private REDLining lining;
		private String fontFace;
		private FontPosture fontPosture;
		private FontWeight fontWeight;
		private int fontSize;
		private Font fontCache;
		private REDStyle superStyle;
		private File backingStore;
		
		boolean equalsEntry(ThemeEntry that) {
			return fontSize == that.fontSize && 
				lining == that.lining &&
				(fontPosture == null && that.fontPosture == null || fontPosture != null && fontPosture.equals(that.fontPosture)) && 
				(fontWeight == null && that.fontWeight == null || fontWeight != null && fontWeight.equals(that.fontWeight)) && 
				(fontFace == null && that.fontFace == null || fontFace != null && fontFace.equals(that.fontFace)) &&
				(foreground == null && that.foreground == null || foreground != null && foreground.equals(that.foreground)) && 
				(background == null && that.background == null || background != null && background.equals(that.background)) && 
				(superStyle == null && that.superStyle == null || superStyle != null && superStyle.getName().equals(that.superStyle.getName()));
		}
		
		ThemeEntry copy() {
			ThemeEntry e = new ThemeEntry();
			e.foreground = foreground;
			e.background = background;
			e.lining = lining;
			e.fontFace = fontFace;
			e.fontPosture = fontPosture;
			e.fontWeight = fontWeight;
			e.fontSize = fontSize;
			e.fontCache = fontCache;
			e.superStyle = superStyle;
			e.backingStore = backingStore;
			return e;
		}
	}		

}
