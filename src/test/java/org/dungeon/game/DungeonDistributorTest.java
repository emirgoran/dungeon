/*
 * Copyright (C) 2015 Bernardo Sulzbach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dungeon.game;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DungeonDistributorTest {

  private static final Point origin = new Point(0, 0, 0);
  private static final MinimumBoundingRectangle boundingRectangle = new MinimumBoundingRectangle(1, 1);
  private static final List<Point> list = DungeonDistributor.makeNoEntrancesZonePointList(origin, boundingRectangle);

  @Test
  public void makeNoEntrancesZonePointListShouldNotIncludeTheStartingPoint() throws Exception {
    List<Point> list = DungeonDistributor.makeNoEntrancesZonePointList(origin, new MinimumBoundingRectangle(1, 1));
    for (Point point : list) {
      Assert.assertThat(point, CoreMatchers.not(origin));
    }
  }

  @Test
  public void makeNoEntrancesZonePointListShouldIncludeBorders() throws Exception {
    List<Point> expectedList = new ArrayList<Point>();
    expectedList.add(new Point(-1, 1, 0));
    expectedList.add(new Point(0, 1, 0));
    expectedList.add(new Point(1, 1, 0));
    expectedList.add(new Point(-1, 0, 0));
    expectedList.add(new Point(1, 0, 0));
    expectedList.add(new Point(-1, -1, 0));
    expectedList.add(new Point(0, -1, 0));
    expectedList.add(new Point(1, -1, 0));
    Assert.assertTrue(list.containsAll(expectedList));
    Assert.assertTrue(expectedList.containsAll(list));
  }

}
