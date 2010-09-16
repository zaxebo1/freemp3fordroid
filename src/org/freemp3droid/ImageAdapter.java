package org.freemp3droid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class ImageAdapter extends ArrayAdapter<Integer> {

	private Integer[] items;
	Context ctx;

	public ImageAdapter(Context context, int textViewResourceId, Integer[] items) {
		super(context, textViewResourceId, items);
		this.ctx = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.image_list_item, null);
		}

		Integer it = items[position];
		if (it != null) {
			ImageView iv = (ImageView) v.findViewById(R.id.image_list_image);
			if (iv != null) {
				iv.setImageDrawable(ctx.getResources().getDrawable(it));
			}
		}
		return v;
	}
}