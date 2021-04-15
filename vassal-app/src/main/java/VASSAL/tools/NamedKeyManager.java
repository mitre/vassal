/*
 *
 * Copyright (c) 2008-2009 by Brent Easton
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

package VASSAL.tools;

import java.util.HashMap;
import java.util.Map;

import javax.swing.KeyStroke;

public class NamedKeyManager {
  protected static NamedKeyManager instance = new NamedKeyManager();

  /*
   * Named Keys are allocated real Keystrokes
   * in the sequence from '\uE000' to '\uF8FE'.
   */
  protected static final int NAMED_START = '\uE000';
  protected static final int NAMED_END = '\uF8FE';

  protected static int nextNamedKey = NAMED_START;
  protected static final Map<String, KeyStroke> strokes = new HashMap<>();

  public static NamedKeyManager getInstance() {
    return instance;
  }

  /**
   * Return true if the supplied KeyStroke is in the range allocated
   * to NamedKeyStrokes
   * @param k KeyStroke
   * @return true if this was generated by us
   */
  public static boolean isNamed(KeyStroke k) {
    if (k == null) {
      return false;
    }
    final int code = k.getKeyCode();
    return code >= NAMED_START && code <= NAMED_END;
  }

  /**
   * Return the generated KeyStroke associated with the name
   * @param name NamedKeyStroke name
   * @return generated KeyStroke
   */
  public KeyStroke getKeyStroke(String name, KeyStroke ks) {
    if (name == null || name.isEmpty()) {
      return ks;
    }

    // Look up the name in the cache and allocate the next
    // available KeyStroke if required.
    return strokes.computeIfAbsent(
      name,
      k -> KeyStroke.getKeyStroke(getNextStroke(), 0)
    );
  }

  /**
   * Return the next KeyStroke from the pool
   * @return KeyStroke Id
   */
  public int getNextStroke() {
    if (nextNamedKey == NAMED_END) {
      throw new IllegalStateException("Too many Named Keys");
    }
    return nextNamedKey++;
  }
}
