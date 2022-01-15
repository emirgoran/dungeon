package org.mafagafogigante.dungeon.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.mafagafogigante.dungeon.entity.creatures.Hero;
import org.mafagafogigante.dungeon.entity.creatures.HeroUtils;
import org.mafagafogigante.dungeon.entity.items.CreatureInventory.SimulationResult;
import org.mafagafogigante.dungeon.entity.items.Item;
import org.mafagafogigante.dungeon.io.Writer;

public class HeroInventoryHandler implements Serializable {

	private static final long serialVersionUID = 7196532579873404778L;

	private static HeroInventoryHandler instance;
	private Hero hero;

	private HeroInventoryHandler() {
	}

	public static HeroInventoryHandler getInstance(Hero hero) {

		instance = new HeroInventoryHandler();
		instance.hero = hero;

		return instance;
	}

	public Hero getHero() {
		return hero;
	}

	/**
	 * Picks up an item and returns if the action was successful
	 */
	public boolean pickUpItem(Item item) {
		final SimulationResult result = getHero().getInventory().simulateItemAddition(item);
		// We stop adding items as soon as we hit the first one which would exceed the
		// amount or weight limit.
		if (result == SimulationResult.AMOUNT_LIMIT) {
			Writer.write("Your inventory is full.");
			return false;
		} else if (result == SimulationResult.WEIGHT_LIMIT) {
			Writer.write("You can't carry more weight.");
			return false;
		} else if (result == SimulationResult.SUCCESSFUL) {
			if (getHero().getLocation().getInventory().hasItem(item)) {
				getHero().getLocation().removeItem(item);
				getHero().addItem(item);
				return true;
			} else {
				HeroUtils.writeNoLongerInLocationMessage(item);
				return false;
			}
		}
		return false;
	}

	public void addItemToInventory(Item item) {
		if (getHero().getInventory().simulateItemAddition(item) == SimulationResult.SUCCESSFUL) {
			getHero().getInventory().addItem(item);
			Writer.write(String.format("Added %s to the inventory.", item.getQualifiedName()));
		} else {
			throw new IllegalStateException("simulateItemAddition did not return SUCCESSFUL.");
		}
	}

	/**
	 * Selects multiple items from the inventory.
	 */
	public List<Item> selectItems(String[] arguments, boolean checkForVisibility) {
		if (getHero().getInventory().getItemCount() == 0) {
			Writer.write("Your inventory is empty.");
			return Collections.emptyList();
		}
		List<Item> visibleItems;
		if (checkForVisibility) {
			visibleItems = getHero().filterByVisibility(getHero().getInventory().getItems());
		} else {
			visibleItems = getHero().getInventory().getItems();
		}
		if (arguments.length != 0 || HeroUtils.checkIfAllEntitiesHaveTheSameName(visibleItems)) {
			return HeroUtils.findItems(visibleItems, arguments);
		} else {
			Writer.write("You must specify an item.");
			return Collections.emptyList();
		}
	}

	/**
	 * Selects a single item from the inventory.
	 */
	public Item selectInventoryItem(String[] arguments) {
		List<Item> selectedItems = selectItems(arguments, false);
		if (selectedItems.size() == 1) {
			return selectedItems.get(0);
		}
		if (selectedItems.size() > 1) {
			Writer.write("The query matched multiple items.");
		}
		return null;
	}

}
