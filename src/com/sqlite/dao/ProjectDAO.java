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

public class ProjectDAO {

	public static final String TAG = "ProjectDAO";
	
	private Context mContext;
	
	// Database fields
	private SQLiteDatabase mDatabase;
	private DBHelper mDbHelper;
	private String[] mAllColumns = { DBHelper.COLUMN_PROJECT_ID, DBHelper.COLUMN_PROJECT_NAME,
			DBHelper.COLUMN_PROJECT_DESCRIPTION, DBHelper.COLUMN_PROJECT_TECH, DBHelper.COLUMN_PROJECT_COST,
			 DBHelper.COLUMN_PROJECT_COMPANY_ID };

	public ProjectDAO(Context context) {
		mDbHelper = new DBHelper(context);
		this.mContext = context;
		// open the database
		try {
			open();
		}
		catch(SQLException e) {
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

	public Project createProject(String name, String description, String tech, double cost, long companyId) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_PROJECT_NAME, name);
		values.put(DBHelper.COLUMN_PROJECT_DESCRIPTION, description);
		values.put(DBHelper.COLUMN_PROJECT_TECH, tech);
		values.put(DBHelper.COLUMN_PROJECT_COST, cost);
		values.put(DBHelper.COLUMN_PROJECT_COMPANY_ID, companyId);
		long insertId = mDatabase.insert(DBHelper.TABLE_PROJECTS, null, values);
		Cursor cursor = mDatabase.query(DBHelper.TABLE_PROJECTS,
				mAllColumns, DBHelper.COLUMN_PROJECT_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Project newProject = cursorToProject(cursor);
		cursor.close();
		return newProject;
	}

	public void deleteProject(Project project) {
		long id = project.getId();
		System.out.println("the deleted project has the id: " + id);
		mDatabase.delete(DBHelper.TABLE_PROJECTS, DBHelper.COLUMN_PROJECT_ID + " = " + id, null);
	}

	public List<Project> getAllProjects() {
		List<Project> listProjects = new ArrayList<Project>();

		Cursor cursor = mDatabase.query(DBHelper.TABLE_PROJECTS,
				mAllColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Project project = cursorToProject(cursor);
			listProjects.add(project);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return listProjects;
	}
	
	public List<Project> getProjectsOfCompany(long companyId) {
		List<Project> listProjects = new ArrayList<Project>();

		Cursor cursor = mDatabase.query(DBHelper.TABLE_PROJECTS, mAllColumns
				, DBHelper.COLUMN_PROJECT_COMPANY_ID + " = ?",
	            new String[] { String.valueOf(companyId) }, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Project project = cursorToProject(cursor);
			listProjects.add(project);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return listProjects;
	}

	private Project cursorToProject(Cursor cursor) {
		Project project = new Project();
		project.setId(cursor.getLong(0));
		project.setName(cursor.getString(1));
		project.setDescription(cursor.getString(2));
		project.setTech(cursor.getString(3));
		project.setCost(cursor.getDouble(4));
		
		// get The company by id
		long companyId = cursor.getLong(5);
		CompanyDAO dao = new CompanyDAO(mContext);
		Company company = dao.getCompanyById(companyId);
		if(company != null)
			project.setCompany(company);
		
		return project;
	}

}
