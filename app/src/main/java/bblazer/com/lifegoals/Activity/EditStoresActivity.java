package bblazer.com.lifegoals.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import bblazer.com.lifegoals.R;
import bblazer.com.lifegoals.Store.Store;
import bblazer.com.lifegoals.Store.StoreAdapter;

public class EditStoresActivity extends AppCompatActivity {
    private RelativeLayout emptyView;
    public ArrayList<Store> stores;
    private ListView listView;
    public StoreAdapter storeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stores);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Stores");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewStore();
            }
        });

        stores = Store.getStores(this);

        emptyView = (RelativeLayout)findViewById(R.id.empty_view);
        listView = (ListView)findViewById(R.id.list_view);
        storeAdapter = new StoreAdapter(this, stores);
        listView.setAdapter(storeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Store selStore = storeAdapter.getItem(position);
                editStore(selStore);
            }
        });

        registerForContextMenu(listView);

        checkEmpty();
    }

    private void checkEmpty() {
        if (stores.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void editStore(Store store) {
        Intent intent = new Intent(this, AddNewStoreActivity.class);
        AddNewStoreActivity.store    = store;
        AddNewStoreActivity.activity = this;
        startActivity(intent);
    }

    private void addNewStore() {
        Intent intent = new Intent(this, AddNewStoreActivity.class);
        AddNewStoreActivity.store    = null;
        AddNewStoreActivity.activity = this;
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.list_view) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.listview_menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Store store = stores.get(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        switch(item.getItemId()) {
            case R.id.edit:
                editStore(store);
                return true;
            case R.id.delete:
//                Store.deleteEvent(store, this);
                Store.removeStore(this, store.getName());
                storeAdapter.stores.remove(store);
                storeAdapter.notifyDataSetChanged();

                checkEmpty();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void updateStore(Store store, String previousName) {
        // Find the previous store and update it
        for (Store previousStore :
                stores) {
            if (previousName.equals(previousStore.getName())) {
                previousStore.updateFrom(store);
            }
        }

//        Store.updateEvent(store, this);
        Store.removeStore(this, previousName);
        Store.addStore(this, store);
    }

    public void saveNewStore(Store store) {
        // Make sure they aren't trying to add a store that already exists
        boolean bExists = false;
        for (Store currentStore :
                stores) {
            if (currentStore.getName().equals(store.getName())) {bExists = true;}
        }

        if (bExists) {
            Toast.makeText(this, "Found a duplicate store, please make sure to add stores only once", Toast.LENGTH_LONG).show();return;}

//        Store.addEvent(store, this);
        Store.addStore(this, store);
        storeAdapter.stores.add(store);
        storeAdapter.notifyDataSetChanged();

        checkEmpty();
    }
}
