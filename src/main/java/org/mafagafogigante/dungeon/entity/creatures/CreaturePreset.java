package org.mafagafogigante.dungeon.entity.creatures;

import java.io.Serializable;
import java.util.List;

import org.mafagafogigante.dungeon.entity.Luminosity;
import org.mafagafogigante.dungeon.entity.AbstractPreset;
import org.mafagafogigante.dungeon.entity.TagSet;
import org.mafagafogigante.dungeon.entity.creatures.Creature.Tag;
import org.mafagafogigante.dungeon.game.Id;
import org.mafagafogigante.dungeon.io.Version;
import org.mafagafogigante.dungeon.logging.DungeonLogger;

/**
 * CreaturePreset class that stores the information that the CreatureFactory
 * uses to produce creatures.
 */
public final class CreaturePreset extends AbstractPreset implements Serializable {

	private static final long serialVersionUID = Version.MAJOR;
	private TagSet<Tag> tagSet = TagSet.makeEmptyTagSet(Creature.Tag.class);
	private int health;
	private int attack;
	private AttackAlgorithmId attackAlgorithmId;
	private List<Id> items;
	private List<Drop> dropList;
	private Luminosity luminosity = Luminosity.ZERO;
	private Id weaponId;
	private int inventoryItemLimit;
	private double inventoryWeightLimit;

	/**
	 * Ensures that an integer value is greater than or equal to a provided minimum.
	 * If it is not, returns the minimum and logs a warning.
	 *
	 * @param value         the original value
	 * @param minimum       the minimum acceptable value
	 * @param attributeName the name of the attribute this value represents (used
	 *                      for logging)
	 * @return an integer i such that i >= minimum
	 */
	private static int validate(int value, int minimum, String attributeName) {
		if (value >= minimum) {
			return value;
		} else {
			String format = "Attempted to set %d to %s in CreaturePreset. Using %d.";
			DungeonLogger.warning(String.format(format, value, attributeName, minimum));
			return minimum;
		}
	}

	TagSet<Creature.Tag> getTagSet() {
		return tagSet;
	}

	void setTagSet(TagSet<Creature.Tag> tagSet) {
		this.tagSet = tagSet;
	}

	public boolean hasTag(Creature.Tag tag) {
		return tagSet.hasTag(tag);
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		health = validate(health, 1, "health");
		this.health = health;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		attack = validate(attack, 0, "attack");
		this.attack = attack;
	}

	AttackAlgorithmId getAttackAlgorithmId() {
		return attackAlgorithmId;
	}

	void setAttackAlgorithmId(AttackAlgorithmId attackAlgorithmId) {
		this.attackAlgorithmId = attackAlgorithmId;
	}

	public List<Id> getItems() {
		return items;
	}

	public void setItems(List<Id> items) {
		this.items = items;
	}

	List<Drop> getDropList() {
		return dropList;
	}

	void setDropList(List<Drop> dropList) {
		this.dropList = dropList;
	}

	public Luminosity getLuminosity() {
		return luminosity;
	}

	public void setLuminosity(Luminosity luminosity) {
		this.luminosity = luminosity;
	}

	Id getWeaponId() {
		return weaponId;
	}

	void setWeaponId(Id weaponId) {
		this.weaponId = weaponId;
	}

	int getInventoryItemLimit() {
		return inventoryItemLimit;
	}

	void setInventoryItemLimit(int inventoryItemLimit) {
		this.inventoryItemLimit = inventoryItemLimit;
	}

	double getInventoryWeightLimit() {
		return inventoryWeightLimit;
	}

	void setInventoryWeightLimit(double inventoryWeightLimit) {
		this.inventoryWeightLimit = inventoryWeightLimit;
	}

}
