package com.hengtian.HTMobileLibrary.formEdittextValidator;

public class PersonNameValidator extends RegexpValidator {
	public PersonNameValidator(String message) {
		// will allow people with hyphens in his name or surname. Supports also unicode
		super(message, "[\\p{L}-]+");
	}
}
