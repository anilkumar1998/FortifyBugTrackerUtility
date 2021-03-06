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
package com.fortify.processrunner.util.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.fortify.util.spring.SpringExpressionUtil;

/**
 * This class allows for building a {@link Map} based on configured
 * {@link IMapUpdater} instances.
 * 
 * @author Ruud Senden
 *
 */
public class MapBuilder {
	private static final Log LOG = LogFactory.getLog(MapBuilder.class);
	private Collection<IMapUpdater> mapUpdaters = new ArrayList<IMapUpdater>();
	
	public <T extends Map<String, Object>> T build(T map) {
		for ( IMapUpdater mapUpdater : mapUpdaters ) {
			mapUpdater.updateMap(map);
		}
		if ( LOG.isDebugEnabled() ) {
			LOG.trace("[Process] Build object map: "+map);
		}
		return map;
	}
	
	public MapBuilder addMapUpdater(IMapUpdater mapUpdater) {
		mapUpdaters.add(mapUpdater);
		return this;
	}
	
	/**
	 * Interface for updating a given map.
	 *
	 */
	public static interface IMapUpdater {
		void updateMap(Map<String, Object> map);
	}
	
	public static abstract class AbstractMapUpdaterWithRootObject implements IMapUpdater {
		private Object rootObject;
		public Object getRootObject() { return rootObject; }
		public void setRootObject(Object rootObject) { this.rootObject = rootObject; }
		public final void updateMap(Map<String, Object> map) {
			Object rootObject = getRootObject();
			if ( rootObject != null ) {
				updateMap(map, rootObject);
			}
		}
		protected abstract void updateMap(Map<String, Object> map, Object rootObject);
	}
	
	public static abstract class AbstractMapUpdaterWithRootObjectAndExpressionMap extends AbstractMapUpdaterWithRootObject {
		private LinkedHashMap<String,? extends Expression> expressions;
		public LinkedHashMap<String,? extends Expression> getExpressions() { return expressions; }
		public void setExpressions(LinkedHashMap<String,? extends Expression> expressions) { this.expressions = expressions; }
		@Override
		protected final void updateMap(Map<String, Object> map, Object rootObject) {
			LinkedHashMap<String,? extends Expression> expressions = getExpressions();
			if ( expressions != null ) {
				for ( Map.Entry<String, ? extends Expression> entry : expressions.entrySet() ) {
					String key = entry.getKey();
					Expression expression = entry.getValue();
					if ( StringUtils.isNotBlank(key) && expression != null ) {
						Object value = getValue(map, key, rootObject, expression);
						map.put(key, value);
					}
				}
			}
		}
		protected abstract Object getValue(Map<String, Object> map, String key, Object rootObject, Expression expression);
	}
	
	public static class MapUpdaterPutValuesFromExpressionMap extends AbstractMapUpdaterWithRootObjectAndExpressionMap {
		private StandardEvaluationContext evaluationContext;
		public MapUpdaterPutValuesFromExpressionMap() {}
		public MapUpdaterPutValuesFromExpressionMap(StandardEvaluationContext evaluationContext, Object rootObject, LinkedHashMap<String, ? extends Expression> expressions) {
			setRootObject(rootObject);
			setExpressions(expressions);
			this.evaluationContext = evaluationContext;
		}
		@Override
		protected Object getValue(Map<String, Object> map, String key, Object rootObject, Expression expression) {
			return SpringExpressionUtil.evaluateExpression(evaluationContext, rootObject, expression, Object.class);
		}
	}
	
	public static class MapUpdaterAppendValuesFromExpressionMap extends AbstractMapUpdaterWithRootObjectAndExpressionMap {
		private StandardEvaluationContext evaluationContext;
		public MapUpdaterAppendValuesFromExpressionMap() {}
		public MapUpdaterAppendValuesFromExpressionMap(StandardEvaluationContext evaluationContext, Object rootObject, LinkedHashMap<String, ? extends Expression> expressions) {
			setRootObject(rootObject);
			setExpressions(expressions);
			this.evaluationContext = evaluationContext;
		}
		@Override
		protected Object getValue(Map<String, Object> map, String key, Object rootObject, Expression expression) {
			Object value = map.get(key);
			if ( rootObject instanceof Iterable ) {
				value = appendValues(value, (Iterable<?>)rootObject, expression);
			} else {
				value = appendValue(value, rootObject, expression);
			}
			return value;
		}

		protected Object appendValues(Object value, Iterable<?> rootObjects, Expression expression) {
			for ( Object rootObject : rootObjects ) {
				value = appendValue(value, rootObject, expression);
			}
			return value;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		protected Object appendValue(Object value, Object rootObject, Expression expression) {
			Object valueToAppend = SpringExpressionUtil.evaluateExpression(evaluationContext, rootObject, expression, Object.class); 
			if ( value instanceof String ) {
				value = value+""+valueToAppend;
			} else if ( value instanceof Collection ) {
				((Collection)value).add(valueToAppend);
			}
			return value;
		}
	}
	
	
}
