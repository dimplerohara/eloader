package com.hcl.neo.cms.microservices.constants;

/**
 * Constant File for metadata operation
 * @author sakshi_ja
 *
 */
public interface Constants {

	public static final String PATH_SEPARATOR = "/";
	public static final String YES = "yes";

	public static final String MSG_ONLY_HTTP_AUTH_SUPPORTED = "Only basic http authentication is supported.";
	public static final String MSG_NULL_INDE_OBJECT = "NULL IndependentObject reference.";
	public static final String MSG_INVALID_OBJECT = "Document OR Folder object id not provided";
	public static final String MSG_OBJECT_CREATED = "Object created successfully.";
	public static final String MSG_OBJECT_UPDATED = "Object updated successfully.";
	public static final String MSG_OBJECT_DELETED = "Object deleted successfully.";
	public static final String MSG_OBJECT_METADATA_DELETED = "Requested metadata deleted successfully.";
	public static final String MSG_OBJECT_METADATA_UPDATED = "Requested metadata updated successfully.";
	public static final String MSG_OBJECT_METADATA_READ = "Requested metadata read successfully.";
	public static final String MSG_INVALID_PATH = "Invalid Path %s.";
	public static final String MSG_INVALID_DOCUMENT_TYPE = "No record found in table dynamic_folder_creation for document_type %s.";
	public static final String MSG_MISSING_REQURIED_ATTR = "Create request does not have required attributes %s.";
	public static final String MSG_MISSING_FILE_EXTENSION = "File does not have any extension.";
	
	public static final String MSG_SUCCESS = "SUCCESS";
	public static final String MSG_ERROR = "ERROR";
	public static final String MSG_FAILED = "FAILED";
	public static final String MSG_SERVER_ERROR = "Server error occurred.";
	public static final String MSG_NO_SEARCH_RESULT = "No object found with the search criteria.";
	public static final String MSG_MULTIPLE_SEARCH_RESULT = "Found more than one object to this search criteria.";
	public static final String MSG_DELETE_OPERATION = "Delete";
	
	public static final String TYPE_DOCUMENT = "DOCUMENT";
	public static final String TYPE_FOLDER = "FOLDER";

	public static final String ATTR_OBJECT_TYPE = "object_type";
	public static final String ATTR_DOCUMENT_TYPE = "document_type";
	public static final String ATTR_PREPEND_PATH = "prepend_path";
	public static final String ATTR_DYNAMIC_PICK = "dynamic_pick";
	public static final String ATTR_APPENDED_PATH = "appended_path";
	public static final String ATTR_YEAR_SPECIFIC = "year_specific";

	public static final String ATTR_R_OBJECT_TYPE="r_object_type";
	public static final String ATTR_I_FOLDER_ID="link_folder_id";
	public static final String TYPE_ADE_DOC = "object1";
	public static final String TYPE_ADE_ENROLLMENT_DOC = "object2";
	public static final String TYPE_ADE_NOTIFICATION = "object3";

	public static final String ATTR_R_FOLDER_PATH = "r_folder_path";
	public static final String ATTR_R_OBJECT_ID = "r_object_id";
	public static final String ATTR_ADE_OBJECTTYPE = "object1";
	public static final String ATTR_ADE_DOC_NAME = "object2";
	public static final String ATTR_ADE_SYSTEM = "object3";
	public static final String ATTR_ADE_DOCUMENTSUBTYPE = "objecttype_subtype";

	public static final String REQ_ATTR_ADE_OBJECTTYPE = "objectType";
	public static final String REQ_ATTR_ADE_SYSTEM = "system";
	public static final String REQ_ATTR_ADE_GUID = "guid";
	public static final String REQ_ATTR_ADE_DOCUMENTSUBTYPE = "documentSubtype";
	
	
	public static final String ATTR_MODIFIED_DATE = "DateLastModified";
	public static final String ATTR_LAST_MODIFIER = "LastModifier";
	public static final String ATTR_CREATED_DATE = "DateCreated";
	public static final String ATTR_CREATOR = "Creator";
	public static final String ATTR_NAME = "Name";
	public static final String ATTR_ID = "Id";
	public static final String ATTR_OWNER = "Owner";
	public static final String ATTR_CONTENT_SIZE = "ContentSize";
	public static final String ATTR_DOCUMENT_TITLE = "DocumentTitle";
	public static final String ATTR_FOLDER_NAME = "FolderName";
	public static final String ATTR_DOCUMENT_DISPLAY_TITLE = "Document Title";
	public static final String ATTR_FOLDER_DISPLAY_NAME = "Folder Name";
	public static final String ATTR_TITLE = "title";
	
}
