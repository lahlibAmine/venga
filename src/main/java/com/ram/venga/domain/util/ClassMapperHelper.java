package com.ram.venga.domain.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ClassMapperHelper {

	public static Map<String, Object> mapObjectToProperties(Object obj, String properties) {
		Map<String, Object> propertiesMap = new HashMap<>();

		// Get all fields, including private ones, from the object's class and its
		// superclasses
		Field[] fields = obj.getClass().getDeclaredFields();

		if (!properties.isEmpty()) {
			for (Field field : fields) {
				try {
					if (properties.contains(field.getName())) {
						// Set the field to be accessible, in case it's private
						field.setAccessible(true);
						// Add the field name and its value to the map
						propertiesMap.put(field.getName(), field.get(obj));
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace(); // Handle the exception as needed
				}
			}
		}
		return propertiesMap;
	}
}

