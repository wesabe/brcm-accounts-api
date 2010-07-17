package com.wesabe.api.accounts.providers.tests;

import static org.fest.assertions.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.providers.XmlsonWriterProvider;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class XmlsonWriterProviderTest {
	public static class Calculating_Message_Body_Size {
		private XmlsonObject t;
		private Class<?> type;
		private Type genericType;
		private Annotation[] annotations;
		private MediaType mediaType;
		private XmlsonWriterProvider provider;
		
		@Before
		public void setup() throws Exception {
			this.provider = new XmlsonWriterProvider();
		}

		@Test
		public void itPuntsOnPrecalculation() throws Exception {
			assertThat(provider.getSize(t, type, genericType, annotations, mediaType)).isEqualTo(-1);
		}
	}
	
	public static class Calculating_Writability {
		private Type genericType;
		private Annotation[] annotations;
		private XmlsonWriterProvider provider;
		
		@Before
		public void setup() throws Exception {
			this.provider = new XmlsonWriterProvider();
		}
		
		@Test
		public void itIsWritableForAnXmlsonObjectAndJson() throws Exception {
			assertThat(
				provider.isWriteable(XmlsonObject.class, genericType, annotations, MediaType.APPLICATION_JSON_TYPE)
			).isTrue();
		}
		
		@Test
		public void itIsWritableForAnXmlsonObjectAndXml() throws Exception {
			assertThat(
				provider.isWriteable(XmlsonObject.class, genericType, annotations, MediaType.APPLICATION_XML_TYPE)
			).isTrue();
		}
		
		@Test
		public void itIsNotWritableForAnXmlsonObjectAndHtml() throws Exception {
			assertThat(
				provider.isWriteable(XmlsonObject.class, genericType, annotations, MediaType.TEXT_HTML_TYPE)
			).isFalse();
		}
		
		@Test
		public void itIsNotWritableForAnStringAndXml() throws Exception {
			assertThat(
				provider.isWriteable(String.class, genericType, annotations, MediaType.APPLICATION_XML_TYPE)
			).isFalse();
		}
	}
	
	public static class Outputting_JSON {
		private XmlsonObject doc;
		private XmlsonWriterProvider provider;
		private Type genericType;
		private Annotation[] annotations;
		private MultivaluedMap<String, Object> httpHeaders;
		private ByteArrayOutputStream entityStream;
		
		@Before
		public void setup() throws Exception {
			this.doc = new XmlsonObject("doc");
			doc.add(new XmlsonObject("object").addProperty("awesome", true));
			
			this.entityStream = new ByteArrayOutputStream();
			
			this.provider = new XmlsonWriterProvider();
		}
		
		@Test
		public void itFormatsTheObjectAsJSON() throws Exception {
			provider.writeTo(doc, XmlsonObject.class, genericType, annotations, MediaType.APPLICATION_JSON_TYPE, httpHeaders, entityStream);
			
			assertThat(entityStream.toString()).isEqualTo("{\"object\":{\"awesome\":true}}");
		}
	}
	
	public static class Outputting_XML {
		private XmlsonObject doc;
		private XmlsonWriterProvider provider;
		private Type genericType;
		private Annotation[] annotations;
		private MultivaluedMap<String, Object> httpHeaders;
		private ByteArrayOutputStream entityStream;
		
		@Before
		public void setup() throws Exception {
			this.doc = new XmlsonObject("doc");
			doc.add(new XmlsonObject("object").addProperty("awesome", true));
			
			this.entityStream = new ByteArrayOutputStream();
			
			this.provider = new XmlsonWriterProvider();
		}
		
		@Test
		public void itFormatsTheObjectAsXML() throws Exception {
			provider.writeTo(doc, XmlsonObject.class, genericType, annotations, MediaType.APPLICATION_XML_TYPE, httpHeaders, entityStream);
			
			assertThat(entityStream.toString()).isEqualTo("<?xml version='1.0' encoding='UTF-8'?><doc><object><awesome>true</awesome></object></doc>");
		}
	}

}
