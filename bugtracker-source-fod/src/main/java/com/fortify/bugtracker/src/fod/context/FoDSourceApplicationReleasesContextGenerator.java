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
package com.fortify.bugtracker.src.fod.context;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.fortify.bugtracker.common.src.context.AbstractSourceContextGenerator;
import com.fortify.bugtracker.src.fod.cli.ICLIOptionsFoD;
import com.fortify.bugtracker.src.fod.config.FoDSourceReleasesConfiguration;
import com.fortify.bugtracker.src.fod.connection.FoDConnectionFactory;
import com.fortify.bugtracker.src.fod.json.preprocessor.filter.FoDJSONMapFilterListenerLoggerRelease;
import com.fortify.client.fod.api.FoDReleaseAPI;
import com.fortify.client.fod.api.query.builder.FoDOrderByDirection;
import com.fortify.client.fod.api.query.builder.FoDReleasesQueryBuilder;
import com.fortify.processrunner.cli.CLIOptionDefinitions;
import com.fortify.processrunner.cli.ICLIOptionDefinitionProvider;
import com.fortify.processrunner.context.Context;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.filter.IJSONMapFilterListener;
import com.fortify.util.rest.json.preprocessor.filter.JSONMapFilterListenerLogger.LogLevel;
import com.fortify.util.spring.SpringExpressionUtil;

@Component
public class FoDSourceApplicationReleasesContextGenerator extends AbstractSourceContextGenerator<FoDSourceReleasesConfiguration, FoDReleasesQueryBuilder> implements ICLIOptionDefinitionProvider {
	@Override
	protected String getCLIOptionNameForId() {
		return ICLIOptionsFoD.PRP_FOD_RELEASE_ID;
	}
	
	@Override
	protected String getCLIOptionNameForName() {
		return ICLIOptionsFoD.PRP_FOD_RELEASE_NAME;
	}

	@Override
	protected String getCLIOptionNameForNamePatterns() {
		return ICLIOptionsFoD.PRP_FOD_RELEASE_NAME_PATTERNS;
	}

	@Override
	public void addCLIOptionDefinitions(CLIOptionDefinitions cliOptionDefinitions) {
		FoDConnectionFactory.addCLIOptionDefinitions(cliOptionDefinitions);
		cliOptionDefinitions.add(ICLIOptionsFoD.CLI_FOD_RELEASE_ID);
		cliOptionDefinitions.add(ICLIOptionsFoD.CLI_FOD_RELEASE_NAME);
		cliOptionDefinitions.add(ICLIOptionsFoD.CLI_FOD_RELEASE_NAME_PATTERNS);
	}
	
	@Override
	protected FoDReleasesQueryBuilder createBaseQueryBuilder(Context context) {
		return FoDConnectionFactory.getConnection(context)
				.api(FoDReleaseAPI.class).queryReleases()
				.onDemandApplication()
				.paramOrderBy("applicationName", FoDOrderByDirection.ASC);
	}
	
	@Override
	protected void updateQueryBuilderWithId(Context initialContext, FoDReleasesQueryBuilder queryBuilder) {
		queryBuilder.releaseId(ICLIOptionsFoD.CLI_FOD_RELEASE_ID.getValue(initialContext));
	}
	
	@Override
	protected void updateQueryBuilderWithName(Context initialContext, FoDReleasesQueryBuilder queryBuilder) {
		queryBuilder.applicationAndOrReleaseName(ICLIOptionsFoD.CLI_FOD_RELEASE_NAME.getValue(initialContext));
	}
	
	@Override
	protected void updateContextForSourceObject(Context context, JSONMap sourceObject) {
		IContextFoD fodCtx = context.as(IContextFoD.class);
		context.put(ICLIOptionsFoD.PRP_FOD_RELEASE_ID, sourceObject.get("releaseId", String.class));
		fodCtx.setRelease(sourceObject);
		fodCtx.setFoDApplicationAndReleaseName(getSourceObjectName(sourceObject));
	}

	@Override
	protected FoDSourceReleasesConfiguration getDefaultConfig() {
		return new FoDSourceReleasesConfiguration();
	}
	
	@Override
	protected IJSONMapFilterListener getFilterListenerForContextNamePatterns(Context initialContext) {
		return new FoDJSONMapFilterListenerLoggerRelease(LogLevel.INFO,
				null,
				"${textObjectDoesOrDoesnt} match application release names specified on command line");
	}

	@Override
	protected IJSONMapFilterListener getFilterListenerForConfiguredFilterExpression(Context initialContext) {
		return new FoDJSONMapFilterListenerLoggerRelease(LogLevel.INFO,
				null,
				"${textObjectDoesOrDoesnt} match configured filter expression");
	}

	@Override
	protected IJSONMapFilterListener getFilterListenerForConfiguredNamePatterns(Context initialContext) {
		return new FoDJSONMapFilterListenerLoggerRelease(LogLevel.INFO,
				null,
				"${textObjectDoesOrDoesnt} match any configured application version name");
	}

	@Override
	protected IJSONMapFilterListener getFilterListenerForConfiguredAttributes(Context initialContext) {
		return new FoDJSONMapFilterListenerLoggerRelease(LogLevel.INFO,
				null,
				"${textObjectDoesOrDoesnt} have values for all attributes ${filter.requiredAttributeNames.toString()}");
	}

	@Override
	protected String getSourceObjectAttributeValue(JSONMap sourceObject, String attributeName) {
		JSONMap attributesMap = SpringExpressionUtil.evaluateExpression(sourceObject, "application.attributesMap", JSONMap.class);
		JSONList attributeValues = attributesMap==null?null:attributesMap.get(attributeName, JSONList.class);
		String attributeValue = CollectionUtils.isEmpty(attributeValues)?null:(String)attributeValues.get(0);
		return attributeValue;
	}

	@Override
	protected String getSourceObjectName(JSONMap sourceObject) {
		return SpringExpressionUtil.evaluateTemplateExpression(sourceObject, "${applicationName}:${releaseName}", String.class);
	}

}
