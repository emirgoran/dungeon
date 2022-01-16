package org.mafagafogigante.dungeon.entity;

import org.mafagafogigante.dungeon.game.Id;
import org.mafagafogigante.dungeon.game.Name;
import org.mafagafogigante.dungeon.util.Percentage;

/**
 * An interface that simplifies Entity instantiation.
 */
public abstract class AbstractPreset {
	private Id id;
	private String type;
	private Name name;
	private Weight weight;
	private Percentage visibility;

	public Id getId() {
		return id;
	}

	public void setId(Id id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public Weight getWeight() {
		return weight;
	}

	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	public Percentage getVisibility() {
		return visibility;
	}

	public void setVisibility(Percentage visibility) {
		this.visibility = visibility;
	}

}
