package org.mafagafogigante.dungeon.world;

import org.mafagafogigante.dungeon.entity.creatures.Observer;
import org.mafagafogigante.dungeon.io.Version;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * A collection of visibility criterion.
 */
class VisibilityCriteria implements IVisibilityCriteria {

  private static final long serialVersionUID = Version.MAJOR;
  private final List<VisibilityCriterion> visibilityCriteria;

  protected VisibilityCriteria(VisibilityCriterion... visibilityCriteria) {
    this.visibilityCriteria = Arrays.asList(visibilityCriteria);
  }

  /**
   * Evaluates whether or not these visibility criteria is met by an observer.
   */
  @Override
  public boolean isMetBy(Observer observer) {
    for (VisibilityCriterion criterion : visibilityCriteria) {
      if (!criterion.isMetBy(observer)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "visibilityCriteria=VisibilityCriteria{" + visibilityCriteria + '}';
  }

}
