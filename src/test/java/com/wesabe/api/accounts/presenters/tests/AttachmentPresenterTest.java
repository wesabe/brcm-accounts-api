package com.wesabe.api.accounts.presenters.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.entities.Attachment;
import com.wesabe.api.accounts.presenters.AttachmentPresenter;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class AttachmentPresenterTest {
	public static class Presenting_An_Attachment {
		private Attachment attachment;
		private AttachmentPresenter presenter;
		
		@Before
		public void setup() throws Exception {
			this.attachment = mock(Attachment.class);
			when(attachment.getGuid()).thenReturn("1234567890");
			when(attachment.getFilename()).thenReturn("receipt.pdf");
			when(attachment.getContentType()).thenReturn("application/pdf");
			when(attachment.getSize()).thenReturn(284569);
			when(attachment.getDescription()).thenReturn("My receipt.");
			
			this.presenter = new AttachmentPresenter();
		}
		
		@Test
		public void itIsNamedAttachment() throws Exception {
			final XmlsonObject representation = presenter.present(attachment);
			
			assertThat(representation.getName(), is("attachment"));
		}
		
		@Test
		public void itHasAGuid() throws Exception {
			final XmlsonObject representation = presenter.present(attachment);
			
			assertThat(representation.getString("guid"), is("1234567890"));
		}
		
		@Test
		public void itHasAFilename() throws Exception {
			final XmlsonObject representation = presenter.present(attachment);
			
			assertThat(representation.getString("filename"), is("receipt.pdf"));
		}
		
		@Test
		public void itHasAContentType() throws Exception {
			final XmlsonObject representation = presenter.present(attachment);
			
			assertThat(representation.getString("content-type"), is("application/pdf"));
		}
		
		@Test
		public void itHasASize() throws Exception {
			final XmlsonObject representation = presenter.present(attachment);
			
			assertThat(representation.getInteger("size"), is(284569));
		}
		
		@Test
		public void itHasADescription() throws Exception {
			final XmlsonObject representation = presenter.present(attachment);
			
			assertThat(representation.getString("description"), is("My receipt."));
		}
	}
}
