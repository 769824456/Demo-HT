package com.hengtian.HTMobileLibrary.formEdittextValidator;


public class AlphabeticAndNumericValidator extends RegexpValidator {
	public AlphabeticAndNumericValidator(String message) {
		super(message, 	"[a-zA-Z0-9 \\./-]*");
	}
	
}
