package com.sqlite.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sqlite.model.Company;
import com.sqlite.model.Project;

public class CompanyDAO {

	public static final String TAG = "CompanyDAO";

	// Database fields
	private SQLiteDatabase mDatabase;
	private DBHelper mDbHelper;
	private Context mContext;
	private String[] mAllColumns = { DBHelper.COLUMN_COMPANY_ID,
			DBHelper.COLUMN_COMPANY_NAME, DBHelper.COLUMN_COMPANY_ADDRESS,
			DBHelper.COLUMN_COMPANY_WEBSITE,
			DBHelper.COLUMN_COMPANY_PHONE_NUMBER };

	public CompanyDAO(Context context) {
		this.mContext = context;
		mDbHelper = new DBHelper(context);
		// open the database
		try {
			open();
		} catch (SQLException e) {
			Log.e(TAG, "SQLException on openning database " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void open() throws SQLException {
		mDatabase = mDbHelper.getWritableDatabase();
	}

	public void close() {
		mDbHelper.close();
	}

	public Company createCompany(String name, String address, String website,
			String phoneNumber) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_COMPANY_NAME, name);
		values.put(DBHelper.COLUMN_COMPANY_ADDRESS, address);
		values.put(DBHelper.COLUMN_COMPANY_WEBSITE, website);
		values.put(DBHelper.COLUMN_COMPANY_PHONE_NUMBER, phoneNumber);
		long insertId = mDatabase
				.insert(DBHelper.TABLE_COMPANIES, null, values);
		Cursor cursor = mDatabase.query(DBHelper.TABLE_COMPANIES, mAllColumns,
				DBHelper.COLUMN_COMPANY_ID + " = " + insertId, null, null,
				null, null);
		cursor.moveToFirst();
		Company newCompany = cursorToCompany(cursor);
		cursor.close();
		return newCompany;
	}

	public void deleteCompany(Company company) {
		long id = company.getId();
		// delete all projects of this company
		ProjectDAO projectDao = new ProjectDAO(mContext);
		List<Project> listProjects = projectDao.getProjectsOfCompany(id);
		if (listProjects != null && !listProjects.isEmpty()) {
			for (Project e : listProjects) {
				projectDao.deleteProject(e);
			}
		}

		System.out.println("the deleted company has the id: " + id);
		mDatabase.delete(DBHelper.TABLE_COMPANIES, DBHelper.COLUMN_COMPANY_ID
				+ " = " + id, null);
	}

	public List<Company> getAllCompanies() {
		List<Company> listCompanies = new ArrayList<Company>();

		Cursor cursor = mDatabase.query(DBHelper.TABLE_COMPANIES, mAllColumns,
				null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Company company = cursorToCompany(cursor);
				listCompanies.add(company);
				cursor.moveToNext();
			}

			// make sure to close the cursor
			cursor.close();
		}
		return listCompanies;
	}

	public Company getCompanyById(long id) {
		Cursor cursor = mDatabase.query(DBHelper.TABLE_COMPANIES, mAllColumns,
				DBHelper.COLUMN_COMPANY_ID + " = ?",
				new String[] { String.valueOf(id) }, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		Company company = cursorToCompany(cursor);
		return company;
	}

	protected Company cursorToCompany(Cursor cursor) {
		Company company = new Company();
		company.setId(cursor.getLong(0));
		company.setName(cursor.getString(1));
		company.setAddress(cursor.getString(2));
		company.setWebsite(cursor.getString(3));
		company.setPhoneNumber(cursor.getString(4));
		return company;
	}

}
