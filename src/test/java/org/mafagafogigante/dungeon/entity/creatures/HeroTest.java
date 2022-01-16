package org.mafagafogigante.dungeon.entity.creatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mafagafogigante.dungeon.date.Date;
import org.mafagafogigante.dungeon.entity.Enchantment;
import org.mafagafogigante.dungeon.entity.TagSet;
import org.mafagafogigante.dungeon.entity.Weight;
import org.mafagafogigante.dungeon.entity.items.BookComponent;
import org.mafagafogigante.dungeon.entity.items.Item;
import org.mafagafogigante.dungeon.entity.items.Item.Tag;
import org.mafagafogigante.dungeon.entity.items.ItemFactory;
import org.mafagafogigante.dungeon.entity.items.Rarity;
import org.mafagafogigante.dungeon.entity.items.WeaponComponent;
import org.mafagafogigante.dungeon.game.BaseName;
import org.mafagafogigante.dungeon.game.DungeonString;
import org.mafagafogigante.dungeon.game.Engine;
import org.mafagafogigante.dungeon.game.Game;
import org.mafagafogigante.dungeon.game.GameState;
import org.mafagafogigante.dungeon.game.Id;
import org.mafagafogigante.dungeon.game.Location;
import org.mafagafogigante.dungeon.game.NameFactory;
import org.mafagafogigante.dungeon.game.PartOfDay;
import org.mafagafogigante.dungeon.game.Point;
import org.mafagafogigante.dungeon.game.World;
import org.mafagafogigante.dungeon.io.Sleeper;
import org.mafagafogigante.dungeon.io.Writer;
import org.mafagafogigante.dungeon.spells.Spell;
import org.mafagafogigante.dungeon.stats.HeroStatistics;
import org.mafagafogigante.dungeon.stats.Statistics;
import org.mafagafogigante.dungeon.util.Percentage;
import org.mafagafogigante.dungeon.world.IVisibilityCriteria;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

public class HeroTest {

	public static final int MAX_HERO_HEALTH = 100;
	private static MockedStatic<Engine> engine;
	private static MockedStatic<Writer> writer;
	private static MockedStatic<Sleeper> sleeper;
	private static MockedStatic<Game> game;
	
	private Hero hero;
	private Location location = mock(Location.class);
	private World world = mock(World.class);

	
	@Test
	public void testRestWhileRested() {
		hero.rest();
		writer.verify(times(1), () -> Writer.write("You are already rested."));
	}
	
	@Test
	public void testRestNotRested() {
		hero.getHealth().decrementBy(80);
		hero.rest();
		assertEquals(60, hero.getHealth().getCurrent());
	}
	
	@Test
	public void testSleepDay() {
		hero.sleep();
		writer.verify(times(1), () -> Writer.write("You can only sleep at night."));
	}
	
	@Test
	public void testSleepNight() {
		sleeper.when(() -> Sleeper.sleep(Mockito.anyLong())).thenAnswer((Answer<Void>) invocation -> null);
		Whitebox.setInternalState(Hero.class, "DREAM_DURATION_IN_SECONDS", Integer.MAX_VALUE);
		Whitebox.setInternalState(Hero.class, "MILLISECONDS_TO_SLEEP_AN_HOUR", 1);
		hero.getHealth().decrementBy(80);
		when(world.getPartOfDay()).thenReturn(PartOfDay.EVENING);
		hero.sleep();
		assertNotEquals(20, hero.getHealth().getCurrent());
	}
	
	@Test
	public void testWriteConditions() {
		hero.writeConditions();
		EffectFactory effectFactory = EffectFactory.getDefaultFactory();
		effectFactory.getEffect(new Id("HEALING"), Arrays.asList(new String[] {"10"}));
		Condition condition = mock(Condition.class);
		Effect effect = mock(Effect.class);
		when(condition.getEffect()).thenReturn(effect);
		when(effect.getMaximumStack()).thenReturn(1);
		when(condition.getExpirationDate()).thenReturn(new Date(2055, 6, 2, 6, 10, 0));
		hero.addCondition(condition);
		hero.writeConditions();
		writer.verify(Mockito.atLeast(1), () -> Writer.write(Mockito.any(DungeonString.class)));
	}
	
	@Test
	public void testWriteSpellList() {
		hero.writeSpellList();
		hero.getSpellcaster().learnSpell(new Spell("PERCEIVE", "Perceive") {
		      private static final int SECONDS_TO_CAST_PERCEIVE = 15;

		      @Override
		      public void operate(Hero hero, String[] targetMatcher) {
		        Engine.rollDateAndRefresh(SECONDS_TO_CAST_PERCEIVE);
		        List<Creature> creatureList = new ArrayList<>(hero.getLocation().getCreatures());
		        creatureList.remove(hero);
		        DungeonString string = new DungeonString();
		        string.append("You concentrate and allow your spells to show you what your eyes may have missed...\n");
		        hero.getObserver().writeCreatureSight(creatureList, string);
		        hero.getObserver().writeItemSight(hero.getLocation().getItemList(), string);
		        Writer.write(string);
		      }
		    });
		hero.writeSpellList();
		writer.verify(Mockito.atLeast(1), () -> Writer.write(Mockito.any(DungeonString.class)));
	}
	
	@Test
	public void testReadTime() {
		when(location.getPoint()).thenReturn(new Point(0,0,0));
		when(world.getPartOfDay()).thenReturn(PartOfDay.AFTERNOON);
		hero.readTime();
	}
	
	@Test
	public void testPrintAllStatus() {
		GameState gameState = mock(GameState.class);
		game.when(() -> Game.getGameState()).thenReturn(gameState);
		when(gameState.getWorld()).thenReturn(world);
		Item item = mock(Item.class);
		when(item.hasTag(Tag.WEAPON)).thenReturn(true);
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		WeaponComponent wc = mock(WeaponComponent.class);
		when(item.getWeaponComponent()).thenReturn(wc);
		when(wc.getDamage()).thenReturn(10);
		hero.getInventory().addItem(item);
		hero.setWeapon(item);
		hero.printAllStatus();
		writer.verify(Mockito.atLeast(1), () -> Writer.write(Mockito.any(DungeonString.class)));
	}
	
	@Test
	public void testUnequipWeapon() {
		Item item = mock(Item.class);
		when(item.hasTag(Tag.WEAPON)).thenReturn(true);
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		hero.getInventory().addItem(item);
		hero.setWeapon(item);
		hero.unequipWeapon();
		assertEquals(null, hero.getWeapon());
	}
	
	@Test
	public void testReadItem() {
		Item item = mock(Item.class);
		when(item.hasTag(Tag.BOOK)).thenReturn(true);
		when(item.getName()).thenReturn(new BaseName("item", "items"));
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		BookComponent bc = mock(BookComponent.class); 
		when(item.getBookComponent()).thenReturn(bc);
		when(bc.isDidactic()).thenReturn(false);
		hero.getInventory().addItem(item);
		hero.readItem(new String[] {"item"});
	}
	
	@Test
	public void testExamineItem() {
		Item item = mock(Item.class);
		when(item.hasTag(Tag.WEAPON)).thenReturn(true);
		when(item.getName()).thenReturn(new BaseName("item", "items"));
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		when(item.getRarity()).thenReturn(Rarity.LEGENDARY);
		WeaponComponent wc = mock(WeaponComponent.class); 
		when(item.getWeaponComponent()).thenReturn(wc);
		when(wc.getDamage()).thenReturn(50);
		Enchantment enchantment = mock(Enchantment.class);
		when(wc.getEnchantments()).thenReturn(Arrays.asList(new Enchantment[] {enchantment}));
		when(enchantment.getName()).thenReturn("Ench1");
		when(enchantment.getDescription()).thenReturn("Desc1");
		hero.getInventory().addItem(item);
		hero.examineItem(new String[] {"item"});
	}
	
	@Test
	public void testParseMilk() {
		hero.getHealth().decrementBy(20);
		Creature creature = mock(Creature.class);
		IVisibilityCriteria vc = mock(IVisibilityCriteria.class);
		when(creature.getName()).thenReturn(new BaseName("Cow", "Cows"));
		when(creature.getVisibilityCriteria()).thenReturn(vc);
		when(vc.isMetBy(Mockito.any())).thenReturn(true);
		when(creature.hasTag(org.mafagafogigante.dungeon.entity.creatures.Creature.Tag.MILKABLE)).thenReturn(true);
		
		when(location.getCreatures()).thenReturn(Arrays.asList(new Creature[] {creature}));
		hero.parseMilk(new String[] {"Cow"});
		assertEquals(92, hero.getHealth().getCurrent());
		
		hero.parseMilk(new String[] {});
		assertEquals(100, hero.getHealth().getCurrent());
	}
	
	@Test
	public void testFish() {
		Whitebox.setInternalState(Hero.class, "BASE_FISHING_PROFICIENCY", Percentage.fromString("100%"));
		TagSet ts = mock(TagSet.class);
		ItemFactory itemF = mock(ItemFactory.class);
		when(location.getTagSet()).thenReturn(ts);
		when(ts.hasTag(Location.Tag.FISHABLE)).thenReturn(true);
		when(world.getItemFactory()).thenReturn(itemF);
		Item item = mock(Item.class);
		when(item.getWeight()).thenReturn(Weight.ZERO);
		when(itemF.makeItem(new Id("FISH"), new Date(2055, 6, 2, 6, 10, 0))).thenReturn(item);
		hero.fish();
		assertEquals(1, hero.getInventory().getItemCount());
	}
	
	@Test
	public void testWriteInventory() {
		Item item = mock(Item.class);
		when(item.hasTag(Tag.WEAPON)).thenReturn(true);
		when(item.getName()).thenReturn(new BaseName("item", "items"));
		when(item.getWeight()).thenReturn(Weight.newInstance(1.0));
		when(item.getRarity()).thenReturn(Rarity.LEGENDARY);
		hero.getInventory().addItem(item);
		hero.writeInventory();
	}
	
	@BeforeClass
	public static void prepareMocks() {
		engine = Mockito.mockStatic(Engine.class);
		writer = Mockito.mockStatic(Writer.class);
		sleeper = Mockito.mockStatic(Sleeper.class);
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
		hero.setLocation(location);
		when(world.getWorldDate()).thenReturn(new Date(2055, 6, 2, 6, 10, 0));
		when(location.getWorld()).thenReturn(world);
	}

	@AfterClass
	public static void cleanup() {
		engine.close();
		writer.close();
		sleeper.close();
		game.close();
	}

}
