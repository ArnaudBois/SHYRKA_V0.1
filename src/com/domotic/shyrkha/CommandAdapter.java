/**
 * 
 */
package com.domotic.shyrkha;

import java.util.List;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Arnaud
 *
 */
public class CommandAdapter extends BaseAdapter {

	private List<Command> LstCMD;
	private LayoutInflater inflater;
	
	public CommandAdapter( Context CONTEXT,List<Command> COMMANDS ) {
		this.inflater = LayoutInflater.from(CONTEXT);
		this.LstCMD = COMMANDS;
	}

	@Override public int getCount() {	return LstCMD.size(); }

	@Override public Object getItem(int arg0) { return LstCMD.get(arg0); }

	@Override public long getItemId(int position) { return position; }
	
	private class ViewHolder {
		TextView LvDate;
		TextView LvCmd;
		TextView LvValid;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
ViewHolder holder;
		
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.itemcmd, null);
			holder.LvDate = (TextView)convertView.findViewById(R.id.LvDate);
			holder.LvCmd = (TextView)convertView.findViewById(R.id.LvCmd);
			holder.LvValid = (TextView)convertView.findViewById(R.id.LvValid);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.LvDate.setText(LstCMD.get(position).getDate());
		holder.LvCmd.setText(LstCMD.get(position).getCommand());
		holder.LvValid.setText(LstCMD.get(position).getValid());
		return convertView;
	}

}
