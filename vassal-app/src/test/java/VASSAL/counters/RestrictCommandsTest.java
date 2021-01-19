/*
 * Copyright 2020 Vassal Development Team
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */

package VASSAL.counters;

import VASSAL.configure.PropertyExpression;
import VASSAL.tools.NamedKeyStroke;
import java.lang.reflect.InvocationTargetException;
import org.junit.Test;

public class RestrictCommandsTest extends DecoratorTest {

  @Test
  public void serializeTests() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

    RestrictCommands trait = new RestrictCommands();

    // Default trait
    serializeTest("Default trait", trait); // NON-NLS

    // Set a Command and Named KeyStroke
    trait = new RestrictCommands();
    trait.name = "testCommand"; // NON-NLS
    trait.action = RestrictCommands.HIDE;
    trait.propertyMatch = new PropertyExpression("{x==2}");
    trait.watchKeys = new NamedKeyStroke[] { new NamedKeyStroke("key1"), new NamedKeyStroke("key2") };
    serializeTest("Complex trait", trait); // NON-NLS

  }
}