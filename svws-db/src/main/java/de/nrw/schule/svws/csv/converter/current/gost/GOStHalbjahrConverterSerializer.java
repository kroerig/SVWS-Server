package de.nrw.schule.svws.csv.converter.current.gost;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import de.nrw.schule.svws.core.types.gost.GostHalbjahr;
import de.nrw.schule.svws.db.converter.current.gost.GOStHalbjahrConverter;

/**
 * Diese Klasse ist ein Serialisierer und serialisiert die Datenbankdarstellung 
 * als GostHalbjahr.
 */
public class GOStHalbjahrConverterSerializer extends StdSerializer<GostHalbjahr> {

	private static final long serialVersionUID = -5121032441860213925L;

	/**
	 * Erzeugt einen neuen Serialisierer
	 */
	public GOStHalbjahrConverterSerializer() {
		super(GostHalbjahr.class);
	}
	
	/**
	 * Erzeugt einen neuen Serialisierer unter Angabe der {@link Class}
	 * 
	 * @param t   das Klassen-Objekt
	 */
	public GOStHalbjahrConverterSerializer(Class<GostHalbjahr> t) {
		super(t);
	}

	@Override
	public void serialize(GostHalbjahr value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(GOStHalbjahrConverter.instance.convertToDatabaseColumn(value).toString());
	}

}
