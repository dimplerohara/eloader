/*package com.hcl.neo.dctm.microservices.services;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.DctmDao;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.CreateObjectParam;
import com.hcl.dctm.data.params.ObjectIdentity;
import com.hcl.neo.dctm.microservices.constants.Constants;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode=ScopedProxyMode.TARGET_CLASS)
public class DctmServiceHelper {

	public void setPathAndTypeInMap(DctmDao dctmDao, Map<String, Object> metadata) throws Throwable{
		StringBuilder dql = new StringBuilder();
		dql.append("select ");
		dql.append(Constants.ATTR_DOCUMENT_TYPE).append(", ");
		dql.append(Constants.ATTR_OBJECT_TYPE).append(", ");
		dql.append(Constants.ATTR_PREPEND_PATH).append(", ");
		dql.append(Constants.ATTR_DYNAMIC_PICK).append(", ");
		dql.append(Constants.ATTR_APPENDED_PATH).append(" ");
		dql.append("from ");
		dql.append(Constants.TABL_DYNAMIC_FOLDER_CREATION).append(" ");
		dql.append("where ");
		dql.append(Constants.ATTR_DOCUMENT_TYPE).append("='").append(metadata.get(Constants.ATTR_ADE_DOCUMENTSUBTYPE).toString().replaceAll("'", "''")).append("'");
		
		List<Map<String, String>> result = dctmDao.execSelect(dql.toString());
		
		if(null == result || result.size() == 0) throw new DctmException(String.format(Constants.MSG_INVALID_DOCUMENT_TYPE, metadata.get(Constants.ATTR_ADE_DOCUMENTSUBTYPE)));
		
		String temp = "";
		StringBuilder path = new StringBuilder();
		
		for(Map<String, String> record : result){
			temp = record.get(Constants.ATTR_PREPEND_PATH);
			if(null != temp && !temp.isEmpty()){
				path.append(temp.trim());
			}
			temp = record.get(Constants.ATTR_DYNAMIC_PICK);
			if(null != temp && !temp.isEmpty()){
				path.append(Constants.PATH_SEPARATOR);
				path.append(metadata.get(temp.trim()));
			}
			temp = record.get(Constants.ATTR_APPENDED_PATH);
			if(null != temp && !temp.isEmpty()){
				path.append(Constants.PATH_SEPARATOR);
				path.append(temp.trim());
			}
			temp = record.get(Constants.ATTR_YEAR_SPECIFIC);
			if(null != temp && !temp.isEmpty() && Constants.YES.equalsIgnoreCase(temp.trim())){
				path.append(Constants.PATH_SEPARATOR);
				path.append(Calendar.getInstance().get(Calendar.YEAR));
			}
			temp = record.get(Constants.ATTR_OBJECT_TYPE);
			if(null != temp && !temp.isEmpty()){
				metadata.put(Constants.ATTR_R_OBJECT_TYPE, temp.trim());
			}
		}
		metadata.put(Constants.ATTR_R_FOLDER_PATH, path.toString());
	}
	
	public void createPath(DctmDao dctmDao, String path) throws Exception, Throwable{
		if(null == path || path.isEmpty()){
			throw new Exception(String.format(Constants.MSG_INVALID_PATH, path));
		}
		path = path.trim();
		path = path.startsWith(Constants.PATH_SEPARATOR) ? path : Constants.PATH_SEPARATOR + path;
		path = path.endsWith(Constants.PATH_SEPARATOR) ? path : path + Constants.PATH_SEPARATOR;
		
		int count = StringUtils.countMatches(path, Constants.PATH_SEPARATOR);
		for(int index=1; index<=count; index++){
			int pos = StringUtils.ordinalIndexOf(path, Constants.PATH_SEPARATOR, index);
			String tempPath = path.substring(0, pos);
			if(tempPath.isEmpty()) continue;

			tempPath = tempPath.endsWith(Constants.PATH_SEPARATOR) ? tempPath.substring(0, tempPath.length()-1) : tempPath;
			
			IDfSysObject object = dctmDao.getObjectByIdentity(ObjectIdentity.newObject(null, tempPath.trim(), null, null));
			if(null == object){
				CreateObjectParam params = new CreateObjectParam();
				String name = FilenameUtils.getBaseName(tempPath);
				String parentPath = FilenameUtils.getPathNoEndSeparator(tempPath);
				if(null == parentPath || parentPath.isEmpty()){
					params.setObjectType(Constants.TYPE_DM_CABINET);
				}
				else{
					params.setObjectType(Constants.TYPE_DM_FOLDER);
					parentPath = parentPath.startsWith(Constants.PATH_SEPARATOR) ? parentPath : Constants.PATH_SEPARATOR + parentPath;
					params.setDestIdentity(ObjectIdentity.newObject(null, parentPath, null, null));
				}
				params.setObjectName(name);
				dctmDao.createObject(params);
			}
		}
	}
}
*/