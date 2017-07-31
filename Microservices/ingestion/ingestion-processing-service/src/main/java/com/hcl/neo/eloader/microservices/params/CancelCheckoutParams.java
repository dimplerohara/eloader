package com.hcl.neo.eloader.microservices.params;

import java.util.List;

public class CancelCheckoutParams extends ExportParams {
    private List<String> successfullObjects;

    public List<String> getSuccessfullObjects() {
        return successfullObjects;
    }

    public void setSuccessfullObjects(List<String> successfullObjects) {
        this.successfullObjects = successfullObjects;
    }

    @Override
    public String toString() {
        return "CancelCheckoutParams{" + "successfullObjects=" + successfullObjects + '}';
    }    
}
