package com.sqlite.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sqlite.R;
import com.sqlite.adapter.ListProjectsAdapter;
import com.sqlite.dao.ProjectDAO;
import com.sqlite.model.Project;

public class ListProjectsActivity extends Activity implements OnItemLongClickListener, OnItemClickListener, OnClickListener {

	public static final String TAG = "ListProjectsActivity";
	public static final int REQUEST_CODE_ADD_PROJECT = 40;
	public static final String EXTRA_ADDED_PROJECT = "extra_key_added_project";
	public static final String EXTRA_SELECTED_COMPANY_ID = "extra_key_selected_company_id";

	private ListView mListviewProjects;
	private TextView mTxtEmptyListProjects;
	private ImageButton mBtnAddProject;
	private ListProjectsAdapter mAdapter;
	private List<Project> mListProjects;
	private ProjectDAO mProjectDao;
	private long mCompanyId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_projects);

		// initialize views
		initViews();

		// get the company id from extras
		mProjectDao = new ProjectDAO(this);
		Intent intent  = getIntent();
		if(intent != null) {
			this.mCompanyId = intent.getLongExtra(EXTRA_SELECTED_COMPANY_ID, -1);
		}

		if(mCompanyId != -1) {
			mListProjects = mProjectDao.getProjectsOfCompany(mCompanyId);
			// fill the listView
			if(mListProjects != null && !mListProjects.isEmpty()) {
				mAdapter = new ListProjectsAdapter(this, mListProjects);
				mListviewProjects.setAdapter(mAdapter);
			}
			else {
				mTxtEmptyListProjects.setVisibility(View.VISIBLE);
				mListviewProjects.setVisibility(View.GONE);
			}
		}
	}

	private void initViews() {
		this.mListviewProjects = (ListView) findViewById(R.id.list_projects);
		this.mTxtEmptyListProjects = (TextView) findViewById(R.id.txt_empty_list_projects);
		this.mBtnAddProject = (ImageButton) findViewById(R.id.btn_add_project);
		this.mListviewProjects.setOnItemClickListener(this);
		this.mListviewProjects.setOnItemLongClickListener(this);
		this.mBtnAddProject.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add_project:
			Intent intent = new Intent(this, AddProjectActivity.class);
			startActivityForResult(intent, REQUEST_CODE_ADD_PROJECT);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_ADD_PROJECT) {
			if(resultCode == RESULT_OK) {
				//refresh the listView
				if(mListProjects == null || !mListProjects.isEmpty()) {
					mListProjects = new ArrayList<Project>();
				}

				if(mProjectDao == null)
					mProjectDao = new ProjectDAO(this);
				mListProjects = mProjectDao.getProjectsOfCompany(mCompanyId);

				if(mListProjects != null && !mListProjects.isEmpty() && 
						mListviewProjects.getVisibility() != View.VISIBLE) {
					mTxtEmptyListProjects.setVisibility(View.GONE);
					mListviewProjects.setVisibility(View.VISIBLE);
				}

				if(mAdapter == null) {
					mAdapter = new ListProjectsAdapter(this, mListProjects);
					mListviewProjects.setAdapter(mAdapter);
				}
				else {
					mAdapter.setItems(mListProjects);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
		else 
			super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mProjectDao.close();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Project clickedProject = mAdapter.getItem(position);
		Log.d(TAG, "clickedItem : "+clickedProject.getName()+" : "+clickedProject.getDescription());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Project clickedProject = mAdapter.getItem(position);
		Log.d(TAG, "longClickedItem : "+clickedProject.getName()+" "+clickedProject.getDescription());

		showDeleteDialogConfirmation(clickedProject);
		return true;
	}

	private void showDeleteDialogConfirmation(final Project project) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setTitle("Delete");
		alertDialogBuilder
		.setMessage("Are you sure you want to delete the Project \""
				+ project.getName() +  "\"");

		// set positive button YES message
		alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// delete the project and refresh the list
				if(mProjectDao != null) {
					mProjectDao.deleteProject(project);

					//refresh the listView
					mListProjects.remove(project);
					if(mListProjects.isEmpty()) {
						mListviewProjects.setVisibility(View.GONE);
						mTxtEmptyListProjects.setVisibility(View.VISIBLE);
					}

					mAdapter.setItems(mListProjects);
					mAdapter.notifyDataSetChanged();
				}

				dialog.dismiss();
				Toast.makeText(ListProjectsActivity.this, R.string.project_deleted_successfully, Toast.LENGTH_SHORT).show();

			}
		});

		// set neutral button OK
		alertDialogBuilder.setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Dismiss the dialog
				dialog.dismiss();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		// show alert
		alertDialog.show();
	}
}
