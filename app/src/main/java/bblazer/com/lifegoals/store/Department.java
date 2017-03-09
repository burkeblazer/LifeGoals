package bblazer.com.lifegoals.Store;

import java.util.ArrayList;

/**
 * Created by bblazer on 1/25/2017.
 */

public class Department {
    private String name;
    private int sortNumber;

    public Department(String name) {
        this.name = name;
    }

    public static ArrayList<String> getDefaultDepartments() {
        ArrayList<String> defaultDepartments = new ArrayList<>();
        defaultDepartments.add("Produce");
        defaultDepartments.add("Meat");
        defaultDepartments.add("Dairy");
        defaultDepartments.add("Frozen");
        defaultDepartments.add("Deli");
        defaultDepartments.add("Aisles");
        return defaultDepartments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
    }

    public static Department clone(Department department) {
        Department newDepartment = new Department(department.getName());
        newDepartment.sortNumber = department.sortNumber;

        return newDepartment;
    }
}
