package com.hcl.dctm.data.params;

import java.util.ArrayList;
import java.util.List;

public class CheckinContentParams extends DctmCommonParam {

	private List<CheckinObject> checkinObjectList = new ArrayList<CheckinObject>();
	private boolean importResourceFork;
	private boolean checkinAsSameVersion;
	
	@Override
	public boolean isValid() {
		return null != getCheckinObjectList() 
			&& getCheckinObjectList().size() > 0;  
	}

	public boolean isImportResourceFork() {
		return importResourceFork;
	}

	public void setImportResourceFork(boolean importResourceFork) {
		this.importResourceFork = importResourceFork;
	}

	public boolean isCheckinAsSameVersion() {
		return checkinAsSameVersion;
	}

	public void setCheckinAsSameVersion(boolean checkinAsSameVersion) {
		this.checkinAsSameVersion = checkinAsSameVersion;
	}

	public List<CheckinObject> getCheckinObjectList() {
		return checkinObjectList;
	}

	public void setCheckinObjectList(List<CheckinObject> checkinObjectList) {
		this.checkinObjectList = checkinObjectList;
	}

	public static CheckinContentParams newObject(){
		return new CheckinContentParams();
	}
}