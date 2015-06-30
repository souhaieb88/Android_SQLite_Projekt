package com.sqlite.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sqlite.R;
import com.sqlite.model.Project;

public class ListProjectsAdapter extends BaseAdapter {
	
	public static final String TAG = "ListProjectsAdapter";
	
	private List<Project> mItems;
	private LayoutInflater mInflater;
	
	public ListProjectsAdapter(Context context, List<Project> listCompanies) {
		this.setItems(listCompanies);
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0 ;
	}

	@Override
	public Project getItem(int position) {
		return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null ;
	}

	@Override
	public long getItemId(int position) {
		return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getId() : position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if(v == null) {
			v = mInflater.inflate(R.layout.list_item_project, parent, false);
			holder = new ViewHolder();
			holder.txtProjectName = (TextView) v.findViewById(R.id.txt_name);
			holder.txtCompanyName = (TextView) v.findViewById(R.id.txt_company_name);
			holder.txtTech = (TextView) v.findViewById(R.id.txt_tech);
			holder.txtCost = (TextView) v.findViewById(R.id.txt_cost);
			v.setTag(holder);
		}
		else {
			holder = (ViewHolder) v.getTag();
		}
		
		// fill row data
		Project currentItem = getItem(position);
		if(currentItem != null) {
			holder.txtProjectName.setText(currentItem.getName()+" : "+currentItem.getDescription());
			holder.txtTech.setText(currentItem.getTech());
			holder.txtCost.setText(String.valueOf(currentItem.getCost())+" $");
			holder.txtCompanyName.setText(currentItem.getCompany().getName());
		}
		
		return v;
	}
	
	public List<Project> getItems() {
		return mItems;
	}

	public void setItems(List<Project> mItems) {
		this.mItems = mItems;
	}

	class ViewHolder {
		TextView txtProjectName;
		TextView txtTech;
		TextView txtCompanyName;
		TextView txtCost;
	}

}
