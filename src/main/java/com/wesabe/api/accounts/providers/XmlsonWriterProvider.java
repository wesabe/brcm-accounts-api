package com.wesabe.api.accounts.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.wesabe.xmlson.JsonFormatter;
import com.wesabe.xmlson.XmlFormatter;
import com.wesabe.xmlson.XmlsonFormatter;
import com.wesabe.xmlson.XmlsonMember;

/**
 * A provider for writing {@link XmlsonMember}s as entity bodies.
 * 
 * @author coda
 */
@Provider
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class XmlsonWriterProvider implements MessageBodyWriter<XmlsonMember> {
	private static final JsonFormatter JSON_FORMATTER = new JsonFormatter();
	private static final XmlFormatter XML_FORMATTER = new XmlFormatter();
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	@Override
	public long getSize(XmlsonMember t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {
		return validType(type) && validMediaType(mediaType);
	}

	private boolean validType(Class<?> type) {
		return XmlsonMember.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(XmlsonMember t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		entityStream.write(
			getFormatter(mediaType)
				.format(t)
				.getBytes(UTF_8));
	}

	private boolean validMediaType(MediaType mediaType) {
		return mediaType.equals(MediaType.APPLICATION_JSON_TYPE)
			|| mediaType.equals(MediaType.APPLICATION_XML_TYPE);
	}
	
	private XmlsonFormatter getFormatter(MediaType mediaType) {
		if (mediaType.equals(MediaType.APPLICATION_XML_TYPE)) {
			return XML_FORMATTER;
		}
		return JSON_FORMATTER;
	}
}
