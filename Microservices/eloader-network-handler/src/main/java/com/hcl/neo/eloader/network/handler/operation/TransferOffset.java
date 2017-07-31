package com.hcl.neo.eloader.network.handler.operation;

import com.hcl.neo.eloader.common.Logger;

class TransferOffset {
	
	private long beginOffset;
	private long endOffset;
	
	protected long getBeginOffset() {
		return beginOffset;
	}
	
	protected void setBeginOffset(long beginOffset) {
		this.beginOffset = beginOffset;
	}
	
	protected long getEndOffset() {
		return endOffset;
	}
	
	protected void setEndOffset(long endOffset) {
		this.endOffset = endOffset;
	}
	
	protected long getByteCount(){
		return this.endOffset - this.beginOffset;
	}
	
	protected static TransferOffset[] calcTransferOffsets(long fileSize, int streams){
		Logger.info(TransferOffset.class, "fileSize: "+fileSize);
		long singleThreadSize = fileSize / streams;
		if(streams > 1) singleThreadSize--;
		TransferOffset[] threadOffsets = new TransferOffset[streams];
		long beginOffset = 0;
		long endOffset = beginOffset + singleThreadSize;
		for(int index=0; index<streams; index++){
			TransferOffset to = new TransferOffset();
			to.setBeginOffset(beginOffset);
			to.setEndOffset(endOffset);
			threadOffsets[index] = to;
			beginOffset = endOffset;
			endOffset = beginOffset + singleThreadSize;
			endOffset = (fileSize - endOffset) < singleThreadSize ? fileSize : endOffset;
		}
		return threadOffsets;
	}
	
	protected static TransferOffset[] calcTransferOffsets(long fileSize, int streams, long skip){
		fileSize -= skip;
		long singleThreadSize = fileSize / streams;
		if(streams > 1) singleThreadSize--;
		TransferOffset[] threadOffsets = new TransferOffset[streams];
		long beginOffset = skip;
		long endOffset = beginOffset + singleThreadSize;
		for(int index=0; index<streams; index++){
			TransferOffset to = new TransferOffset();
			to.setBeginOffset(beginOffset);
			to.setEndOffset(endOffset);
			threadOffsets[index] = to;
			beginOffset = endOffset;
			endOffset = beginOffset + singleThreadSize;
			endOffset = (fileSize - endOffset) < singleThreadSize ? fileSize : endOffset;
		}
		return threadOffsets;
	}
	
	@Override
	public String toString() {
		return "TransferOffset [beginOffset=" + beginOffset + ", endOffset=" + endOffset + "]";
	}
}