package com.hcl.neo.cms.microservices.services;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.filenet.api.core.IndependentObject;
import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.params.CreateObjectParam;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.neo.cms.microservices.constants.Constants;

/**
 * Helper Class for metadata operations
 * @author sakshi_ja
 *
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode=ScopedProxyMode.TARGET_CLASS)
public class DctmServiceHelper {


	/**
	 * Method to create folders that does not exists while creating filenet object.
	 * @param cmsDao
	 * @param path
	 * @param repository
	 * @throws Exception
	 * @throws Throwable
	 */
	public void createPath(CmsDao cmsDao, String path, String repository) throws Exception, Throwable{
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
			IndependentObject object = cmsDao.getObjectByIdentity(ObjectIdentity.newObject(null, tempPath, null, Constants.TYPE_FOLDER),repository);
			if(null == object){
				CreateObjectParam params = new CreateObjectParam();
				String name = FilenameUtils.getBaseName(tempPath);
				String parentPath = FilenameUtils.getPathNoEndSeparator(tempPath);
				params.setObjectType(Constants.TYPE_FOLDER);
				parentPath = parentPath.startsWith(Constants.PATH_SEPARATOR) ? parentPath : Constants.PATH_SEPARATOR + parentPath;
				params.setDestIdentity(ObjectIdentity.newObject(null, parentPath, null, Constants.TYPE_FOLDER));
				params.setObjectName(name);
				cmsDao.createObject(params,repository);
			}
		}
	}
}
