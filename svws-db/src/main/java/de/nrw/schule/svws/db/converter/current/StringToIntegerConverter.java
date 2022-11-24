package de.nrw.schule.svws.db.converter.current;

import de.nrw.schule.svws.db.converter.DBAttributeConverter;
import jakarta.persistence.Converter;


/**
 * Diese Klasse dient dem Konvertieren eines String zu Integer.
 *  
 */
@Converter
public class StringToIntegerConverter extends DBAttributeConverter<Integer, String> {

	/** Die Instanz des Konverters */
	public final static StringToIntegerConverter instance = new StringToIntegerConverter();
	
	@Override
	public String convertToDatabaseColumn(Integer value) {
		if (value == null)
			return null;
		return value.toString();
	}

	@Override
	public Integer convertToEntityAttribute(String dbData) {
		try {
			return Integer.parseInt(dbData);
		} catch(@SuppressWarnings("unused") NumberFormatException e) {
			return null;
		}		
	}

	@Override
	public Class<Integer> getResultType() {
		return Integer.class;
	}

	@Override
	public Class<String> getDBType() {
		return String.class;
	}

}
