/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hcl.neo.eloader.microservices.model;

import java.io.Serializable;
import java.util.Date;

import com.hcl.neo.eloader.microservices.util.IngestionUtil;

/**
 *
 * @author Pankaj.Srivastava
 */
public class LocalJobInfo implements Serializable{
    private static final long serialVersionUID = 2L;
    private String jobType;
    private Object localJob;
    private String jobName;
    private long jobId;
    private int processStep = 1;
    private String tempDirPath;
    private long ftpServerId;
    private String localJobError;
    private String status;
    private String clientos;
    private String cacheFilePath;
    private final String creationDate;
    
    public LocalJobInfo() {
        this.creationDate = IngestionUtil.formatDate(new Date(), "yyyy/MM/dd HH:mm:ss");
    }
    
    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Object getLocalJob() {
        return localJob;
    }

    public void setLocalJob(Object localJob) {
        this.localJob = localJob;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public int getProcessStep() {
        return processStep;
    }

    public void setProcessStep(int processStep) {
        this.processStep = processStep;
    }

    public String getTempDirPath() {
        return tempDirPath;
    }

    public void setTempDirPath(String tempDirPath) {
        this.tempDirPath = tempDirPath;
    }

    public long getFtpServerId() {
        return ftpServerId;
    }

    public void setFtpServerId(long ftpServerId) {
        this.ftpServerId = ftpServerId;
    }

    public String getLocalJobError() {
        return localJobError;
    }

    public void setLocalJobError(String localJobError) {
        this.localJobError = localJobError;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClientos() {
        return clientos;
    }

    public void setClientos(String clientos) {
        this.clientos = clientos;
    }

    public String getCacheFilePath() {
        return cacheFilePath;
    }

    public void setCacheFilePath(String cacheFilePath) {
        this.cacheFilePath = cacheFilePath;
    }

    public String getCreationDate() {
        return creationDate;
    }    

    @Override
    public String toString() {
        return "LocalJobInfo{" + "jobType=" + jobType + ", localJob=" + localJob + ", jobName=" + jobName + ", jobId=" + jobId + ", processStep=" + processStep + ", tempDirPath=" + tempDirPath + ", ftpServerId=" + ftpServerId + ", localJobError=" + localJobError + ", status=" + status + ", clientos=" + clientos + ", cacheFilePath=" + cacheFilePath + ", creationDate=" + creationDate + '}';
    }
}
