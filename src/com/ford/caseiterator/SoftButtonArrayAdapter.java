package com.ford.caseiterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.ford.syncV4.proxy.rpc.SoftButton;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

public class SoftButtonArrayAdapter implements ListAdapter {
	private ArrayList<SoftButton> mSoftButtonList = null;
	private Set<Integer> mSelectedList = null;
	private Context mContext = null;
	private LayoutInflater mLI = null;

	public SoftButtonArrayAdapter(Context context) {
		mSoftButtonList = new ArrayList<SoftButton>();
		mSelectedList = new HashSet<Integer>();
		mContext = context;
		mLI = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void clearSelection() {
		mSelectedList.clear();
	}
	
	public void setSoftButtonArray(ArrayList<SoftButton> arrayList){
		mSoftButtonList.addAll(arrayList);
	}
	public ArrayList<SoftButton> getSoftButtonList(){
		return mSoftButtonList;
	}
	public ArrayList<SoftButton> getSelectedSoftButton() {
		ArrayList<SoftButton> selected = new ArrayList<SoftButton>();

		Object[] selectess = mSelectedList.toArray();
		for (int i = 0; i < selectess.length; i++) {
			selected.add(mSoftButtonList.get((Integer)selectess[i]));
		}
		return selected;
	}

	public void addSoftButton(SoftButton object) {
		mSoftButtonList.add(object);
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mSoftButtonList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mSoftButtonList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		return 1;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = mLI.inflate(R.layout.softbuttonitem, null);
		TextView textview = (TextView) view.findViewById(R.id.Name);
		textview.setText("SoftButton_"
				+ mSoftButtonList.get(position).getText());
		CheckBox checkbox = (CheckBox) view.findViewById(R.id.Select);
		checkbox.setChecked(mSelectedList.contains(position));
		
		return view;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}

}
