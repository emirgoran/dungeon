package org.mafagafogigante.dungeon.entity.creatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mafagafogigante.dungeon.date.Date;
import org.mafagafogigante.dungeon.entity.Entity;
import org.mafagafogigante.dungeon.entity.Luminosity;
import org.mafagafogigante.dungeon.entity.Weight;
import org.mafagafogigante.dungeon.entity.items.Item;
import org.mafagafogigante.dungeon.entity.items.Item.Tag;
import org.mafagafogigante.dungeon.game.Id;
import org.mafagafogigante.dungeon.game.Location;
import org.mafagafogigante.dungeon.game.NameFactory;
import org.mafagafogigante.dungeon.game.Point;
import org.mafagafogigante.dungeon.game.World;
import org.mafagafogigante.dungeon.util.Percentage;

public class CreatureTest {
	
	public static final int MAX_HERO_HEALTH = 100;
	
	private Creature creature;
	private World world = mock(World.class);
	private Location location = mock(Location.class);
	
	@Before
	public void beforeTest() {
		final CreaturePreset creaturePreset = new CreaturePreset();
		creaturePreset.setId(new Id("CREATURE"));
		creaturePreset.setType("CREATURE");
		creaturePreset.setName(NameFactory.newInstance("CREATURE"));
		creaturePreset.setHealth(10);
		creaturePreset.setVisibility(Percentage.fromString("100"));
		creaturePreset.setItems(new ArrayList<Id>());
		creaturePreset.setHealth(MAX_HERO_HEALTH);
		creaturePreset.setInventoryItemLimit(1);
		creaturePreset.setInventoryWeightLimit(1);
		creaturePreset.setAttack(1);
		creaturePreset.setLuminosity(new Luminosity(Percentage.fromString("25%")));
		
		creature = new Creature(creaturePreset);
		creature.setLocation(location);
		
		
		when(world.getWorldDate()).thenReturn(new Date(2055, 6, 2, 6, 10, 0));
		when(location.getWorld()).thenReturn(world);
	}
	
	@Test
	public void testAddCondition() {
		EffectFactory effectFactory = EffectFactory.getDefaultFactory();
		effectFactory.getEffect(new Id("HEALING"), Arrays.asList(new String[] {"10"}));
		Condition condition = mock(Condition.class);
		Effect effect = mock(Effect.class);
		when(condition.getEffect()).thenReturn(effect);
		when(effect.getMaximumStack()).thenReturn(1);
		creature.addCondition(condition);
		assertEquals(creature.getConditions().size(), 1);
		creature.addCondition(condition);
		assertEquals(creature.getConditions().size(), 1);
		when(condition.hasExpired(new Date(2055, 6, 2, 6, 10, 0))).thenReturn(true);
		creature.addCondition(condition);
		assertEquals(creature.getConditions().size(), 0);
	}
	
	@Test
	public void testGetAttack() {
		EffectFactory effectFactory = EffectFactory.getDefaultFactory();
		effectFactory.getEffect(new Id("HEALING"), Arrays.asList(new String[] {"10"}));
		Condition condition = mock(Condition.class);
		Effect effect = mock(Effect.class);
		when(condition.getEffect()).thenReturn(effect);
		when(condition.modifyAttack(1)).thenReturn(2);
		when(effect.getMaximumStack()).thenReturn(1);
		creature.addCondition(condition);
		int attack = creature.getAttack();
		assertEquals(2, attack);
	}

	@Test
	public void testGetFishingProficiency() {
		EffectFactory effectFactory = EffectFactory.getDefaultFactory();
		effectFactory.getEffect(new Id("HEALING"), Arrays.asList(new String[] {"10"}));
		Condition condition = mock(Condition.class);
		Effect effect = mock(Effect.class);
		when(condition.getEffect()).thenReturn(effect);
		when(condition.modifyFishingProficiency(Percentage.fromString("25%"))).thenReturn(Percentage.fromString("26%"));
		when(effect.getMaximumStack()).thenReturn(1);
		creature.addCondition(condition);
		Percentage fishing = creature.getFishingProficiency();
		assertEquals(0, fishing.compareTo(Percentage.fromString("26%")));
	}
	
	@Test
	public void testSetWeapon() {
		Item item = mock(Item.class);
		when(item.hasTag(Tag.WEAPON)).thenReturn(true);
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		creature.getInventory().addItem(item);
		creature.setWeapon(item);
		assertTrue(creature.hasWeapon());
	}
	
	@Test
	public void testGetLuminosity() {
		Luminosity lum = creature.getLuminosity();
		assertEquals(Percentage.fromString("25%"), lum.toPercentage());
		
		Item item = mock(Item.class);
		when(item.hasTag(Tag.WEAPON)).thenReturn(true);
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		when(item.getLuminosity()).thenReturn(new Luminosity(Percentage.fromString("20%")));
		creature.getInventory().addItem(item);
		creature.setWeapon(item);
		lum = creature.getLuminosity();
		assertEquals(Percentage.fromString("45%"), lum.toPercentage());
	}
	
	@Test
	public void testDropItem() {
		Item item = mock(Item.class);
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		creature.getInventory().addItem(item);
		creature.dropItem(item);
		assertEquals(0, creature.getInventory().getItemCount());
	}
	
	@Test
	public void testFilterByVisibility() {
		List<Entity> res = creature.filterByVisibility(Arrays.asList(new Entity[] {creature}));
		assertEquals(1, res.size());
	}
	
	@Test
	public void testCanSeeTheSky() {
		when(location.getPoint()).thenReturn(new Point(0, 0, 0));
		assertTrue(creature.canSeeTheSky());
	}
}
