package com.sqlite.model;

import java.io.Serializable;

public class Project implements Serializable {

	public static final String TAG = "Project";
	private static final long serialVersionUID = -7406082437623008161L;

	private long mId;
	private String mName;
	private String mDescription;
	private String mTech;
	private double mCost;
	private Company mCompany;

	public Project() {
		
	}

	public Project(String name, String description, String tech,String email, double cost) {
		this.mName = name;
		this.mDescription = description;
		this.mTech = tech;
		this.mCost = cost;
	}

	public long getId() {
		return mId;
	}

	public void setId(long mId) {
		this.mId = mId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public String getTech() {
		return mTech;
	}

	public void setTech(String mTech) {
		this.mTech = mTech;
	}

	public double getCost() {
		return mCost;
	}

	public void setCost(double mCost) {
		this.mCost = mCost;
	}

	public Company getCompany() {
		return mCompany;
	}

	public void setCompany(Company mCompany) {
		this.mCompany = mCompany;
	}
}
