package com.gdelight.tools.string;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class StringUtils {

	public static final String SUBST_KEY = "substKey";

	/**
	 * Method to substitute variables in the string provided.  For example if a property existed
	 * with key = testVariable then any match on the pattern $$testVariable would be replaced by the keys value.
	 * @param props the values being substituted in the string
	 * @param str the string being checked for substitutions.
	 * @return the string with all found matching patterns substituted.
	 */
	public static String substituteMessageVariables(Properties props, String str) {
		String newStr = str;
		String substKey = props.getProperty(SUBST_KEY);
		Iterator<?> iter = props.keySet().iterator();
		String key = "";
		while (iter.hasNext()) {
			key = (String) iter.next();
			if (!key.equals(SUBST_KEY)) {
				newStr = newStr.replaceAll(substKey+key, props.getProperty(key));
			}
		}
		return newStr;
	}

	/**
	 * Method to substitute variables in the string provided.  For example if a property existed
	 * with key = testVariable then any match on the pattern $$testVariable would be replaced by the keys value.
	 * @param substKey the key to look for to be substituting.  In this method if the key is $$ it will look for $$0, $$1, $$2 etc. 
	 * @param props the properties to be substituted in
	 * @param str the string that contains the substitutions that are to be made.
	 * @return the string with all substitutions made.
	 */
	public static String substituteMessageVariables(String substKey, List<String> props, String str) {
		String newStr = str;
		String prop = "";
		for (int i = 0; i < props.size(); i++) {
			prop = props.get(i);
			newStr = newStr.replace(substKey+i, prop);
		}
		return newStr;
	}

}
