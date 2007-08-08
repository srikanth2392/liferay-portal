/**
 * Copyright (c) 2000-2007 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.action;

import com.liferay.portal.struts.JSONAction;
import com.liferay.util.CollectionFactory;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * <a href="JSONServiceAction.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class JSONServiceAction extends JSONAction {

	public String getJSON(
			ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res)
		throws Exception {

		String className = ParamUtil.getString(req, "serviceClassName");
		String methodName = ParamUtil.getString(req, "serviceMethodName");
		String[] parameters = StringUtil.split(
			ParamUtil.getString(req, "serviceParameters"));

		Class classObj = Class.forName(className);

		Object[] methodAndParameterTypes = getMethodAndParameterTypes(
			classObj, methodName, parameters.length);

		if (methodAndParameterTypes != null) {
			Method method = (Method)methodAndParameterTypes[0];
			Class[] parameterTypes = (Class[])methodAndParameterTypes[1];
			Object[] args = new Object[parameters.length];

			for (int i = 0; i < parameters.length; i++) {
				args[i] = getArgValue(
					req, classObj, methodName, parameters[i],
					parameterTypes[i]);

				if (args[i] == null) {
					return null;
				}
			}

			try {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Invoking class " + classObj + " on method " +
							method.getName() + " with args " + args);
				}

				Object returnObj = method.invoke(classObj, args);

				if (returnObj != null) {
					if (returnObj instanceof JSONArray) {
						JSONArray jsonArray = (JSONArray)returnObj;

						return jsonArray.toString();
					}
					else if (returnObj instanceof JSONObject) {
						JSONObject jsonObj = (JSONObject)returnObj;

						return jsonObj.toString();
					}
					else if (returnObj instanceof Boolean ||
							 returnObj instanceof Integer ||
							 returnObj instanceof Long ||
							 returnObj instanceof Double ||
							 returnObj instanceof String) {

						JSONObject jsonObj = new JSONObject();

						jsonObj.put("returnValue", returnObj.toString());

						return jsonObj.toString();
					}
					else {
						_log.error(
							"Unsupported return type for class " + classObj +
								" and method " + methodName);

						return null;
					}
				}
				else {
					JSONObject jsonObj = new JSONObject();

					return jsonObj.toString();
				}
			}
			catch (InvocationTargetException ite) {
				JSONObject jsonObj = new JSONObject();

				jsonObj.put("exception", ite.getCause());

				return jsonObj.toString();
			}
		}

		return null;
	}

	protected Object getArgValue(
			HttpServletRequest req, Class classObj, String methodName,
			String parameter, Class parameterType)
		throws Exception {

		String parameterTypeName = parameterType.getName();

		if (parameterTypeName.equals("boolean") ||
			parameterTypeName.equals(Boolean.class.getName())) {

			return new Boolean(ParamUtil.getBoolean(req, parameter));
		}
		else if (parameterTypeName.equals("double") ||
				 parameterTypeName.equals(Double.class.getName())) {

			return new Double(ParamUtil.getDouble(req, parameter));
		}
		else if (parameterTypeName.equals("int") ||
				 parameterTypeName.equals(Integer.class.getName())) {

			return new Integer(ParamUtil.getInteger(req, parameter));
		}
		else if (parameterTypeName.equals("long") ||
				 parameterTypeName.equals(Long.class.getName())) {

			return new Long(ParamUtil.getLong(req, parameter));
		}
		else if (parameterTypeName.equals("short") ||
				 parameterTypeName.equals(Short.class.getName())) {

			return new Short(ParamUtil.getShort(req, parameter));
		}
		else if (parameterTypeName.equals(String.class.getName())) {
			return ParamUtil.getString(req, parameter);
		}
		else if (parameterTypeName.equals("[Ljava.lang.String;")) {
			return StringUtil.split(ParamUtil.getString(req, parameter));
		}
		else {
			_log.error(
				"Unsupported parameter type for class " + classObj +
					", method " + methodName + ", parameter " + parameter +
						", and type " + parameterTypeName);

			return null;
		}
	}

	protected Object[] getMethodAndParameterTypes(
			Class classObj, String methodName, int paramtersCount)
		throws Exception {

		String key =
			classObj.getName() + "_METHOD_NAME_" + methodName +
				"_PARAMETERS_COUNT_" + paramtersCount;

		Object[] methodAndParameterTypes = (Object[])_methodCache.get(key);

		if (methodAndParameterTypes != null) {
			return methodAndParameterTypes;
		}

		Method method = null;
		Class[] parameterTypes = null;

		Method[] methods = classObj.getMethods();

		for (int i = 0; i < methods.length; i++) {
			Method curMethod = methods[i];

			if (curMethod.getName().equals(methodName)) {
				Class[] curParameterTypes = curMethod.getParameterTypes();

				if (curParameterTypes.length == paramtersCount) {
					if (method != null) {
						_log.error(
							"Obscure method name for class " + classObj +
								", method " + methodName +
									", and parameter count " + paramtersCount);

						return null;
					}
					else {
						method = curMethod;
						parameterTypes = curParameterTypes;
					}
				}
			}
		}

		if (method != null) {
			methodAndParameterTypes = new Object[] {method, parameterTypes};

			_methodCache.put(key, methodAndParameterTypes);

			return methodAndParameterTypes;
		}
		else {
			_log.error(
				"No method found for class " + classObj + ", method " +
					methodName + ", and parameter count " + paramtersCount);

			return null;
		}
	}

	private static Log _log = LogFactory.getLog(JSONServiceAction.class);

	private Map _methodCache = CollectionFactory.getHashMap();

}