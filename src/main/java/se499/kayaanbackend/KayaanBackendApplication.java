package se499.kayaanbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import se499.kayaanbackend.entity.Employee;
import se499.kayaanbackend.repository.EmployeeRepository;

@SpringBootApplication
public class KayaanBackendApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(KayaanBackendApplication.class, args);
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // ใช้กับทุก endpoint
                        .allowedOrigins("http://localhost:5173") // Origin ของ frontend (เช่น Vite dev server)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
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
