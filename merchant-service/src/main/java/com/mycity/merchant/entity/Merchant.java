package com.mycity.merchant.entity;

import java.time.LocalDate;

public class Merchant {


	 private Long merchantId;
	    private String businessName;
	    private String registrationNumber;
	    private String email;
	    private String contactPersonName;
	    private String contactPhoneNumber;
	    private String businessAddress;
	    private String businessWebsite;
	    private String businessCategory;
	    private LocalDate incorporationDate;
	    private String legalDocumentType;
	    private String legalDocumentNumber;
	    private String legalDocumentImageUrl; // Cloudinary URL
	    private String proofOfAddressDocumentType;
	    private String proofOfAddressDocumentNumber;
	    private String proofOfAddressDocumentImageUrl; // Cloudinary URL
	    private String bankAccountNumber;
	    private String bankIfscCode;
	    private String bankAccountHolderName;
	    private boolean kycVerified;
	    private String kycVerificationStatus;
	    private boolean enabled;
	

	
}
