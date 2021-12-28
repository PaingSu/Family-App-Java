package com.startinpoint.proj.familyapp.config;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;

/**
 * Serialize date to dd/MM/yyyy
 * @author nankhinmhwe
 *
 */
public class DateSerializer extends StdSerializer<Date>{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private SimpleDateFormat formatter 
	      = new SimpleDateFormat(FamilyAppConst.CALENDAR_DATE_FORMAT);
	 
	    public DateSerializer() {
	        this(null);
	    }
	 
	    public DateSerializer(Class t) {
	        super(t);
	    }
	     
	    @Override
	    public void serialize (Date value, JsonGenerator gen, SerializerProvider arg2)
	      throws IOException, JsonProcessingException {
	        gen.writeString(formatter.format(value));
	    }
}
