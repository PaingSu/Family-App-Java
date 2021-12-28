package com.startinpoint.proj.familyapp.webservice.entity.enums;

/**
 * @author ThoonSandy
 * @since 04-05-2018
 *
 */
public enum ScrollStatus {
	
	BEFORE("before"),
	AFTER("after");

	ScrollStatus(String id){
		this.id = id;
	}
	private String id;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	
}
