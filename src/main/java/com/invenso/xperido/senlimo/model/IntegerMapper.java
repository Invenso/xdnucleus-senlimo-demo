package com.invenso.xperido.senlimo.model;

import org.mapstruct.Mapper;

/**
 * Maps an integer to a boolean value (null = null, 1 = true, not 1 = false)
 */
@Mapper
public abstract class IntegerMapper {
	public Boolean integerToBoolean(Integer value) {
		return value != null ? value == 1 : null;
	}
}
