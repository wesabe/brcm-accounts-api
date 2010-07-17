package com.wesabe.api.accounts.presenters;

import com.wesabe.api.accounts.entities.Attachment;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link Attachment} instances.
 * 
 * @author coda
 *
 */
public class AttachmentPresenter {
	public XmlsonObject present(Attachment attachment) {
		final XmlsonObject root = new XmlsonObject("attachment");
		root.addProperty("guid", attachment.getGuid());
		root.addProperty("filename", attachment.getFilename());
		root.addProperty("content-type", attachment.getContentType());
		root.addProperty("size", attachment.getSize());
		root.addProperty("description", attachment.getDescription());
		return root;
	}
}
