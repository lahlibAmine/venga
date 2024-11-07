package com.ram.venga.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateFormatterUtil {
	
	/**
	 * 
	 * @param dateString (example : "2023-12-22")
	 * @return
	 */
	public static OffsetDateTime toDateTime(String dateString) {
		
		LocalDate localDate = null;
		OffsetDateTime offsetDateTime = null;
		if (!dateString.isEmpty()) {
	        localDate = LocalDate.parse(dateString);
	        offsetDateTime = localDate.atTime(0, 0).atOffset(ZoneOffset.UTC);
		}
        return offsetDateTime;
	}

}
