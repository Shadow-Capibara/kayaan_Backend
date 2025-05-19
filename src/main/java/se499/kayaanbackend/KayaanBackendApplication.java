package se499.kayaanbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import se499.kayaanbackend.entity.Employee;
import se499.kayaanbackend.repository.EmployeeRepository;

@SpringBootApplication
public class KayaanBackendApplication{

    public static void main(String[] args) {
        SpringApplication.run(KayaanBackendApplication.class, args);
    }

   
}
