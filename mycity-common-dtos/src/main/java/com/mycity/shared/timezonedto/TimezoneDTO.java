package com.mycity.shared.timezonedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimezoneDTO {
	
    private Long timezoneId;
    private String name;
}

