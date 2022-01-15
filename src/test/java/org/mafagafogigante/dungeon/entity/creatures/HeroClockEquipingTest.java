package org.mafagafogigante.dungeon.entity.creatures;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mafagafogigante.dungeon.date.Date;
import org.mafagafogigante.dungeon.entity.Weight;
import org.mafagafogigante.dungeon.entity.items.ClockComponent;
import org.mafagafogigante.dungeon.entity.items.Item;
import org.mafagafogigante.dungeon.entity.items.WeaponComponent;
import org.mafagafogigante.dungeon.game.Engine;
import org.mafagafogigante.dungeon.game.Game;
import org.mafagafogigante.dungeon.game.Id;
import org.mafagafogigante.dungeon.game.Location;
import org.mafagafogigante.dungeon.game.NameFactory;
import org.mafagafogigante.dungeon.game.Point;
import org.mafagafogigante.dungeon.game.World;
import org.mafagafogigante.dungeon.stats.HeroStatistics;
import org.mafagafogigante.dungeon.stats.Statistics;
import org.mafagafogigante.dungeon.util.Percentage;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class HeroClockEquipingTest {

	private Hero hero;
	private Item workingClock;
	private Item brokenClock;
	private Location location = mock(Location.class);
	private Point point = mock(Point.class);
	private World world = mock(World.class);
	private static MockedStatic<Game> game;
	private static MockedStatic<Engine> engine;

	@Test
	public void equipWorkingClock() {
		hero.setLocation(location);
		hero.addItem(workingClock);

		Item clock = hero.getBestClock();

		assertTrue(clock.equals(workingClock));
	}

	@Test
	public void equipBrokenClock() {
		hero.setLocation(location);
		hero.addItem(brokenClock);

		Item clock = hero.getBestClock();

		assertTrue(clock.equals(brokenClock));
	}

	@Test
	public void equipClock() {
		hero.setLocation(location);

		Item clock = hero.getBestClock();

		assertNull(clock);
	}

	@Test
	public void equipedWorkingClock() {
		hero.setLocation(location);
		hero.addItem(workingClock);
		hero.parseEquip(new String[] { "workingClock" });
		Item clock = hero.getBestClock();

		assertTrue(clock.equals(workingClock));
	}

	@Test
	public void equipedBrokenClock() {
		hero.setLocation(location);
		hero.addItem(brokenClock);
		hero.parseEquip(new String[] { "brokenClock" });
		Item clock = hero.getBestClock();

		assertTrue(clock.equals(brokenClock));
	}

	@BeforeClass
	public static void prepareMocks() {
		engine = Mockito.mockStatic(Engine.class);
		game = Mockito.mockStatic(Game.class);
	}

	@Before
	public void createTestHero() {
		final CreaturePreset creaturePreset = new CreaturePreset();
		creaturePreset.setId(new Id("HERO"));
		creaturePreset.setType("HERO");
		creaturePreset.setName(NameFactory.newInstance("HERO"));
		creaturePreset.setHealth(10);
		creaturePreset.setVisibility(Percentage.fromString("100"));
		creaturePreset.setItems(new ArrayList<Id>());
		creaturePreset.setHealth(10);
		creaturePreset.setInventoryItemLimit(1);
		creaturePreset.setInventoryWeightLimit(1);

		CreatureFactory creatureFactory = new CreatureFactory(new CreaturePresetFactory() {
			@Override
			public Collection<CreaturePreset> getCreaturePresets() {
				return Collections.singleton(creaturePreset);
			}
		});
		Statistics stats = mock(Statistics.class);
		HeroStatistics heroStats = mock(HeroStatistics.class);
		when(stats.getHeroStatistics()).thenReturn(heroStats);
		hero = creatureFactory.makeHero(new Date(2055, 01, 01), new World(null), stats);
		when(world.getWorldDate()).thenReturn(new Date(2055, 6, 2, 6, 10, 0));
		when(location.getWorld()).thenReturn(world);
		when(location.getPoint()).thenReturn(point);
		when(point.getZ()).thenReturn(-1);

		workingClock = mock(Item.class);
		setupClock(workingClock, false);
		brokenClock = mock(Item.class);
		setupClock(brokenClock, true);
		when(brokenClock.isBroken()).thenReturn(true);
	}

	private void setupClock(Item item, boolean broken) {
		when(item.hasTag(Item.Tag.CLOCK)).thenReturn(true);
		when(item.hasTag(Item.Tag.WEAPON)).thenReturn(true);

		String name = broken ? "brokenClock" : "workingClock";

		when(item.getQualifiedName()).thenReturn(name);
		when(item.getName()).thenReturn(NameFactory.newInstance(name));
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		ClockComponent clockComponent = mock(ClockComponent.class);
		when(item.getClockComponent()).thenReturn(clockComponent);
		WeaponComponent weaponComponent = mock(WeaponComponent.class);
		when(weaponComponent.getDamage()).thenReturn(10);
		when(item.getWeaponComponent()).thenReturn(weaponComponent);
	}

	@AfterClass
	public static void cleanup() {
		engine.close();
		game.close();
	}

}
