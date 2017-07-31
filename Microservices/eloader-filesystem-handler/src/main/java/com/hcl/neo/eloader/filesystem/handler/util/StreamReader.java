package com.hcl.neo.eloader.filesystem.handler.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.hcl.neo.eloader.common.Logger;

public class StreamReader extends Thread{
	private InputStream is = null;
    private String message = null;
 
    public StreamReader(InputStream is) {
    	this.is = is;
    }

    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ( (line = br.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            this.message = buffer.toString();
        } 
        catch (Throwable e) {
        	Logger.error(getClass(), e);
        }
    }

	public String getMessage() {
		return message;
	}
}
