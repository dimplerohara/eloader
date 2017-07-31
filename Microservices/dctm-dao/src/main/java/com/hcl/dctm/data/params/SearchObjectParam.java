package com.hcl.dctm.data.params;

import java.util.List;
import java.util.Map;

public class SearchObjectParam extends DctmCommonParam {

	@Override
	public boolean isValid() {
		if(getObjectType() == null && getQuery() == null){
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "SearchObjectParam [selectAttributes=" + selectAttributes + ", conditionalAttributes="
				+ conditionalAttributes + ", objectType=" + objectType + ", query=" + query + ", fullTextFlag="
				+ fullTextFlag + ", ftQueryString=" + ftQueryString + "]";
	}

	/**
	 * @return the selectAttributes
	 */
	public List<String> getSelectAttributes() {
		return selectAttributes;
	}
	/**
	 * @param selectAttributes the selectAttributes to set
	 */
	public void setSelectAttributes(List<String> selectAttributes) {
		this.selectAttributes = selectAttributes;
	}
	/**
	 * @return the conditionalAttributes
	 */
	public Map<String, String> getConditionalAttributes() {
		return conditionalAttributes;
	}
	/**
	 * @param conditionalAttributes the conditionalAttributes to set
	 */
	public void setConditionalAttributes(Map<String, String> conditionalAttributes) {
		this.conditionalAttributes = conditionalAttributes;
	}
	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}
	/**
	 * @param objectType the objectType to set
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}
	/**
	 * @return the fullTextFlag
	 */
	public boolean isFullTextFlag() {
		return fullTextFlag;
	}
	/**
	 * @param fullTextFlag the fullTextFlag to set
	 */
	public void setFullTextFlag(boolean fullTextFlag) {
		this.fullTextFlag = fullTextFlag;
	}

	/**
	 * @return the ftQueryString
	 */
	public String getFtQueryString() {
		return ftQueryString;
	}
	/**
	 * @param ftQueryString the ftQueryString to set
	 */
	public void setFtQueryString(String ftQueryString) {
		this.ftQueryString = ftQueryString;
	}

	private List<String> selectAttributes;
	private Map<String,String> conditionalAttributes;
	private String objectType;
	private String query;
	private boolean fullTextFlag;
	private String ftQueryString;
}

