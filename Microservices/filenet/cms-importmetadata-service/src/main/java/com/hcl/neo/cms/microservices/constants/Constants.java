package com.hcl.neo.cms.microservices.constants;

/**
 * Constant File for Import Metadata operation
 * @author sakshi_ja
 *
 */
public interface Constants {

	public static final String PATH_SEPARATOR = "/";
	public static final String YES = "yes";

	public static final String MSG_ONLY_HTTP_AUTH_SUPPORTED = "Only basic http authentication is supported.";
	public static final String MSG_NULL_SYS_OBJECT = "NULL IDfSysObject reference.";
	public static final String MSG_OBJECT_CREATED = "Object created successfully.";
	public static final String MSG_OBJECT_UPDATED = "Object updated successfully.";
	public static final String MSG_INVALID_PATH = "Invalid Path %s.";
	public static final String MSG_INVALID_DOCUMENT_TYPE = "No record found in table dynamic_folder_creation for document_type %s.";
	public static final String MSG_MISSING_REQURIED_ATTR = "Create request does not have required attributes %s.";
	public static final String MSG_MISSING_FILE_EXTENSION = "File does not have any extension.";


	public static final String ATTR_R_OBJECT_ID = "r_object_id";
	public static final String ATTR_I_FOLDER_ID = "i_folder_id";
	public static final String ATTR_R_FOLDER_PATH = "r_folder_path";
	public static final String ATTR_R_OBJECT_TYPE = "r_object_type";

	public static final String ATTR_OBJECT_TYPE = "object_type";
	public static final String ATTR_DOCUMENT_TYPE = "document_type";
	public static final String ATTR_PREPEND_PATH = "prepend_path";
	public static final String ATTR_DYNAMIC_PICK = "dynamic_pick";
	public static final String ATTR_APPENDED_PATH = "appended_path";
	public static final String ATTR_YEAR_SPECIFIC = "year_specific";


	public static final String TYPE_ADE_DOC = "object1";
	public static final String TYPE_ADE_ENROLLMENT_DOC = "object2";
	public static final String TYPE_ADE_NOTIFICATION = "object3";

	public static final String ATTR_ADE_OBJECTTYPE = "object1";
	public static final String ATTR_ADE_DOC_NAME = "object2";
	public static final String ATTR_ADE_SYSTEM = "object3";
	public static final String ATTR_ADE_DOCUMENTSUBTYPE = "objecttype_subtype";

	public static final String REQ_ATTR_ADE_OBJECTTYPE = "objectType";
	public static final String REQ_ATTR_ADE_SYSTEM = "system";
	public static final String REQ_ATTR_ADE_GUID = "guid";
	public static final String REQ_ATTR_ADE_DOCUMENTSUBTYPE = "documentSubtype";
	

	public static final String ATTR_DOCUMENT_DISPLAY_TITLE = "Document Title";
	public static final String ATTR_FOLDER_DISPLAY_NAME = "Folder Name";
	public static final String ATTR_TITLE = "title";
}

