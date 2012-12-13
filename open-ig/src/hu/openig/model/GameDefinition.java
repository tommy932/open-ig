/*
 * Copyright 2008-2012, David Karnok 
 * The file is part of the Open Imperium Galactica project.
 * 
 * The code should be distributed under the LGPL license.
 * See http://www.gnu.org/licenses/lgpl.html for details.
 */

package hu.openig.model;

import hu.openig.utils.Exceptions;
import hu.openig.utils.U;
import hu.openig.utils.XElement;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The game definition used on the single player screen.
 * @author akarnokd, 2010.01.16.
 */
public class GameDefinition {
	/** The teaser image to display. */
	public BufferedImage image;
	/** The image resource path. */
	public String imagePath;
	/** The intro media to play on start. */
	public String intro;
	/** The title text. */
	public final Map<String, String> titles = U.newLinkedHashMap();
	/** The campaign description. */
	public final Map<String, String> descriptions = U.newLinkedHashMap();
	/** The game name. */
	public String name;
	/** The starting level of the game. */
	public int level;
	/** The immediately available technology level. */
	public int techLevel;
	/** Resource location. */
	@LoadField
	public String battle;
	/** Resource location. */
	@LoadField
	public String bridge;
	/** Resource location. */
	@LoadField
	public String buildings;
	/** Resource location. */
	@LoadField
	public String diplomacy;
	/** Resource location. */
	@LoadField
	public String galaxy;
	/** Resource location. */
	@LoadField
	public String planets;
	/** Resource location. */
	@LoadField
	public String players;
	/** Resource location. */
	@LoadField
	public String talks;
	/** Resource location. */
	@LoadField
	public String tech;
	/** Resource location. */
	@LoadField
	public String test;
	/** Resource location. */
	@LoadField
	public String walks;
	/** The scripting definition. */
	@LoadField
	public String scripting;
	/** The optional chat definition. */
	@LoadField
	public String chats;
	/** Spying. */
	@LoadField
	public String spies;
	/** The game parameters. */
	public final Map<String, String> parameters = new LinkedHashMap<String, String>();
	/** The traits to apply to the main player. */
	public Traits traits;
	/** The referenced labels. */
	public final List<String> labels = U.newArrayList();
	/** Skirmish hint: do not load planetary buildings. */
	public boolean noPlanetBuildings;
	/** Skirmish hint: do not load planetary inventory. */
	public boolean noPlanetInventory;
	/** Skirmish hint: do not assign owner and population to the planet. */
	public boolean noPlanetOwner;
	/**
	 * Parse the definition from the XML file.
	 * @param xdef the XML data
	 */
	public void parse(XElement xdef) {
		for (XElement texts : xdef.childrenWithName("texts")) {
			String lang = texts.get("language");
			titles.put(lang, texts.childValue("title"));
			descriptions.put(lang, texts.childValue("description"));
		}
		
		intro = xdef.childValue("intro");
		imagePath = xdef.childValue("image");
		level = xdef.intValue("level");
		techLevel = xdef.intValue("tech-level", 0);
		
		for (XElement xlabels : xdef.childrenWithName("label")) {
			labels.add(xlabels.content);
		}
		
		for (Field f : GameDefinition.class.getDeclaredFields()) {
			if (f.isAnnotationPresent(LoadField.class)) {
				try {
					f.set(this, xdef.childValue(f.getName()));
				} catch (IllegalAccessException ex) {
					Exceptions.add(ex);
				}
			}
		}
		XElement xp = xdef.childElement("parameters");
		if (xp != null) {
			parameters.putAll(xp.attributes());
		}
	}
	/**
	 * Save the definition.
	 * @param xout the output XML
	 */
	public void save(XElement xout) {
		// store descriptions
		Set<String> languages = new LinkedHashSet<String>();
		languages.addAll(titles.keySet());
		languages.addAll(descriptions.keySet());
		for (String lang : languages) {
			XElement xtext = xout.add("texts");
			xtext.set("language", lang);
			xtext.add("title", titles.get(lang));
			xtext.add("description", descriptions.get(lang));
		}
		xout.add("intro", intro);
		xout.add("image", imagePath);
		xout.add("level", level);
		xout.add("tech-level", techLevel);
		
		for (String lr : labels) {
			xout.add("label", lr);
		}

		for (Field f : GameDefinition.class.getDeclaredFields()) {
			if (f.isAnnotationPresent(LoadField.class)) {
				try {
					xout.add(f.getName(), f.get(this));
				} catch (IllegalAccessException ex) {
					Exceptions.add(ex);
				}
			}
		}

		XElement xp = xout.add("parameters");
		for (Map.Entry<String, String> e : parameters.entrySet()) {
			xp.set(e.getKey(), e.getValue());
		}
	}
	/**
	 * Parse the game definition from.
	 * @param rl the resource locator
	 * @param name the definition/game name
	 * @return the parsed definition.
	 */
	public static GameDefinition parse(ResourceLocator rl, String name) {
		GameDefinition result = new GameDefinition();
		result.name = name;
		XElement root = rl.getXML(name + "/definition");
		result.parse(root);
		if (result.imagePath != null) {
			result.image = rl.getImage(result.imagePath);
		}
		
		return result;
	}
	/**
	 * Returns the title of the game in the specified language, or in English or
	 * the first specified language available.
	 * @param language the language
	 * @return the title or empty string
	 */
	public String getTitle(String language) {
		String title = titles.get(language);
		if (title == null) {
			title = titles.get("en");
		}
		if (title == null && !titles.isEmpty()) {
			title = titles.values().iterator().next();
		}
		return title != null ? title : "";
	}
	/**
	 * Returns the description of the game in the specified language, or in English or
	 * the first specified language available.
	 * @param language the language
	 * @return the title or empty string
	 */
	public String getDescription(String language) {
		String description = descriptions.get(language);
		if (description == null) {
			description = descriptions.get("en");
		}
		if (description == null && !descriptions.isEmpty()) {
			description = descriptions.values().iterator().next();
		}
		return description != null ? description : "";
	}
}
