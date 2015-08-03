package com.hengtian.HTMobileLibrary.formEdittextValidator;

public class AlphabeticValidator extends RegexpValidator {
	public AlphabeticValidator(String message) {
		super(message, "[a-zA-Z \\./-]*");
	}
}
