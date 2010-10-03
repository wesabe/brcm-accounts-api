package com.wesabe.api.accounts.presenters;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;

import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

public class InvalidStateExceptionPresenter {

	public XmlsonObject present(InvalidStateException exception) {
		final XmlsonObject error = new XmlsonObject("error");
		error.addProperty("type", "validation");
		
		final XmlsonArray invalidValues = new XmlsonArray("invalid-values");
		for (InvalidValue invalidValue : exception.getInvalidValues()) {
			final XmlsonObject value = new XmlsonObject("invalid-value");
			value.addProperty("class", invalidValue.getBeanClass().getName());
			value.addProperty("field", invalidValue.getPropertyName());
			value.addProperty("message", invalidValue.getMessage());
			invalidValues.add(value);
		}
		error.add(invalidValues);
		
		return error;
	}
}
