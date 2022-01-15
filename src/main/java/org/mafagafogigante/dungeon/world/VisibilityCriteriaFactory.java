package org.mafagafogigante.dungeon.world;

import org.mafagafogigante.dungeon.entity.Luminosity;

public final class VisibilityCriteriaFactory {

    public IVisibilityCriteria createNewLuminosityVisibilityCriteria(Luminosity minimumLuminosity) {
        return  new VisibilityCriteria(new LuminosityVisibilityCriterion(minimumLuminosity));
    }

    public IVisibilityCriteria createNewVisbilityCriteriaFromMultipleCriterion(VisibilityCriterion... visibilityCriteria) {
        return  new VisibilityCriteria(visibilityCriteria);
    }
}
