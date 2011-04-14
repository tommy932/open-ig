/*
 * Copyright 2008-2011, David Karnok 
 * The file is part of the Open Imperium Galactica project.
 * 
 * The code should be distributed under the LGPL license.
 * See http://www.gnu.org/licenses/lgpl.html for details.
 */

package hu.openig.screen.items;

import hu.openig.core.Act;
import hu.openig.core.Action1;
import hu.openig.model.Fleet;
import hu.openig.model.FleetKnowledge;
import hu.openig.model.FleetMode;
import hu.openig.model.FleetStatistics;
import hu.openig.model.InventoryItem;
import hu.openig.model.InventorySlot;
import hu.openig.model.Planet;
import hu.openig.model.PlanetStatistics;
import hu.openig.model.ResearchSubCategory;
import hu.openig.model.ResearchType;
import hu.openig.model.Screens;
import hu.openig.model.SelectionMode;
import hu.openig.render.RenderTools;
import hu.openig.screen.EquipmentConfigure;
import hu.openig.screen.ScreenBase;
import hu.openig.screen.TechnologySlot;
import hu.openig.screen.VehicleCell;
import hu.openig.screen.VehicleList;
import hu.openig.ui.UIImage;
import hu.openig.ui.UIImageButton;
import hu.openig.ui.UIImageTabButton;
import hu.openig.ui.UILabel;
import hu.openig.ui.UIMouse;
import hu.openig.ui.UIMouse.Type;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The equipment screen.
 * @author akarnokd, 2010.01.11.
 */
public class EquipmentScreen extends ScreenBase {
	/** The panel base rectangle. */
	final Rectangle base = new Rectangle();
	/** The left panel. */
	final Rectangle leftPanel = new Rectangle();
	/** The right panel. */
	final Rectangle rightPanel = new Rectangle();
	/** The info button. */
	UIImageButton infoButton;
	/** The production button. */
	UIImageButton productionButton;
	/** The research button. */
	UIImageButton researchButton;
	/** The bridge button. */
	UIImageButton bridgeButton;
	/** The large vertical starmap button right to the minimap. */
	UIImageButton starmapButton;
	/** The large vertical colony button right to the minimap. */
	UIImageButton colonyButton;
	/** The placeholder for the no-research-button case. */
	UIImage noResearch;
	/** The placeholder for the no-production-button case. */
	UIImage noProduction;
	/** Placeholder for the no-starmap-button case. */
	UIImage noStarmap;
	/** Placeholder for the no-colony-button case. */
	UIImage noColony;
	/** The previous button. */
	UIImageButton prev;
	/** The next button. */
	UIImageButton next;
	/** The fleet name. */
	UILabel fleetName;
	/** The spaceship count label. */
	UILabel spaceshipsLabel;
	/** The fighters count label. */
	UILabel fightersLabel;
	/** The vehicles count label. */
	UILabel vehiclesLabel;
	/** The spaceship count label. */
	UILabel spaceshipsMaxLabel;
	/** The fighters count label. */
	UILabel fightersMaxLabel;
	/** The vehicles count label. */
	UILabel vehiclesMaxLabel;
	/** The fleet status label. */
	UILabel fleetStatusLabel;
	/** Secondary label. */
	UILabel secondaryLabel;
	/** Secondary value.*/
	UILabel secondaryValue;
	/** Secondary fighters. */
	UILabel secondaryFighters;
	/** Secondary vehicles. */
	UILabel secondaryVehicles;
	/** Empty category image. */
	UIImage battleshipsAndStationsEmpty;
	/** Empty category  image. */
	UIImage cruisersEmpty;
	/** Empty category  image. */
	UIImage fightersEmpty;
	/** Empty category  image. */
	UIImage tanksEmpty;
	/** Empty category  image. */
	UIImage vehiclesEmpty;
	/** Battleships category. */
	UIImageTabButton battleships;
	/** Cruisers category. */
	UIImageTabButton cruisers;
	/** Fighters category. */
	UIImageTabButton fighters;
	/** Stations category. */
	UIImageTabButton stations;
	/** Tanks category. */
	UIImageTabButton tanks;
	/** Vehicles category. */
	UIImageTabButton vehicles;
	/** End splitting. */
	UIImageButton endSplit;
	/** End joining. */
	UIImageButton endJoin;
	/** No planet nearby error. */
	UIImage noPlanetNearby;
	/** No spaceport on the nearby planet. */
	UIImage noSpaceport;
	/** Not your planet. */
	UIImage notYourPlanet;
	/** New fleet button. */
	UIImageButton newButton;
	/** Add ship to fleet. */
	UIImageButton addButton;
	/** Delete ship to fleet. */
	UIImageButton delButton;
	/** Delete a fleet. */
	UIImageButton deleteButton;
	/** Transfer ships between fleets or planet. */
	UIImageButton transferButton;
	/** Split a fleet. */
	UIImageButton splitButton;
	/** Join two fleets. */
	UIImageButton joinButton;
	/** Move one left. */
	UIImageButton left1;
	/** Move multiple left. */
	UIImageButton left2;
	/** Move all left. */
	UIImageButton left3;
	/** Move one right. */
	UIImageButton right1;
	/** Move multiple right.*/
	UIImageButton right2;
	/** Move all right.*/
	UIImageButton right3;
	/** Add one inner equipment. */
	UIImageButton addOne;
	/** Remove one inner equipment. */
	UIImageButton removeOne;
	/** The fleet listing button. */
	UIImageButton listButton;
	/** Inner equipment rectangle. */
	Rectangle innerEquipment;
	/** Show the inner equipment rectangle? */
	boolean innerEquipmentVisible;
	/** Inner equipment name. */
	UILabel innerEquipmentName;
	/** Inner equipment value. */
	UILabel innerEquipmentValue;
	/** Inner equipment separator. */
	UILabel innerEquipmentSeparator;
	/** The name and type of the current selected ship or equipment. */
	UILabel selectedNameAndType;
	/** The planet image. */
	UIImage planet;
	/** The equipment slot locations. */
	final List<TechnologySlot> slots = new ArrayList<TechnologySlot>();
	/** The rolling disk animation timer. */
	Closeable animation;
	/** The current animation step counter. */
	int animationStep;
	/** The equipment mode. */
	Screens mode;
	/** The left fighter cells. */
	final List<VehicleCell> leftFighterCells = new ArrayList<VehicleCell>();
	/** The left tank cells. */
	final List<VehicleCell> leftTankCells = new ArrayList<VehicleCell>();
	/** The right fighter cells. */
	final List<VehicleCell> rightFighterCells = new ArrayList<VehicleCell>();
	/** The right tank cells. */
	final List<VehicleCell> rightTankCells = new ArrayList<VehicleCell>();
	/** The left vehicle listing. */
	VehicleList leftList;
	/** The right vehicle listing. */
	VehicleList rightList;
	/** The currently displayed planet. */
	Planet planetShown;
	/** The currently displayed fleet. */
	Fleet fleetShown;
	/** The configuration component. */
	EquipmentConfigure configure;
	@Override
	public void onInitialize() {
		base.setBounds(0, 0, 
				commons.equipment().base.getWidth(), commons.equipment().base.getHeight());
		
		infoButton = new UIImageButton(commons.common().infoButton);
		bridgeButton = new UIImageButton(commons.common().bridgeButton);
		researchButton = new UIImageButton(commons.research().research);
		productionButton = new UIImageButton(commons.research().production);
		
		researchButton.onClick = new Act() {
			@Override
			public void act() {
				displaySecondary(Screens.RESEARCH);
			}
		};
		productionButton.onClick = new Act() {
			@Override
			public void act() {
				displaySecondary(Screens.PRODUCTION);
			}
		};
		bridgeButton.onClick = new Act() {
			@Override
			public void act() {
				displayPrimary(Screens.BRIDGE);
			}
		};
		infoButton.onClick = new Act() {
			@Override
			public void act() {
				displaySecondary(Screens.INFORMATION_INVENTIONS);
			}
		};
		
		starmapButton = new UIImageButton(commons.equipment().starmap);
		starmapButton.onClick = new Act() {
			@Override
			public void act() {
				displayPrimary(Screens.STARMAP);
			}
		};
		colonyButton = new UIImageButton(commons.equipment().planet);
		colonyButton.onClick = new Act() {
			@Override
			public void act() {
				displayPrimary(Screens.COLONY);
			}
		};
		
		noResearch = new UIImage(commons.common().emptyButton);
		noResearch.visible(false);
		noProduction = new UIImage(commons.common().emptyButton);
		noProduction.visible(false);
		noStarmap = new UIImage(commons.equipment().buttonMapEmpty);
		noStarmap.visible(false);
		noColony = new UIImage(commons.equipment().buttonMapEmpty);
		noColony.visible(false);
		
		endSplit = new UIImageButton(commons.equipment().endSplit);
		endSplit.visible(false);
		endJoin = new UIImageButton(commons.equipment().endJoin);
		endJoin.visible(false);
		
		prev = new UIImageButton(commons.starmap().backwards);
		prev.onClick = new Act() {
			@Override
			public void act() {
				doPrev();
			}
		};
		prev.setDisabledPattern(commons.common().disabledPattern);
		next = new UIImageButton(commons.starmap().forwards);
		next.onClick = new Act() {
			@Override
			public void act() {
				doNext();
			}
		};
		next.setDisabledPattern(commons.common().disabledPattern);
		
		fleetName = new UILabel("Fleet1", 14, commons.text());
		fleetName.color(0xFFFF0000);
		
		spaceshipsLabel = new UILabel(format("equipment.spaceships", 0), 10, commons.text());
		fightersLabel = new UILabel(format("equipment.fighters", 0), 10, commons.text());
		vehiclesLabel = new UILabel(format("equipment.vehicles", 0), 10, commons.text());
		
		spaceshipsMaxLabel = new UILabel(format("equipment.max", 25), 10, commons.text());
		fightersMaxLabel = new UILabel(format("equipment.maxpertype", 30), 10, commons.text());
		vehiclesMaxLabel = new UILabel(format("equipment.max", 0), 10, commons.text());
		
		fleetStatusLabel = new UILabel("TODO", 10, commons.text());

		secondaryLabel = new UILabel(get("equipment.secondary"), 10, commons.text());
		secondaryValue = new UILabel("TODO", 10, commons.text());
		secondaryValue.color(0xFFFF0000);
		
		secondaryFighters = new UILabel(format("equipment.fighters", 0), 10, commons.text());
		secondaryVehicles = new UILabel(format("equipment.vehiclesandmax", 0, 8), 10, commons.text());
		
		battleshipsAndStationsEmpty = new UIImage(commons.equipment().categoryEmpty);
		battleshipsAndStationsEmpty.visible(false);
		cruisersEmpty = new UIImage(commons.equipment().categoryEmpty);
		cruisersEmpty.visible(false);
		fightersEmpty = new UIImage(commons.equipment().categoryEmpty);
		fightersEmpty.visible(false);
		tanksEmpty = new UIImage(commons.equipment().categoryEmpty);
		tanksEmpty.visible(false);
		vehiclesEmpty = new UIImage(commons.equipment().categoryEmpty);
		vehiclesEmpty.visible(false);
		
		battleships = new UIImageTabButton(commons.equipment().categoryBattleships);
		cruisers = new UIImageTabButton(commons.equipment().categoryCruisers);
		fighters = new UIImageTabButton(commons.equipment().categoryFighers);
		stations = new UIImageTabButton(commons.equipment().categorySpaceStations);
		stations.visible(false);
		tanks = new UIImageTabButton(commons.equipment().categoryTanks);
		vehicles = new UIImageTabButton(commons.equipment().categoryVehicles);
		
		battleships.onPress = categoryAction(ResearchSubCategory.SPACESHIPS_BATTLESHIPS);
		cruisers.onPress = categoryAction(ResearchSubCategory.SPACESHIPS_CRUISERS);
		fighters.onPress = categoryAction(ResearchSubCategory.SPACESHIPS_FIGHTERS);
		stations.onPress = categoryAction(ResearchSubCategory.SPACESHIPS_STATIONS);
		tanks.onPress = categoryAction(ResearchSubCategory.WEAPONS_TANKS);
		vehicles.onPress = categoryAction(ResearchSubCategory.WEAPONS_VEHICLES);
		
		slots.clear();
		
		noPlanetNearby = new UIImage(commons.equipment().noPlanetNearby);
		noPlanetNearby.visible(false);
		noSpaceport = new UIImage(commons.equipment().noSpaceport);
		noSpaceport.visible(false);
		notYourPlanet = new UIImage(commons.equipment().notYourplanet);
		notYourPlanet.visible(false);
		
		newButton = new UIImageButton(commons.equipment().newFleet);
		newButton.visible(false);
		newButton.onClick = new Act() {
			@Override
			public void act() {
				doCreateFleet();
			}
		};
		
		addButton = new UIImageButton(commons.equipment().add);
		addButton.visible(false);
		addButton.onClick = new Act() {
			@Override
			public void act() {
				doAddItem();
			}
		};
		addButton.setHoldDelay(100);
		
		delButton = new UIImageButton(commons.equipment().remove);
		delButton.visible(false);
		delButton.onClick = new Act() {
			@Override
			public void act() {
				doRemoveItem();
			}
		};
		delButton.setHoldDelay(100);
		
		deleteButton = new UIImageButton(commons.equipment().delete);
		deleteButton.visible(false);
		deleteButton.onClick = new Act() {
			@Override
			public void act() {
				// delete fleet
			}
		};
		
		transferButton = new UIImageButton(commons.equipment().transfer);
		transferButton.visible(false);
		splitButton = new UIImageButton(commons.equipment().split);
		splitButton.visible(false);

		addOne = new UIImageButton(commons.equipment().addOne);
		addOne.visible(false);
		addOne.onClick = new Act() {
			@Override
			public void act() {
				doAddOne();
			}
		};
		addOne.setHoldDelay(100);
		
		removeOne = new UIImageButton(commons.equipment().removeOne);
		removeOne.visible(false);
		removeOne.onClick = new Act() {
			@Override
			public void act() {
				doRemoveOne();
			}
		};
		removeOne.setHoldDelay(100);

		
		
		joinButton = new UIImageButton(commons.equipment().join);
		joinButton.visible(false);
		
		left1 = new UIImageButton(commons.equipment().moveLeft1);
		left1.visible(false);
		left2 = new UIImageButton(commons.equipment().moveLeft2);
		left2.visible(false);
		left3 = new UIImageButton(commons.equipment().moveLeft3);
		left3.visible(false);
		right1 = new UIImageButton(commons.equipment().moveRight1);
		right1.visible(false);
		right2 = new UIImageButton(commons.equipment().moveRight2);
		right2.visible(false);
		right3 = new UIImageButton(commons.equipment().moveRight3);
		right3.visible(false);

		listButton = new UIImageButton(commons.equipment().list);
		
		innerEquipment = new Rectangle();
		innerEquipmentName = new UILabel("TODO", 7, commons.text());
		innerEquipmentName.visible(false);
		innerEquipmentValue = new UILabel(format("equipment.innercount", 0, 0), 7, commons.text());
		innerEquipmentValue.visible(false);
		innerEquipmentSeparator = new UILabel("-----", 7, commons.text());
		innerEquipmentSeparator.visible(false);
		
		selectedNameAndType = new UILabel(format("equipment.selectednametype", "TODO", "TODO"), 10, commons.text());
		selectedNameAndType.visible(false);
		selectedNameAndType.color(0xFF6DB269);
		
		planet = new UIImage(commons.equipment().planetOrbit);
		leftList = new VehicleList(commons);
		
		configure = new EquipmentConfigure();
		configure.z = -1;
		configure.size(298, 128);
		configure.onSelect = new Action1<InventorySlot>() {
			@Override
			public void invoke(InventorySlot value) {
				onSelectInventorySlot(value);
			}
		};
		
		Action1<ResearchType> selectSlot = new Action1<ResearchType>() {
			@Override
			public void invoke(ResearchType value) {
				onSelectResearchSlot(value);
			}
		};
		Action1<ResearchType> selectVehicle = new Action1<ResearchType>() {
			@Override
			public void invoke(ResearchType value) {
				onSelectVehicleSlot(value);
			}
		};
		leftList.onSelect = new Action1<InventoryItem>() {
			@Override
			public void invoke(InventoryItem value) {
				onSelectInventoryItem(value);
			}
		};
	
		for (int i = 0; i < 6; i++) {
			final TechnologySlot ts = new TechnologySlot(commons);
			ts.visible(false);
			ts.onPress = selectSlot; 
			
			slots.add(ts);
		}
		
		for (int i = 0; i < 6; i++) {
			VehicleCell vc = new VehicleCell(commons);
			vc.onSelect = selectVehicle;
			vc.topCenter = true;
			leftFighterCells.add(vc);

			vc = new VehicleCell(commons);
			vc.topCenter = true;
			vc.onSelect = selectVehicle;
			rightFighterCells.add(vc);
		}
		for (int i = 0; i < 7; i++) {
			VehicleCell vc = new VehicleCell(commons);
			vc.onSelect = selectVehicle;
			leftTankCells.add(vc);
			
			vc = new VehicleCell(commons);
			vc.onSelect = selectVehicle;
			rightTankCells.add(vc);
		}
		
		
		rightList = new VehicleList(commons);
		rightList.visible(false);
		
		add(leftFighterCells);
		add(rightFighterCells);
		add(leftTankCells);
		add(rightTankCells);
		add(slots);
		addThis();
	}
	/**
	 * @param cat the category to set 
	 * @return Create an action which selects the given category. 
	 */
	Act categoryAction(final ResearchSubCategory cat) {
		return new Act() {
			@Override
			public void act() {
				displayCategory(cat);
				configure.selectedSlot = null;
			}
		};
	}
	@Override
	public void onEnter(Screens mode) {
		if (mode == null) {
			this.mode = Screens.EQUIPMENT_FLEET;
		} else {
			this.mode = mode; 
		}
		ResearchType rt = research();
		if (rt == null) {
			List<ResearchType> rts = world().getResearch();
			if (rts.size() > 0) {
				rt = rts.get(0);
				world().selectResearch(rt);
			}
		}
		displayCategory(rt.category);
		doSelectVehicle(rt);
		updateCurrentInventory();
		configure.type = rt;
		configure.item = leftList.selectedItem;
		configure.selectedSlot = null;
		
		
		animation = commons.register(100, new Act() {
			@Override
			public void act() {
				doAnimation();
			}
		});

	}

	@Override
	public void onLeave() {
		close0(animation);
		animation = null;
	}

	@Override
	public void onFinish() {
	}

	@Override
	public void onResize() {
		RenderTools.centerScreen(base, width, height, true);
		
		leftPanel.setBounds(base.x + 1, base.y + 1, 318, 198);
		rightPanel.setBounds(leftPanel.x + leftPanel.width + 2, leftPanel.y, 318, 198);
		infoButton.location(base.x + 535, base.y + 303 - 20);
		productionButton.location(infoButton.x, infoButton.y + infoButton.height);
		researchButton.location(productionButton.x, productionButton.y + productionButton.height);
		bridgeButton.location(researchButton.x, researchButton.y + researchButton.height);
		
		starmapButton.location(base.x + 479, base.y + 303 - 20);
		colonyButton.location(starmapButton.x + 26, starmapButton.y);
		
		noResearch.location(researchButton.location());
		noProduction.location(productionButton.location());
		noStarmap.location(starmapButton.location());
		noColony.location(colonyButton.location());
		
		endJoin.location(infoButton.location());
		endSplit.location(infoButton.location());
		
		prev.location(base.x + 151, base.y + 304 - 20);
		next.location(base.x + 152 + 50, base.y + 304 - 20);
		
		fleetName.location(base.x + 3, base.y + 308 - 20);
		fleetName.width = 147;
		
		spaceshipsLabel.location(fleetName.x + 3, fleetName.y + 20);
		fightersLabel.location(spaceshipsLabel.x, spaceshipsLabel.y + 14);
		vehiclesLabel.location(fightersLabel.x, fightersLabel.y + 14);
		fleetStatusLabel.location(vehiclesLabel.x, vehiclesLabel.y + 14);
		
		spaceshipsMaxLabel.location(spaceshipsLabel.x + 110, spaceshipsLabel.y);
		fightersMaxLabel.location(fightersLabel.x + 110, fightersLabel.y);
		vehiclesMaxLabel.location(vehiclesLabel.x + 110, vehiclesLabel.y);
		
		secondaryLabel.location(fightersLabel.x, fleetStatusLabel.y + 22);
		secondaryValue.location(secondaryLabel.x + secondaryLabel.width + 8, secondaryLabel.y);
		secondaryFighters.location(secondaryLabel.x, secondaryLabel.y + 14);
		secondaryVehicles.location(secondaryLabel.x, secondaryFighters.y + 14);
		
		battleships.location(base.x + 2, base.y + 435 - 20);
		stations.location(battleships.location());
		cruisers.location(battleships.x + 50, battleships.y);
		fighters.location(cruisers.x + 50, cruisers.y);
		tanks.location(fighters.x + 50, fighters.y);
		vehicles.location(tanks.x + 50, tanks.y);
		
		battleshipsAndStationsEmpty.location(battleships.location());
		cruisersEmpty.location(cruisers.location());
		fightersEmpty.location(fighters.location());
		tanksEmpty.location(tanks.location());
		vehiclesEmpty.location(vehicles.location());
		
		for (int i = 0; i < 6; i++) {
			slots.get(i).location(base.x + 2 + i * 106, base.y + 219 - 20);
			slots.get(i).size(106, 82);
		}

		noPlanetNearby.location(base.x + 242, base.y + 194 - 20);
		noSpaceport.location(noPlanetNearby.location());
		notYourPlanet.location(noSpaceport.location());
		
		transferButton.location(base.x + 401, base.y + 194 - 20);
		joinButton.location(transferButton.location());
		splitButton.location(base.x + 480, base.y + 194 - 20);
		
		newButton.location(base.x + 560, base.y + 194 - 20);
		addButton.location(base.x + 322, base.y + 194 - 20);
		addOne.location(addButton.location());
		deleteButton.location(base.x + 242, base.y + 194 - 20);
		delButton.location(deleteButton.location());
		removeOne.location(deleteButton.location());
		
		listButton.location(base.x + 620, base.y + 49 - 20);
		
		right1.location(base.x + 322, base.y + 191 - 20);
		right2.location(right1.x + 48, right1.y);
		right3.location(right2.x + 48, right2.y);
		left1.location(base.x + 272, base.y + 191 - 20);
		left2.location(left1.x - 48, left1.y);
		left3.location(left2.x - 48, left2.y);
		
		innerEquipment.setBounds(base.x + 325, base.y + 156 - 20, 120, 35);
		innerEquipmentName.location(innerEquipment.x + 5, innerEquipment.y + 4);
		innerEquipmentSeparator.location(innerEquipmentName.x, innerEquipmentName.y + 10);
		innerEquipmentValue.location(innerEquipmentName.x, innerEquipmentName.y + 20);
		
		selectedNameAndType.location(base.x + 326, base.y + 56 - 20);
		
		planet.location(leftPanel.x - 1, leftPanel.y - 1);
		
		for (int i = 0; i < 6; i++) {
			VehicleCell vc = leftFighterCells.get(i);
			vc.bounds(leftPanel.x + leftPanel.width - 2 - (6 - i) * 34, leftPanel.y + 2, 33, 38);

			vc = rightFighterCells.get(i);
			vc.bounds(rightPanel.x + 2 + i * 34, rightPanel.y + 2, 33, 38);
		}
		for (int i = 0; i < 7; i++) {
			VehicleCell vc = leftTankCells.get(i);
			vc.bounds(leftPanel.x + leftPanel.width - 2 - (7 - i) * 34, leftPanel.y + leftPanel.height - 56, 33, 28);
			
			vc = rightTankCells.get(i);
			vc.bounds(rightPanel.x + 2 + i * 34, rightPanel.y + rightPanel.height - 56, 33, 28);
		}

		leftList.bounds(leftPanel.x + 72, leftPanel.y + 40, leftPanel.width - 74, leftPanel.height - 56 - 38);
		rightList.bounds(rightPanel.x, rightPanel.y + 40, rightPanel.width - 22, rightPanel.height - 56 - 38);
		
		configure.bounds(rightPanel.x, rightPanel.y + 28, 298, 128);

	}
	@Override
	public void draw(Graphics2D g2) {
		RenderTools.darkenAround(base, width, height, g2, 0.5f, true);
		g2.drawImage(commons.equipment().base, base.x, base.y, null);
		
		update();
		
		super.draw(g2);

		if (innerEquipmentVisible) {
			g2.setColor(new Color(0xFF4D7DB6));
			g2.drawRect(innerEquipment.x, innerEquipment.y, innerEquipment.width - 1, innerEquipment.height - 1);
		}
	}
	/**
	 * Update the slot belonging to the specified technology.
	 * @param rt the research technology
	 */
	public void updateSlot(final ResearchType rt) {
		final TechnologySlot slot = slots.get(rt.index);
		slot.type = rt;
		slot.displayResearchCost = false;
		slot.displayProductionCost = false;
		slot.visible(true);
	}
	/**
	 * Update animating components.
	 */
	void doAnimation() {
		if (animationStep == Integer.MAX_VALUE) {
			animationStep = 0;
		} else {
			animationStep ++;
		}
		for (TechnologySlot sl : slots) {
			sl.animationStep = animationStep;
		}
		askRepaint(base);
	}
	@Override
	public boolean mouse(UIMouse e) {
		if (!base.contains(e.x, e.y) && e.has(Type.UP)) {
			hideSecondary();
			return true;
		} else {
			return super.mouse(e);
		}
	}
	@Override
	public Screens screen() {
		return mode;
	}
	@Override
	public void onEndGame() {
		for (TechnologySlot ts : slots) {
			ts.type = null;
		}
		clearCells(leftFighterCells);
		clearCells(rightFighterCells);
		clearCells(leftTankCells);
		clearCells(rightTankCells);
		
		leftList.clear();
		rightList.clear();
	}
	/** Create a new fleet. */
	void doCreateFleet() {
		Fleet f = new Fleet();
		f.id = world().fleetIdSequence++;
		f.owner = player();
		f.name = get("newfleet.name");
		f.x = planet().x + 5;
		f.y = planet().y + 5;
		
		player().currentFleet = f;
		player().selectionMode = SelectionMode.FLEET;
		player().fleets.put(f, FleetKnowledge.FULL);
	}
	/**
	 * Update the display values based on the current selection.
	 */
	void update() {
		innerEquipmentVisible = false;
		innerEquipmentName.visible(false);
		innerEquipmentSeparator.visible(false);
		innerEquipmentValue.visible(false);
		ResearchType rt = research();
		if (player().selectionMode == SelectionMode.PLANET) {

			PlanetStatistics ps = planet().getStatistics();
			
			newButton.visible(planet().owner == player() && ps.hasMilitarySpaceport);
			noSpaceport.visible(planet().owner == player() && !ps.hasMilitarySpaceport);
			notYourPlanet.visible(planet().owner != player());
			noPlanetNearby.visible(false);
			
			if (planetShown != planet()) {
				planetShown = planet();
				updateCurrentInventory();
			}
			
			planet.visible(true);
			List<Planet> planets = player().ownPlanets();
			Collections.sort(planets, Planet.NAME_ORDER);
			int idx = planets.indexOf(planet());
			prev.enabled(idx > 0);
			next.enabled(idx < planets.size() - 1);
			fleetName.text(planet().name);
			
			spaceshipsLabel.visible(false);
			spaceshipsMaxLabel.visible(false);
			fightersLabel.text(format("equipment.fighters", ps.fighterCount), true);
			vehiclesLabel.text(format("equipment.vehicles", ps.vehicleCount), true);
			
			if (ps.hasSpaceStation) {
				fightersMaxLabel.text(format("equipment.maxpertype", 30), true);
			} else {
				fightersMaxLabel.text(format("equipment.max", 0), true);
			}
			vehiclesMaxLabel.text(format("equipment.max", ps.vehicleMax), true);
			
			fleetStatusLabel.visible(false);
			
			secondaryLabel.visible(false);
			secondaryFighters.visible(false);
			secondaryVehicles.visible(false);
			secondaryValue.visible(false);
			
			prepareCells(planet(), null, leftFighterCells, leftTankCells);
			clearCells(rightFighterCells);
			clearCells(rightTankCells);
			
			battleships.visible(false);
			cruisers.visible(false);
			cruisersEmpty.visible(true);
			stations.visible(true);
			
			
			
			if (ps.hasMilitarySpaceport) {
				if (rt.category == ResearchSubCategory.SPACESHIPS_FIGHTERS) {
					addButton.visible(player().inventoryCount(rt) > 0
							&& planet().inventoryCount(rt, player()) < 30);
					delButton.visible(planet().inventoryCount(rt, player()) > 0);
				} else
				if (rt.category == ResearchSubCategory.WEAPONS_TANKS
						|| rt.category == ResearchSubCategory.WEAPONS_VEHICLES) {
					addButton.visible(player().inventoryCount(rt) > 0
							&& planet().inventoryCount(rt.category, player()) < ps.vehicleMax);
					delButton.visible(
							planet().inventoryCount(rt.category, player()) > 0);
				} else
				if (rt.category == ResearchSubCategory.SPACESHIPS_STATIONS) {
					addButton.visible(player().inventoryCount(rt) > 0
							&& planet().inventoryCount(rt.category, player()) < 3);
					delButton.visible(false);
				} else {
					addButton.visible(false);
					delButton.visible(false);
				}
			} else {
				addButton.visible(false);
				delButton.visible(false);
			}
			addOne.visible(false);
			removeOne.visible(false);
		} else {
			if (fleetShown != fleet()) {
				fleetShown = fleet();
				updateCurrentInventory();
			}
			
			Fleet f = fleet();
			FleetStatistics fs = f.getStatistics();
			
			prepareCells(null, fleet(), leftFighterCells, leftTankCells);

			PlanetStatistics ps = fs.planet != null ? fs.planet.getStatistics() : null;
			
			List<Fleet> fleets = player().ownFleets();
			int idx = fleets.indexOf(f);
			prev.enabled(idx > 0);
			next.enabled(idx < fleets.size() - 1);
			
			planet.visible(false);
			fleetName.text(f.name);
			spaceshipsLabel.text(format("equipment.spaceships", fs.cruiserCount), true).visible(true);
			spaceshipsMaxLabel.text(format("equipment.max", 25), true).visible(true);
			fightersLabel.text(format("equipment.fighters", fs.fighterCount), true);
			vehiclesLabel.text(format("equipment.vehicles", fs.vehicleCount), true);
			fightersMaxLabel.text(format("equipment.maxpertype", 30), true);
			vehiclesMaxLabel.text(format("equipment.max", fs.vehicleMax), true);
			
			
			fleetStatusLabel.visible(true);
			if (f.targetFleet == null && f.targetPlanet == null) {
				if (f.waypoints.size() > 0) {
					fleetStatusLabel.text(format("fleetstatus.moving"), true);
				} else {
					if (fs.planet != null) {
						fleetStatusLabel.text(format("fleetstatus.stopped.at", fs.planet.name), true);
					} else {
						fleetStatusLabel.text(format("fleetstatus.stopped"), true);
					}
				}
			} else {
				if (f.mode == FleetMode.ATTACK) {
					if (f.targetFleet != null) {
						fleetStatusLabel.text(format("fleetstatus.attack", f.targetFleet.name), true);
					} else {
						fleetStatusLabel.text(format("fleetstatus.attack", f.targetPlanet.name), true);
					}
				} else {
					if (f.targetFleet != null) {
						fleetStatusLabel.text(format("fleetstatus.moving.after", f.targetFleet.name), true);
					} else {
						fleetStatusLabel.text(format("fleetstatus.moving.to", f.targetPlanet.name), true);
					}
				}
			}
			
			secondaryLabel.visible(false);
			secondaryFighters.visible(false);
			secondaryVehicles.visible(false);
			secondaryValue.visible(false);
			
			battleships.visible(true);
			cruisers.visible(true);
			cruisersEmpty.visible(false);
			stations.visible(false);
			
			newButton.visible(ps != null && fs.planet.owner == f.owner && ps.hasMilitarySpaceport);
			noSpaceport.visible(ps != null && fs.planet.owner == f.owner && !ps.hasMilitarySpaceport);
			notYourPlanet.visible(ps != null && fs.planet.owner != f.owner);
			noPlanetNearby.visible(ps == null);

			if (ps != null && ps.hasMilitarySpaceport) {
				if (rt.category == ResearchSubCategory.SPACESHIPS_FIGHTERS) {
					addButton.visible(player().inventoryCount(rt) > 0
							&& fleet().inventoryCount(rt) < 30);
					delButton.visible(fleet().inventoryCount(rt) > 0);
				} else
				if (rt.category == ResearchSubCategory.WEAPONS_TANKS
						|| rt.category == ResearchSubCategory.WEAPONS_VEHICLES) {
					addButton.visible(player().inventoryCount(rt) > 0
							&& fleet().inventoryCount(rt.category) < fs.vehicleMax);
					delButton.visible(
							fleet().inventoryCount(rt.category) > 0);
				} else
				if (rt.category == ResearchSubCategory.SPACESHIPS_BATTLESHIPS) {
					addButton.visible(player().inventoryCount(rt) > 0
							&& fleet().inventoryCount(rt.category) < 3);
					delButton.visible(false);
				} else
				if (rt.category == ResearchSubCategory.SPACESHIPS_CRUISERS) {
					addButton.visible(player().inventoryCount(rt) > 0
							&& fleet().inventoryCount(rt.category) < 25);
					delButton.visible(false);
				} else {
					addButton.visible(false);
					delButton.visible(false);
				}

				if (configure.selectedSlot != null) {
					addOne.visible(
							player().inventoryCount(rt) > 0
							&& (configure.selectedSlot.type != rt || !configure.selectedSlot.isFilled())
					);
					removeOne.visible(
							configure.selectedSlot.type != null && configure.selectedSlot.count > 0
					);
					innerEquipmentVisible = true;
					innerEquipmentName.text(get("inventoryslot." + configure.selectedSlot.slot.id), true).visible(true);
					if (configure.selectedSlot.type != null) {
						innerEquipmentSeparator.text(configure.selectedSlot.type.name, true).visible(true);
					} else {
						innerEquipmentSeparator.text("----", true).visible(true);
					}
					innerEquipmentValue.text(format("equipment.innercount", configure.selectedSlot.count, configure.selectedSlot.slot.max), true).visible(true);
				} else {
					addOne.visible(false);
					removeOne.visible(false);
				}

			} else {
				addButton.visible(false);
				delButton.visible(false);
				addOne.visible(false);
				removeOne.visible(false);
			}
			
			splitButton.visible(true);
			
		}
	}
	/** 
	 * Clear the cells. 
	 * @param cells the list cells
	 */
	void clearCells(List<VehicleCell> cells) {
		for (VehicleCell vc : cells) {
			vc.type = null;
//			vc.selected = false;
			vc.count = 0;
		}
	}
	/** 
	 * Initialize the cells according to the available fighter and vehicles.
	 * @param p the planet to use for the inventory count
	 * @param f the fleet to use for the inventory count 
	 * @param fighters the fighter cells to use
	 * @param tanks the tank cells
	 */
	void prepareCells(Planet p, Fleet f, 
			List<VehicleCell> fighters, List<VehicleCell> tanks) {
		clearCells(fighters);
		clearCells(tanks);
		
		for (ResearchType rt : world().researches.values()) {
			VehicleCell vc = null;
			if (rt.category == ResearchSubCategory.SPACESHIPS_FIGHTERS) {
				vc = fighters.get(rt.index);
			} else
			if (rt.category == ResearchSubCategory.WEAPONS_TANKS) {
				vc = tanks.get(rt.index);
			} else
			if (rt.category == ResearchSubCategory.WEAPONS_VEHICLES) {
				vc = tanks.get(rt.index + 4);
			}
			
			if (vc != null && player().isAvailable(rt)) {
				vc.type = rt;
				if (p != null) {
					vc.count = p.inventoryCount(rt, player());
				} else
				if (f != null) {
					vc.count = f.inventoryCount(rt);
				}
			}
		}
	}
	/** Go to previous planet/fleet. */
	void doPrev() {
		if (player().selectionMode == SelectionMode.PLANET) {
			List<Planet> planets = player().ownPlanets();
			Collections.sort(planets, Planet.NAME_ORDER);
			int idx = planets.indexOf(planet());
			if (idx > 0) {
				player().currentPlanet = planets.get(idx - 1);
			}
			updateInventory(planet(), null, leftList);
		} else {
			List<Fleet> fleets = player().ownFleets();
			int idx = fleets.indexOf(fleet());
			if (idx > 0) {
				player().currentFleet = fleets.get(idx - 1);
			}
			updateInventory(null, fleet(), leftList);
		}
	}
	/** Go to next planet/fleet. */
	void doNext() {
		if (player().selectionMode == SelectionMode.PLANET) {
			List<Planet> planets = player().ownPlanets();
			Collections.sort(planets, Planet.NAME_ORDER);
			int idx = planets.indexOf(planet());
			if (idx < planets.size() - 1 && planets.size() > 0) {
				player().currentPlanet = planets.get(idx + 1);
			}
			updateInventory(planet(), null, leftList);
		} else {
			List<Fleet> fleets = player().ownFleets();
			int idx = fleets.indexOf(fleet());
			if (idx < fleets.size() - 1 && fleets.size() > 0) {
				player().currentFleet = fleets.get(idx + 1);
			}
			updateInventory(null, fleet(), leftList);
		}
	}
	/** 
	 * Update inventory listing. 
	 * @param p the planet to use
	 * @param f the fleet to use
	 * @param list the target listing
	 */
	void updateInventory(Planet p, Fleet f, VehicleList list) {
		list.clear();
		
		if (p != null) {
			list.group = false;
			for (InventoryItem pii : p.inventory) {
				if (pii.type.category == ResearchSubCategory.SPACESHIPS_STATIONS) {
					list.items.add(pii);
				}
			}
		} else {		
			list.group = true;
			for (InventoryItem pii : f.inventory) {
				if (pii.type.category == ResearchSubCategory.SPACESHIPS_BATTLESHIPS
						|| pii.type.category == ResearchSubCategory.SPACESHIPS_CRUISERS
				) {
					list.items.add(pii);
				}
			}
		}

		list.compute();
		
	}
	/**
	 * Select the given vehicle.
	 * @param rt the research type
	 */
	void doSelectVehicle(ResearchType rt) {
		for (VehicleCell vc : leftFighterCells) {
			vc.selected = vc.type == rt;
		}
		for (VehicleCell vc : rightFighterCells) {
			vc.selected = vc.type == rt;
		}
		for (VehicleCell vc : leftTankCells) {
			vc.selected = vc.type == rt;
		}
		for (VehicleCell vc : rightTankCells) {
			vc.selected = vc.type == rt;
		}
	}
	/**
	 * Display the elements of the given sub-category in the slots.
	 * @param cat the target category
	 */
	void displayCategory(ResearchSubCategory cat) {
		for (TechnologySlot ts : slots) {
			ts.type = null;
			ts.visible(false);
		}
		for (ResearchType rt : world().researches.values()) {
			if (world().canDisplayResearch(rt) && rt.category == cat) {
				updateSlot(rt);
			}
		}
		doSelectCategoryButtons(cat);
	}
	/**
	 * Select the category buttons based on the category.
	 * @param cat the category
	 */
	void doSelectCategoryButtons(ResearchSubCategory cat) {
		battleships.down = cat == ResearchSubCategory.SPACESHIPS_BATTLESHIPS;
		cruisers.down = cat == ResearchSubCategory.SPACESHIPS_CRUISERS;
		fighters.down = cat == ResearchSubCategory.SPACESHIPS_FIGHTERS;
		tanks.down = cat == ResearchSubCategory.WEAPONS_TANKS;
		vehicles.down = cat == ResearchSubCategory.WEAPONS_VEHICLES;
		stations.down = cat == ResearchSubCategory.SPACESHIPS_STATIONS;
	}
	/** Add a ship or vehicle to the planet or fleet. */
	void doAddItem() {
		addRemoveItem(1);
	}
	/** Remove a fighter or vehicle from the planet or fleet. */
	void doRemoveItem() {
		addRemoveItem(-1);
	}
	/**
	 * Add or remove a given amount of the currently selected research.
	 * @param delta the delta
	 */
	void addRemoveItem(int delta) {
		if (player().selectionMode == SelectionMode.PLANET) {
			if (research().category == ResearchSubCategory.SPACESHIPS_STATIONS
			) {
				if (delta > 0) {
					InventoryItem pii = new InventoryItem();
					pii.owner = player();
					pii.type = research();
					pii.count = 1;
					pii.hp = pii.type.productionCost;
					pii.shield = pii.type.productionCost;
					planet().inventory.add(pii);
					leftList.items.add(pii);
					leftList.compute();
				}
			} else {
				planet().changeInventory(research(), player(), delta);
			}
		} else {
			if (delta > 0) {
				int cnt = fleet().inventoryCount(research());
				List<InventoryItem> iss = fleet().addInventory(research(), delta);
				
				leftList.items.clear();
				leftList.items.addAll(fleet().getSingleItems());
				leftList.compute();
				configure.item = iss.get(0);
				
				leftList.map.get(research()).index = cnt;
			}
		}
		player().changeInventoryCount(research(), -delta);
		doSelectListVehicle(research());
	}
	/** Update the inventory display based on the current selection. */
	void updateCurrentInventory() {
		if (player().selectionMode == SelectionMode.PLANET) {
			updateInventory(planet(), null, leftList);
		} else {
			updateInventory(null, fleet(), leftList);
		}
		doSelectListVehicle(research());
	}
	/** 
	 * Update the list selection.
	 * @param value the new selected type
	 */
	void doSelectListVehicle(ResearchType value) {
		if (leftList.selectedItem != null) {
			if (leftList.selectedItem.type != value) {
				leftList.selectedItem = null;
			}
		} else {
			for (InventoryItem pii : leftList.items) {
				if (pii.type == value) {
					leftList.selectedItem = pii;
					break;
				}
			}
		}
	}
	/** 
	 * Select the items suitable for the given inventory slot. 
	 * @param slot the target inventory slot
	 */
	void onSelectInventorySlot(InventorySlot slot) {
		for (TechnologySlot ts : slots) {
			ts.visible(false);
			ts.type = null;
		}
		for (ResearchType rt : slot.slot.items) {
			updateSlot(rt);
		}
		if (slot.type != null) {
			world().selectResearch(slot.type);
		} else {
			world().selectResearch(slot.slot.items.get(0));
		}
		configure.selectedSlot = slot;
		doSelectCategoryButtons(research().category);
	}
	/**
	 * Select an inventory item from the inventory listing.
	 * @param value select the given inventory item
	 */
	void onSelectInventoryItem(InventoryItem value) {
		if (leftList.selectedItem == null
				|| value.type != leftList.selectedItem.type
		) {
			displayCategory(value.type.category);
			research(value.type);
		}
		leftList.selectedItem = value;
		configure.type = value.type;
		configure.item = value;
		if (configure.selectedSlot != null) {
			configure.selectedSlot = configure.item.getSlot(configure.selectedSlot.slot.id);
		}
		doSelectVehicle(value.type); // deselect the vehicles
	}
	/**
	 * Select the given vehicle slot (which is not in the listing).
	 * @param value the vehicle slot
	 */
	void onSelectVehicleSlot(ResearchType value) {
		world().selectResearch(value);
		displayCategory(value.category);
		leftList.selectedItem = null;
		configure.type = value;
		configure.item = null;
		configure.selectedSlot = null;
		doSelectVehicle(value);
	}
	/**
	 * Select the given research slot.
	 * @param value the value
	 */
	void onSelectResearchSlot(ResearchType value) {
		world().selectResearch(value);
		if (configure.selectedSlot == null) {
			doSelectVehicle(value);
			doSelectListVehicle(value);
			configure.type = value;
			configure.item = leftList.selectedItem;
		}
	}
	/** Add one or replace the current slot contents. */
	void doAddOne() {
		if (configure.selectedSlot.type != research()) {
			configure.selectedSlot.type = research();
			configure.selectedSlot.count = 1;
			configure.selectedSlot.hp = research().productionCost;
		} else {
			configure.selectedSlot.count++;
		}
		player().changeInventoryCount(configure.selectedSlot.type, -1);
	}
	/** Remove one item from the current slot. */
	void doRemoveOne() {
		player().changeInventoryCount(configure.selectedSlot.type, 1);
		configure.selectedSlot.count--;
		if (configure.selectedSlot.count == 0) {
			configure.selectedSlot.type = null;
		}
	}
}