package Pro2;

import java.text.SimpleDateFormat;
import java.util.*;

class InvalidAssetsException extends Exception {
    public InvalidAssetsException(String message) {
        super(message);
    }
}

class InvalidExperienceException extends Exception {
    public InvalidExperienceException(String message) {
        super(message);
    }
}

class Asset {
    private String assetId;
    private String assetName;
    private String assetExpiry;

    public Asset(String assetId, String assetName, String assetExpiry) throws InvalidAssetsException {
        setAssetId(assetId);
        this.assetName = assetName;
        this.assetExpiry = assetExpiry;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) throws InvalidAssetsException {
        if (!assetId.matches("^(DSK|LTP|IPH)-\\d{6,7}[HL]$")) {
            throw new InvalidAssetsException("Invalid Asset ID format: " + assetId);
        }
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAssetExpiry() {
        return assetExpiry;
    }

    @Override
    public String toString() {
        return "Asset [Asset ID: " + assetId + ", Name: " + assetName + ", Expiry Date: " + assetExpiry + "]";
    }
}

abstract class Employee {
    private static int counter = 1000;
    private String employeeId;
    private String employeeName;
    private double basicPay;
    private float salary;

    public Employee(String employeeName, double basicPay) {
        this.employeeId = "E" + ++counter;
        this.employeeName = employeeName;
        this.basicPay = basicPay;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public double getBasicPay() {
        return basicPay;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public abstract void calculateSalary(float salaryFactor);
}

class PermanentEmployee extends Employee {
    private String[] salaryComponents;
    private Asset[] assets;

    public PermanentEmployee(String employeeName, double basicPay, String[] salaryComponents, Asset[] assets)
            throws InvalidAssetsException {
        super(employeeName, basicPay);
        setSalaryComponents(salaryComponents);
        setAssets(assets);
    }

    public void setSalaryComponents(String[] salaryComponents) {
        this.salaryComponents = salaryComponents;
    }

    public String[] getSalaryComponents() {
        return salaryComponents;
    }

    public void setAssets(Asset[] assets) throws InvalidAssetsException {
        if (assets != null && assets.length > 2) {
            throw new InvalidAssetsException("A permanent employee cannot be allocated more than 2 assets.");
        }
        this.assets = assets;
    }

    public Asset[] getAssets() {
        return assets;
    }

    @Override
    public void calculateSalary(float salaryFactor) {
        double grossSalary = getBasicPay();
        for (String component : salaryComponents) {
            String[] parts = component.split("-");
            double percentage = Double.parseDouble(parts[1]);
            grossSalary += (getBasicPay() * percentage / 100);
        }
        setSalary((float)(grossSalary * salaryFactor));
    }

    public int getExperience() throws InvalidExperienceException {
        try {
            String lastTwoDigits = getEmployeeId().substring(getEmployeeId().length() - 2);
            return Integer.parseInt(lastTwoDigits);
        } catch (Exception e) {
            throw new InvalidExperienceException("Cannot determine experience from employee ID.");
        }
    }

    @Override
    public String toString() {
        return "PermanentEmployee [Employee ID: " + getEmployeeId() + ", Name: " + getEmployeeName() +
               ", Basic Pay: " + getBasicPay() + "]";
    }
}

class ContractEmployee extends Employee {
    private float wagePerHour;

    public ContractEmployee(String employeeName, float wagePerHour) {
        super(employeeName, 0);
        this.wagePerHour = wagePerHour;
    }

    public float getWagePerHour() {
        return wagePerHour;
    }

    @Override
    public void calculateSalary(float salaryFactor) {
        setSalary(wagePerHour * salaryFactor);
    }

    @Override
    public String toString() {
        return "ContractEmployee [Employee ID: " + getEmployeeId() + ", Name: " + getEmployeeName() +
               ", Wage/hour: " + wagePerHour + "]";
    }
}

class Admin {
    public void generateSalarySlip(Employee[] employees, float[] salaryFactor) {
        for (int i = 0; i < employees.length; i++) {
            if (employees[i] != null) {
                employees[i].calculateSalary(salaryFactor[i]);
            }
        }
    }

    public int generateAssetsReport(Employee[] employees, String expiryDate) {
        int count = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH);
            Date checkDate = sdf.parse(expiryDate);
            for (Employee emp : employees) {
                if (emp instanceof PermanentEmployee) {
                    PermanentEmployee pe = (PermanentEmployee) emp;
                    Asset[] assets = pe.getAssets();
                    if (assets != null) {
                        for (Asset asset : assets) {
                            Date assetDate = sdf.parse(asset.getAssetExpiry());
                            if (!assetDate.after(checkDate)) {
                                count++;
                            }
                        }
                    }
                }
            }
            return count;
        } catch (Exception e) {
            return -1;
        }
    }

    public String[] generateAssetsReport(Employee[] employees, char assetType) {
        List<String> result = new ArrayList<>();
        for (Employee emp : employees) {
            if (emp instanceof PermanentEmployee) {
                PermanentEmployee pe = (PermanentEmployee) emp;
                Asset[] assets = pe.getAssets();
                if (assets != null) {
                    for (Asset asset : assets) {
                        if (asset.getAssetId().charAt(0) == assetType) {
                            result.add(asset.getAssetId());
                        }
                    }
                }
            }
        }
        return result.toArray(new String[0]);
    }
}

public class Tester {
    public static void main(String[] args) {
        Admin admin = new Admin();

        Asset asset1 = null, asset2 = null, asset3 = null, asset4 = null, asset5 = null;
        Asset asset6 = null, asset7 = null, asset8 = null, asset9 = null, asset10 = null;

        try {
            asset1 = new Asset("DSK-876761L", "Dell-Desktop", "2020-Dec-01");
            asset2 = new Asset("DSK-876762L", "Acer-Desktop", "2021-Mar-31");
            asset3 = new Asset("DSK-876763L", "Dell-Desktop", "2022-Jun-12");
            asset4 = new Asset("LTP-987123H", "Dell-Laptop", "2021-Dec-31");
            asset5 = new Asset("LTP-987124H", "Dell-Laptop", "2021-Sep-20");
            asset6 = new Asset("LTP-987125L", "HP-Laptop", "2022-Oct-25");
            asset7 = new Asset("LTP-987126L", "HP-Laptop", "2021-Oct-02");
            asset8 = new Asset("IPH-110110H", "VoIP", "2021-Dec-12");
            asset9 = new Asset("IPH-110120H", "VoIP", "2020-Dec-31");
            asset10 = new Asset("IPH-110130H", "VoIP", "2020-Nov-30");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Employee[] employees = new Employee[7];
        float[] salaryFactor = { 3.9f, 2.3f, 4f, 8.1f, 12.5f, 189f, 211f };

        try {
            employees[0] = new PermanentEmployee("Roger Federer", 15500.0, new String[]{"DA-50", "HRA-40"}, new Asset[]{asset1, asset10});
            employees[1] = new PermanentEmployee("Serena Williams", 14000.0, new String[]{"DA-40", "HRA-40"}, new Asset[]{asset6, asset9});
            employees[2] = new PermanentEmployee("James Peter", 18500.0, new String[]{"DA-50", "HRA-45"}, new Asset[]{asset4});
            employees[3] = new PermanentEmployee("Catherine Maria", 20000.0, new String[]{"DA-50", "HRA-45"}, new Asset[]{asset2, asset5});
            employees[4] = new PermanentEmployee("Jobin Nick", 21000.0, new String[]{"DA-50", "HRA-50"}, null);
            employees[5] = new ContractEmployee("Rafael Nadal", 70);
            employees[6] = new ContractEmployee("Ricky Neol", 72.5f);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\nInitiating salary calculation...");
        admin.generateSalarySlip(employees, salaryFactor);
        for (Employee emp : employees) {
            if (emp != null) {
                System.out.println("Salary Slip: Employee ID: " + emp.getEmployeeId() + ", Salary: " + emp.getSalary());
            }
        }

        System.out.println("\nDesktop Assets:");
        for (String assetId : admin.generateAssetsReport(employees, 'D')) {
            System.out.println(assetId);
        }
    }
}

