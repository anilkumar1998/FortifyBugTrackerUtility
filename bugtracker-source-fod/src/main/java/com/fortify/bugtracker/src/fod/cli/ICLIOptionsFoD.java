/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC
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
package com.fortify.bugtracker.src.fod.cli;

import com.fortify.processrunner.cli.CLIOptionDefinition;

public interface ICLIOptionsFoD {

	String PRP_FOD_BASE_URL = "FoDBaseUrl";
	String PRP_FOD_TENANT = "FoDTenant";
	String PRP_FOD_CLIENT_ID = "FoDClientId";
	String PRP_FOD_CLIENT_SECRET = "FoDClientSecret";
	String PRP_FOD_USER_NAME = "FoDUserName";
	String PRP_FOD_PASSWORD = "FoDPassword";
	String PRP_FOD_RELEASE_ID = "FoDReleaseId";
	String PRP_FOD_RELEASE_NAME_PATTERNS = "FoDReleaseNamePatterns";

	CLIOptionDefinition CLI_FOD_BASE_URL = new CLIOptionDefinition(ICLIOptionsFoD.PRP_FOD_BASE_URL, "FoD base URL", true).readFromConsole(true);
	CLIOptionDefinition CLI_FOD_TENANT = new CLIOptionDefinition(ICLIOptionsFoD.PRP_FOD_TENANT, "FoD tenant", true).readFromConsole(true);
	CLIOptionDefinition CLI_FOD_CLIENT_ID = new CLIOptionDefinition(ICLIOptionsFoD.PRP_FOD_CLIENT_ID, "FoD client id (leave blank to use user credentials)", true).readFromConsole(true).isAlternativeForOptions(ICLIOptionsFoD.PRP_FOD_USER_NAME);
	CLIOptionDefinition CLI_FOD_CLIENT_SECRET = new CLIOptionDefinition(ICLIOptionsFoD.PRP_FOD_CLIENT_SECRET, "FoD client secret", true).readFromConsole(true).isPassword(true).dependsOnOptions(ICLIOptionsFoD.PRP_FOD_CLIENT_ID);
	CLIOptionDefinition CLI_FOD_USER_NAME = new CLIOptionDefinition(ICLIOptionsFoD.PRP_FOD_USER_NAME, "FoD user name (leave blank to use client credentials)", true).readFromConsole(true).isAlternativeForOptions(ICLIOptionsFoD.PRP_FOD_CLIENT_ID);
	CLIOptionDefinition CLI_FOD_PASSWORD = new CLIOptionDefinition(ICLIOptionsFoD.PRP_FOD_PASSWORD, "FoD password", true).readFromConsole(true).isPassword(true).dependsOnOptions(ICLIOptionsFoD.PRP_FOD_USER_NAME);
	CLIOptionDefinition CLI_FOD_RELEASE_ID = new CLIOptionDefinition(ICLIOptionsFoD.PRP_FOD_RELEASE_ID,"FoD release id from which to retrieve vulnerabilities",true);
	CLIOptionDefinition CLI_FOD_RELEASE_NAME_PATTERNS = new CLIOptionDefinition(ICLIOptionsFoD.PRP_FOD_RELEASE_NAME_PATTERNS, "FoD application release names (<application name pattern>:<release name pattern>), separated by comma's", true).isAlternativeForOptions(ICLIOptionsFoD.PRP_FOD_RELEASE_ID);

}