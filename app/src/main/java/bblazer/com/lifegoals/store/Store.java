package bblazer.com.lifegoals.Store;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import bblazer.com.lifegoals.R;

/**
 * Created by bblazer on 1/25/2017.
 */

public class Store {
    private String name;
    private ArrayList<Department> departments = new ArrayList<>();

    public Store(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(ArrayList<Department> departments) {
        this.departments = departments;
    }

    public boolean removeDepartment(Department removeDept) {
        int index = -1;
        for (int ct = 0; ct < departments.size(); ct++) {
            if (departments.get(ct).getName().equals(removeDept.getName())) {index = ct;}
        }

        if (index == -1) {return false;}

        departments.remove(index);

        return true;
    }

    public boolean addDepartment(Department addDept) {
        // Make sure it doesn't already exist
        for (int ct = 0; ct < departments.size(); ct++) {
            if (departments.get(ct).getName().equals(addDept.getName())) {return false;}
        }

        addDept.setSortNumber(departments.size());

        departments.add(addDept);

        return true;
    }

    public static ArrayList<Store> getStores(Activity context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String storesJSON        = preferences.getString(context.getApplicationContext().getResources().getString(R.string.stores_json), "");
        if (storesJSON == null || storesJSON.equals("")) {return new ArrayList<Store>();}

        Type listOfTestObject = new TypeToken<ArrayList<Store>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<Store> stores = gson.fromJson(storesJSON, listOfTestObject);

        return stores;
    }

    public static void addStore(Activity context, Store store) {
        Gson gson = new Gson();
        ArrayList<Store> stores = getStores(context);
        stores.add(store);
        Type listOfTestObject = new TypeToken<ArrayList<Store>>(){}.getType();
        String json = gson.toJson(stores, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.stores_json), json);
        editor.commit();
    }

    public static void removeStore(Activity context, String storeName) {
        Gson gson = new Gson();
        ArrayList<Store> stores = getStores(context);

        // Find the store
        int index = -1;
        for (int ct = 0; ct < stores.size(); ct++) {
            if (stores.get(ct).getName().equals(storeName)) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        stores.remove(index);
        Type listOfTestObject = new TypeToken<ArrayList<Store>>(){}.getType();
        String json = gson.toJson(stores, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.stores_json), json);
        editor.commit();
    }

    public static Store clone(Store store) {
        Store newStore       = new Store(store.getName());
        newStore.departments = Store.cloneDepartments(store);

        return newStore;
    }

    private static ArrayList<Department> cloneDepartments(Store store) {
        ArrayList<Department> departments = new ArrayList<Department>();
        for (Department dept :
                store.getDepartments()) {
            departments.add(Department.clone(dept));
        }

        return departments;
    }

    public void updateFrom(Store store) {
        this.name        = store.name;
        this.departments = store.departments;
    }

//    public static void addEvent(Store store, Context context) {
//        Event addEvent = new Event();
//        addEvent.setType("Store");
//        addEvent.setDescription("Added "+store.getName());
//        addEvent.setCreated(Calendar.getInstance());
//
//        Event.addEvent(context, addEvent);
//    }
//
//    public static void updateEvent(Store store, Context context) {
//        Event addEvent = new Event();
//        addEvent.setType("Store");
//        addEvent.setDescription("Updated "+store.getName());
//        addEvent.setCreated(Calendar.getInstance());
//
//        Event.addEvent(context, addEvent);
//    }
//
//    public static void deleteEvent(Store store, Context context) {
//        Event addEvent = new Event();
//        addEvent.setType("Store");
//        addEvent.setDescription("Deleted "+store.getName());
//        addEvent.setCreated(Calendar.getInstance());
//
//        Event.addEvent(context, addEvent);
//    }
}
