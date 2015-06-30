package com.sqlite.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sqlite.R;
import com.sqlite.adapter.SpinnerCompaniesAdapter;
import com.sqlite.dao.CompanyDAO;
import com.sqlite.dao.ProjectDAO;
import com.sqlite.model.Company;
import com.sqlite.model.Project;

public class AddProjectActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	public static final String TAG = "AddProjectActivity";

	private EditText mTxtName;
	private EditText mTxtDescription;
	private EditText mTxtTech;
	private EditText mTxtCost;
	private Spinner mSpinnerCompany;
	private Button mBtnAdd;

	private CompanyDAO mCompanyDao;
	private ProjectDAO mProjectDao;
	
	private Company mSelectedCompany;
	private SpinnerCompaniesAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_project);

		initViews();

		this.mCompanyDao = new CompanyDAO(this);
		this.mProjectDao = new ProjectDAO(this);
		
		//fill the spinner with companies
		List<Company> listCompanies = mCompanyDao.getAllCompanies();
		if(listCompanies != null) {
			mAdapter = new SpinnerCompaniesAdapter(this, listCompanies);
			mSpinnerCompany.setAdapter(mAdapter);
			mSpinnerCompany.setOnItemSelectedListener(this);
		}
	}

	private void initViews() {
		this.mTxtName = (EditText) findViewById(R.id.txt_name);
		this.mTxtDescription = (EditText) findViewById(R.id.txt_description);
		this.mTxtTech = (EditText) findViewById(R.id.txt_tech);
		this.mTxtCost = (EditText) findViewById(R.id.txt_cost);
		this.mSpinnerCompany = (Spinner) findViewById(R.id.spinner_companies);
		this.mBtnAdd = (Button) findViewById(R.id.btn_add);

		this.mBtnAdd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add:
			Editable name = mTxtName.getText();
			Editable description = mTxtDescription.getText();
			Editable tech = mTxtTech.getText();
			Editable strCost = mTxtCost.getText();
			mSelectedCompany = (Company) mSpinnerCompany.getSelectedItem();
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description)
					&& !TextUtils.isEmpty(tech) && !TextUtils.isEmpty(strCost)
				 && mSelectedCompany != null ) {
				// add the project to database
				double cost = Double.valueOf(strCost.toString());
				Project createdProject = mProjectDao.createProject(name.toString(), description.toString(), tech.toString(), cost, mSelectedCompany.getId());
				Toast.makeText(this, R.string.project_created_successfully, Toast.LENGTH_LONG).show();
				Log.d(TAG, "added project : "+ createdProject.getName()+" "+createdProject.getDescription()+", project.companyId : "+createdProject.getCompany().getId());
				setResult(RESULT_OK);
				finish();
				
			}
			else {
				Toast.makeText(this, R.string.empty_fields_message, Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCompanyDao.close();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		mSelectedCompany = mAdapter.getItem(position);
		Log.d(TAG, "selectedCompany : "+mSelectedCompany.getName());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
}
