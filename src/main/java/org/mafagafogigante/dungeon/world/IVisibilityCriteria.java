package org.mafagafogigante.dungeon.world;

import org.mafagafogigante.dungeon.entity.creatures.Observer;

import java.io.Serializable;

public interface IVisibilityCriteria extends Serializable {
    boolean isMetBy(Observer observer);

    @Override
    String toString();
}
