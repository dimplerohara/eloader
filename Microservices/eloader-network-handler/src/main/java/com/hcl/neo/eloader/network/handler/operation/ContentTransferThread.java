package com.hcl.neo.eloader.network.handler.operation;

import java.util.concurrent.CountDownLatch;

class ContentTransferThread extends Thread {

	private CountDownLatch finishSignal;
	
	protected ContentTransferThread(CountDownLatch finish){
		this.finishSignal = finish;
	}

	protected CountDownLatch getFinishSignal() {
		return finishSignal;
	}
}
