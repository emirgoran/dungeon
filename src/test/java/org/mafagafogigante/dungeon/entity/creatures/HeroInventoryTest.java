package org.mafagafogigante.dungeon.entity.creatures;

import static org.junit.Assert.assertEquals;
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
import org.mafagafogigante.dungeon.entity.Integrity;
import org.mafagafogigante.dungeon.entity.Luminosity;
import org.mafagafogigante.dungeon.entity.Weight;
import org.mafagafogigante.dungeon.entity.items.FoodComponent;
import org.mafagafogigante.dungeon.entity.items.Item;
import org.mafagafogigante.dungeon.entity.items.ItemIntegrity;
import org.mafagafogigante.dungeon.entity.items.LocationInventory;
import org.mafagafogigante.dungeon.entity.items.WeaponComponent;
import org.mafagafogigante.dungeon.game.Engine;
import org.mafagafogigante.dungeon.game.Game;
import org.mafagafogigante.dungeon.game.Id;
import org.mafagafogigante.dungeon.game.Location;
import org.mafagafogigante.dungeon.game.NameFactory;
import org.mafagafogigante.dungeon.game.World;
import org.mafagafogigante.dungeon.stats.HeroStatistics;
import org.mafagafogigante.dungeon.stats.Statistics;
import org.mafagafogigante.dungeon.util.Percentage;
import org.mafagafogigante.dungeon.world.LuminosityVisibilityCriterion;
import org.mafagafogigante.dungeon.world.IVisibilityCriteria;
import org.mafagafogigante.dungeon.world.VisibilityCriteriaFactory;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class HeroInventoryTest {

	public static final int MAX_HERO_HEALTH = 100;
	private static MockedStatic<Engine> engine;
	private static MockedStatic<Game> game;

	private Hero hero;
	private Item item;
	private Item item2 = mock(Item.class);
	private Location location = mock(Location.class);
	private World world = mock(World.class);

	@Test
	public void addItem() {
		when(item.getQualifiedName()).thenReturn("TestItem");
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		hero.addItem(item);
		assertEquals(1, hero.getInventory().getItemCount());
	}

	@Test
	public void pickUpItem() {
		LocationInventory locInventory = new LocationInventory();
		when(location.getInventory()).thenReturn(locInventory);
		when(location.getItemList()).thenReturn(locInventory.getItems());
		when(location.getLuminosity()).thenReturn(new Luminosity(new Percentage(100.0)));
		when(item.getVisibilityCriteria()).thenReturn(
				new VisibilityCriteriaFactory().createNewLuminosityVisibilityCriteria(new Luminosity(new Percentage(80.0))));
		when(item.getQualifiedName()).thenReturn("TestItem");
		when(item.getName()).thenReturn(NameFactory.newInstance("TestItem"));
		when(item.getWeight()).thenReturn(Weight.newInstance(0.5));
		location.getInventory().addItem(item);
		hero.setLocation(location);
		hero.pickItems(new String[] { "TestItem" });
		assertEquals(1, hero.getInventory().getItemCount());

		location.getInventory().addItem(item2);
		when(item2.getVisibilityCriteria()).thenReturn(
				new VisibilityCriteriaFactory().createNewLuminosityVisibilityCriteria(new Luminosity(new Percentage(80.0))));
		when(item2.getQualifiedName()).thenReturn("TestItem2");
		when(item2.getName()).thenReturn(NameFactory.newInstance("TestItem2"));
		when(item2.getWeight()).thenReturn(Weight.newInstance(0.2));
		hero.pickItems(new String[] { "TestItem2" });
		assertEquals(1, hero.getInventory().getItemCount());
	}

	@Test
	public void testWeightLimit() {
		LocationInventory locInventory = new LocationInventory();
		when(location.getInventory()).thenReturn(locInventory);
		when(location.getItemList()).thenReturn(locInventory.getItems());
		when(location.getLuminosity()).thenReturn(new Luminosity(new Percentage(100.0)));
		when(item.getVisibilityCriteria()).thenReturn(
				new VisibilityCriteriaFactory().createNewLuminosityVisibilityCriteria(new Luminosity(new Percentage(80.0))));
		when(item.getQualifiedName()).thenReturn("TestItem");
		when(item.getName()).thenReturn(NameFactory.newInstance("TestItem"));
		when(item.getWeight()).thenReturn(Weight.newInstance(2));
		location.getInventory().addItem(item);
		hero.setLocation(location);
		hero.pickItems(new String[] { "TestItem" });
		assertEquals(0, hero.getInventory().getItemCount());
	}

	@Test
	public void equipItem() {
		when(item.hasTag(Item.Tag.WEAPON)).thenReturn(true);
		when(item.getQualifiedName()).thenReturn("TestWeapon");
		when(item.getName()).thenReturn(NameFactory.newInstance("TestWeapon"));
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		WeaponComponent weaponComponent = mock(WeaponComponent.class);
		when(weaponComponent.getDamage()).thenReturn(10);
		when(item.getWeaponComponent()).thenReturn(weaponComponent);

		hero.setLocation(location);
		hero.getInventory().addItem(item);
		hero.parseEquip(new String[] { "TestWeapon" });
		assertTrue(hero.hasWeapon());
	}

	@Test
	public void dropItems() {
		LocationInventory locInventory = new LocationInventory();
		when(location.getInventory()).thenReturn(locInventory);
		when(location.getItemList()).thenReturn(locInventory.getItems());
		when(location.getLuminosity()).thenReturn(new Luminosity(new Percentage(100.0)));

		when(item.getQualifiedName()).thenReturn("TestItem");
		when(item.getName()).thenReturn(NameFactory.newInstance("TestItem"));
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		hero.setLocation(location);
		hero.getInventory().addItem(item);

		hero.dropItems(new String[] { "TestItem" });

		assertEquals(0, hero.getInventory().getItemCount());
	}

	@Test
	public void eatItem() {
		when(item.hasTag(Item.Tag.FOOD)).thenReturn(true);
		when(item.getQualifiedName()).thenReturn("TestFood");
		when(item.getName()).thenReturn(NameFactory.newInstance("TestFood"));
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		FoodComponent foodComponent = mock(FoodComponent.class);
		when(foodComponent.getIntegrityDecrementOnEat()).thenReturn(1);
		when(item.getFoodComponent()).thenReturn(foodComponent);

		ItemIntegrity itemIntegrity = ItemIntegrity.makeItemIntegrity(new Integrity(10, 10), item);
		when(item.getIntegrity()).thenReturn(itemIntegrity);

		hero.setLocation(location);
		hero.getInventory().addItem(item);

		hero.eatItem(new String[] { "TestFood" });

		assertEquals(item.getIntegrity().getMaximum(), item.getIntegrity().getCurrent());
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
		creaturePreset.setHealth(MAX_HERO_HEALTH);
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
		item = mock(Item.class);
	}

	@AfterClass
	public static void cleanup() {
		engine.close();
		game.close();
	}
}
