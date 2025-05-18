package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
