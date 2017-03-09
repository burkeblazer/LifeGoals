package bblazer.com.lifegoals.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import bblazer.com.lifegoals.R;
import bblazer.com.lifegoals.Store.Department;
import bblazer.com.lifegoals.Store.DragAndDropAdapter;
import bblazer.com.lifegoals.Store.Store;

public class AddNewStoreActivity extends AppCompatActivity {
    private Spinner departmentSpinner;
    private ImageButton addDeptButton;
    private EditText storeName;
    private DragListView dragListView;
    private RelativeLayout emptyView;
    private Button addAllDeptButton;

    public static EditStoresActivity activity;
    public static Store store;
    private boolean isEdit = false;
    private String previousName = "";
    private ArrayList<Pair<Integer, String>> mItemArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_store);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Create or set a store object (this is how we know if we are editting or adding)
        if (store == null) {
            store = new Store("");
            getSupportActionBar().setTitle("Add Store");
        }
        else {
            isEdit       = true;
            store        = Store.clone(store);
            previousName = store.getName();
            getSupportActionBar().setTitle("Edit Store");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set UI variables
        departmentSpinner = (Spinner) findViewById(R.id.dept_spinner);
        addDeptButton     = (ImageButton) findViewById(R.id.add_dept_button);
        storeName         = (EditText) findViewById(R.id.store_name);
        dragListView      = (DragListView) findViewById(R.id.drag_list_view);
        emptyView         = (RelativeLayout)findViewById(R.id.empty_view);
        addAllDeptButton  = (Button) findViewById(R.id.add_all_dept_button);

        // Create string adapter for dept dropdown
        ArrayList<String> deptArray  = Department.getDefaultDepartments();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, deptArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(adapter);

        // Add a listener for the add dept button
        addDeptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDept();
            }
        });

        // Add a listener for the store name textfield on change
        storeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                store.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set up the drag and drop from library
//        dragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        dragListView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {

            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                updateSortOrders();
            }
        });

        this.setupListRecyclerView();

        addAllDeptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (String selectedDepartment:
                        Department.getDefaultDepartments() ) {
                    Department deptObj = new Department(selectedDepartment);

                    // Make sure this dept doesn't already exist
                    boolean bExists = false;
                    for (Department dept :
                            store.getDepartments()) {
                        if (dept.getName().equals(deptObj.getName())) {
                            bExists = true;
                        }
                    }

                    // Display error
                    if (bExists) {
                        continue;
                    }

                    // Add the department to the store
                    store.addDepartment(deptObj);

                    // Add the department to the listview
                    int size = store.getDepartments().size();
                    mItemArray.add(new Pair<>(new Integer(size + 1), selectedDepartment));
                    dragListView.getAdapter().notifyDataSetChanged();
                }
                checkEmpty();
            }
        });

        // If we are editting an existing store, load it's data into the form
        if (isEdit) {
            loadStoreData();
        }

        checkEmpty();
    }

    private void checkEmpty() {
        if (store.getDepartments().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void loadStoreData() {
        // Set the store's name
        storeName.setText(store.getName());

        // Load it's sorted departments
        for (Department storeDept :
                store.getDepartments()) {
            mItemArray.add(new Pair<>(new Integer(storeDept.getSortNumber()), storeDept.getName()));
        }

        // Sort the arraylist by pair
        Collections.sort(mItemArray, new Comparator<Pair>(){
            public int compare(Pair o1, Pair o2){
                Integer o1Int = (Integer)o1.first;
                Integer o2Int = (Integer)o2.first;
                if(o1Int.intValue() == o2Int.intValue())
                    return 0;
                return o1Int.intValue() < o2Int.intValue() ? -1 : 1;
            }
        });

        dragListView.getAdapter().notifyDataSetChanged();

        checkEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_store_menu, menu);
        MenuItem update = menu.findItem(R.id.update_store);
        MenuItem save = menu.findItem(R.id.save_store);
        if (isEdit) {
            save.setVisible(false);
        }
        else {
            update.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.update_store:
            case R.id.save_store:
                // Make sure to validate the new store to make sure they've at least entered a name...
                if (store.getName() == null || store.getName().equals("")) {
                    Toast.makeText(AddNewStoreActivity.this, "Please make sure to at least enter a name before trying to save.", Toast.LENGTH_LONG).show();return true;}

                if (isEdit) {
                    activity.updateStore(store, previousName);
                }
                else {
                    activity.saveNewStore(store);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSortOrders() {
        // Need a function that will sync the current sort orders of the listview to the arraylist on the store
        int newSort = 0;
        for (Pair<Integer, String> pair :
                mItemArray) {
            int sortNumber  = pair.first;
            String deptName = pair.second;

            // Loop through each of the departments on the store and set their sortnumber
            for (Department dept :
                    store.getDepartments()) {
                if (dept.getName().equals(deptName)) {
                    dept.setSortNumber(newSort);
                    newSort++;
                }
            }
        }
    }

    private void setupListRecyclerView() {
        dragListView.setLayoutManager(new LinearLayoutManager(this));
        DragAndDropAdapter listAdapter = new DragAndDropAdapter(mItemArray, R.layout.list_item, R.id.image, false, this);
        dragListView.setAdapter(listAdapter, true);
        dragListView.setCanDragHorizontally(false);
        dragListView.setCustomDragItem(new MyDragItem(this, R.layout.list_item));
    }

    public void removeDepartment(String departmentName) {
        // Remove from local departments on store
        ArrayList<Department> localDepartments = store.getDepartments();
        for (int ct = 0; ct < localDepartments.size(); ct++) {
            if (localDepartments.get(ct).getName().equals(departmentName)) {localDepartments.remove(ct);}
        }
        updateSortOrders();

        // Remove from array adapter's list
        int ct = 0;
        for (Pair<Integer, String> department :
                mItemArray) {
            if (department.second.equals(departmentName)) {mItemArray.remove(ct);break;}
            ct++;
        }

        dragListView.getAdapter().notifyDataSetChanged();
        checkEmpty();
    }

    private void addDept() {
        // Create a new department object from the string selected in the spinner
        String selectedDepartment = departmentSpinner.getSelectedItem().toString();
        Department deptObj        = new Department(selectedDepartment);

        // Make sure this dept doesn't already exist
        boolean bExists = false;
        for (Department dept :
                store.getDepartments()) {
            if (dept.getName().equals(deptObj.getName())) {bExists = true;}
        }

        // Display error
        if (bExists) {
            Toast.makeText(this, "Department already exists, please modify the currently added one.", Toast.LENGTH_LONG).show();return;}

        // Add the department to the store
        store.addDepartment(deptObj);

        // Add the department to the listview
        int size = store.getDepartments().size();
        mItemArray.add(new Pair<>(new Integer(size+1), selectedDepartment));
        dragListView.getAdapter().notifyDataSetChanged();
        checkEmpty();
    }

    private static class MyDragItem extends DragItem {

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            dragView.setBackgroundColor(dragView.getResources().getColor(R.color.colorPrimary));
        }
    }
}
