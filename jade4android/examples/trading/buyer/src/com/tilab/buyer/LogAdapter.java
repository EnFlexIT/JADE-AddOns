package com.tilab.buyer;


import jade.util.leap.LinkedList;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LogAdapter extends BaseAdapter {
	private LinkedList stringList;
	private Activity act;
	
	public LogAdapter(Activity act) {
		stringList = new LinkedList();
		this.act=act;
		
	}

	public int getCount() {//ritorna il numero di elementi
		// TODO Auto-generated method stub
		return stringList.size();
	}

	public Object getItem(int position) {//ritorna un elemento spec di cui viene pass la posizione
		// TODO Auto-generated method stub
		return stringList.get(position);
	}

	public long getItemId(int position) {//ritorna l'id di un elemento
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {//ritorna la rappresentazione di tutti gli elementi della lista
		// TODO Auto-generated method stub

    	LinearLayout linearLayout1;
		linearLayout1 = new LinearLayout(act);
		linearLayout1.setOrientation(LinearLayout.VERTICAL);

		linearLayout1.setPreferredHeight(LayoutParams.FILL_PARENT);

		linearLayout1.setPreferredWidth(LayoutParams.FILL_PARENT);
		
		TextView label2 = new TextView(act);
		label2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		label2.setTextColor(Color.BLACK);
		label2.setTextSize(12);
		label2.setText((String)stringList.get(position));
        linearLayout1.addView(label2);
		return linearLayout1;
	}
	
	public void addElement(String str) {
		stringList.add(str);
	}
	
	public void addHaed(String str) {
		stringList.addFirst(str);
	}

}
