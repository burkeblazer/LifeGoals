package bblazer.com.lifegoals.Store;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bblazer.com.lifegoals.R;

/**
 * Created by bblazer on 1/28/2017.
 */

public class StoreAdapter extends BaseAdapter {
    private Activity activity;
    public ArrayList<Store> stores;
    private static LayoutInflater inflater;

    public StoreAdapter(Activity activity, ArrayList<Store> stores) {
        this.activity = activity;
        this.stores   = stores;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return stores.size();
    }

    @Override
    public Store getItem(int position) {
        return stores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView storeName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        Store store = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.store_row, null);

            holder = new ViewHolder();
            holder.storeName = (TextView) vi.findViewById(R.id.store_name);

            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder)vi.getTag();
        }

        holder.storeName.setText(store.getName());

        return vi;
    }
}
