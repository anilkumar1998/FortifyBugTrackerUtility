/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC, a Micro Focus company
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.processrunner.cli;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class holds all configured {@link CLIOptionDefinition} instances
 * indexed by {@link CLIOptionDefinition} name.
 * 
 * @author Ruud Senden
 *
 */
public class CLIOptionDefinitions extends LinkedHashMap<String, CLIOptionDefinition> {
	private static final long serialVersionUID = 1L;

	/**
	 * Add the given {@link CLIOptionDefinition} to the {@link Map} of {@link CLIOptionDefinition}s
	 * @param cLIOptionDefinition
	 * @return
	 */
	public CLIOptionDefinition add(CLIOptionDefinition cLIOptionDefinition) {
		put(cLIOptionDefinition.getName(), cLIOptionDefinition);
		return cLIOptionDefinition;
	}

}