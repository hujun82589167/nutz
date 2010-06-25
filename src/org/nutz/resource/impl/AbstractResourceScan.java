package org.nutz.resource.impl;

import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugin.Plugin;
import org.nutz.resource.ResourceScan;

public abstract class AbstractResourceScan implements ResourceScan, Plugin {
	
	private static final Log LOG = Logs.getLog(AbstractResourceScan.class);

	protected String getClassPath() {
		Properties properties = System.getProperties();
		if (properties != null)
			for (Entry<Object, Object> entry : properties.entrySet())
				if (entry.getValue() != null
					&& "java.class.path".equalsIgnoreCase(entry.getKey().toString()))
					return entry.getValue().toString();
		Map<String, String> env = System.getenv();
		if (env != null)
			for (Entry<String, String> entry : env.entrySet())
				if ("CLASSPATH".equalsIgnoreCase(entry.getKey()))
					return entry.getValue();
		return null;
	}
	
	protected String [] splitClassPath() {
		String CLASSPATH = getClassPath();
		if (CLASSPATH != null) {
			if (LOG.isDebugEnabled())
				LOG.debugf("Scan resource in ClassPath : %s", CLASSPATH);
			Object pathSeparator = System.getProperties().get("path.separator");
			if (pathSeparator == null) {
				if (Lang.isWin())
					pathSeparator = ";";
				else
					pathSeparator = ":";
			}
			return CLASSPATH.split(pathSeparator.toString());
		}
		return null;
	}
}
