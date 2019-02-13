package com.reimbes;

public interface EmployeeService {
    public Employee create(Employee employee);
    public Employee update(Employee employee);
    public Employee get(long id);
    public void delete();

}
