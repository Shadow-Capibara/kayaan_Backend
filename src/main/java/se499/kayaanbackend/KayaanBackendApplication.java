package se499.kayaanbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import se499.kayaanbackend.entity.Employee;
import se499.kayaanbackend.repository.EmployeeRepository;

@SpringBootApplication
public class KayaanBackendApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(KayaanBackendApplication.class, args);
    }

    @Autowired
    private EmployeeRepository employeeRepository;


    @Override
    public void run(String... args) throws Exception {

        Employee employee1 = Employee.builder()
                .firstName("Hola")
                .lastName("Mola")
                .email("holamola@gmail.com")
                .build();

        Employee employee2 = Employee.builder()
                .firstName("Tony")
                .lastName("Stark")
                .email("Tony@gmail.com")
                .build();

        Employee employee3 = Employee.builder()
                .firstName("sushi")
                .lastName("Ro")
                .email("sushiRo@gmail.com")
                .build();

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);
    }
}
