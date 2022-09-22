package de.nrw.schule.svws.csv.converter.migration;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import de.nrw.schule.svws.db.converter.current.StringToIntegerConverter;

/**
 * Diese Klasse ist ein Deserialisierer für Strinwerte. Sie deserialisiert die 
 * Datenbankdarstellung als Integer.
 */
public class MigrationStringToIntegerConverterDeserializer extends StdDeserializer<Integer> {

	private static final long serialVersionUID = 899602939694388520L;

	/**
	 * Erzeugt einen neuen Deerialisierer
	 */
	public MigrationStringToIntegerConverterDeserializer() {
		super(Integer.class);
	}
	
	/**
	 * Erzeugt einen neuen Deserialisierer unter Angabe der {@link Class}
	 * 
	 * @param t   das Klassen-Objekt
	 */
	protected MigrationStringToIntegerConverterDeserializer(Class<Integer> t) {
		super(t);
	}

	@Override
	public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return StringToIntegerConverter.instance.convertToEntityAttribute(p.getText());
	}
	
}
