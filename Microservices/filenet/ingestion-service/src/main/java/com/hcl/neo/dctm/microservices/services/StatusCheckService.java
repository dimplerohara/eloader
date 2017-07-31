package com.hcl.neo.dctm.microservices.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hcl.neo.dctm.microservices.exceptions.ServiceException;
import com.hcl.neo.dctm.microservices.model.ServiceResponse;

@Service
public class StatusCheckService {

	public ServiceResponse<String> getStatus() throws ServiceException{
		ServiceResponse<String> res = new ServiceResponse<String>();
		try{
			res.setCode(HttpStatus.OK.value());
			res.setMessage(HttpStatus.OK.toString());
			res.setData("It's working!");
		}
		catch(Throwable e){
			res.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			res.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			res.setData(e.getMessage());
		}
		return res;
	}
}
