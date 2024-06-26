/*
 *
 * Copyright (c) 2008-2009 Brent Easton
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
package VASSAL.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import VASSAL.tools.WarningDialog;
import bsh.BeanShellExpressionValidator;
import bsh.EvalError;
import bsh.NameSpace;

/**
 *
 * Class encapsulating BeanShell support in Vassal
 *
 */
public class BeanShell {

  public static final String TRUE = "true"; // NON-NLS
  public static final String FALSE = "false"; // NON-NLS

  private static final BeanShell instance = new BeanShell();

  public static BeanShell getInstance() {
    return instance;
  }

  protected static final String INIT_SCRIPT = "/VASSAL/script/init_script.bsh"; // NON-NLS

  /*
   * An interpreter for adding script methods to the global NameSpace
   */
  protected ScriptInterpreter globalInterpreter;

  public BeanShell() {
    globalInterpreter = new ScriptInterpreter(this.getClass().getClassLoader());
    init();
  }

  public void init() {
    // Read in the Vassal Script init script
    final URL ini = instance.getClass().getResource(INIT_SCRIPT);
    try (BufferedReader in = new BufferedReader(
      new InputStreamReader(ini.openStream(), StandardCharsets.UTF_8))) {

      final CompileResult result = compile(in);
      if (!result.isSuccess()) {
        result.printStackTrace();
      }
    }
    catch (IOException e) {
      //FIXME: Error message
      WarningDialog.show(e, "");
    }
  }

  public CompileResult compile(Reader in) {
    try {
      globalInterpreter.eval(in);
    }
    catch (EvalError e) {
      return new CompileResult(e);
    }
    return new CompileResult();
  }

  /**
   * Return the Vassal shared NameSpace
   *
   * @return Global NameSpace
   */
  public NameSpace getGlobalNameSpace() {
    return globalInterpreter.getNameSpace();
  }

  /**
   * Execute a Script named in a component DoAction or trait DoAction.
   * Action Scripts take no parameters and return no value.
   * @param scriptName Script name
   */
  public void executeActionScript(String scriptName) {
    try {
      globalInterpreter.evaluate(scriptName + "();");
    }
    catch (EvalError e) {
      e.printStackTrace();
    }
  }

  /**
   * Parse and validate a single expression or script. No evaluation or checking
   * for undefined variables
   *
   * @param expression Expression to validate
   */
  public static boolean validateExpression(String expression) {
    return new BeanShellExpressionValidator(expression).isValid();
  }

  /**
   * Convert a String value into a wrapped primitive object if possible.
   *
   * @param value Value to Wrap
   * @return wrapped value
   */
  public static Object wrap(String value) {
    if (value == null) {
      return "";
    }
    else if (TRUE.equals(value)) {
      return Boolean.TRUE;
    }
    else if (FALSE.equals(value)) {
      return Boolean.FALSE;
    }
    else {
      try {
        return Integer.valueOf(value);
      }
      catch (NumberFormatException e) {
        return value;
      }
    }
  }
}
