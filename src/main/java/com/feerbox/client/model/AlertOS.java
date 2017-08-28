package com.feerbox.client.model;

public class AlertOS {
	private boolean less;
	private long limit;

	public enum AlertOSType {
		NOTENGOUGHUPTIME, MAXIMUMDISKOCCUPANCY

	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public boolean isLess() {
		return less;
	}

	public void setLess(boolean less) {
		this.less = less;
	}

	public boolean generateAlert(double thershold) {
		if(this.less){
			if(thershold<this.limit){
				return true;
			}
		} else{
			if(thershold>this.limit){
				return true;
			}
		}
		return false;
	}

}
