package com.project.schoolmanagment;
import com.project.schoolmanagment.entity.concretes.user.UserRole;
import com.project.schoolmanagment.entity.enums.Gender;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.payload.request.user.UserRequest;
import com.project.schoolmanagment.repository.user.UserRoleRepository;
import com.project.schoolmanagment.service.user.UserRoleService;
import com.project.schoolmanagment.service.user.UserService;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchoolManagementApplication implements CommandLineRunner {
    
    private final UserRoleService userRoleService;
    private final UserRoleRepository userRoleRepository;
    private final UserService userService;

    public SchoolManagementApplication(UserRoleService userRoleService,
        UserRoleRepository userRoleRepository, UserService userService) {
        this.userRoleService = userRoleService;
        this.userRoleRepository = userRoleRepository;
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SchoolManagementApplication.class, args);
    }

    @Override
    //for tables like userrole (or any tables representing static data that doesn't change often), it's essential to have
    // predefined entries available in the table before interacting with them. This is because these tables typically represent
    // reference data that the application relies on for its functionality.
    public void run(String... args) throws Exception {
        if(userRoleService.getAllUserRole().isEmpty()){
            //admin
            UserRole admin = new UserRole();
            admin.setRoleType(RoleType.ADMIN);
            admin.setRoleName("Admin");
            userRoleRepository.save(admin);
            //dean
            UserRole dean = new UserRole();
            dean.setRoleType(RoleType.MANAGER);
            dean.setRoleName("Dean");
            userRoleRepository.save(dean);
            //vice-dean
            UserRole viceDean = new UserRole();
            viceDean.setRoleType(RoleType.ASSISTANT_MANAGER);
            viceDean.setRoleName("ViceDean");
            userRoleRepository.save(viceDean);
            //student
            UserRole student = new UserRole();
            student.setRoleType(RoleType.STUDENT);
            student.setRoleName("Student");
            userRoleRepository.save(student);
            //teacher
            UserRole teacher = new UserRole();
            teacher.setRoleType(RoleType.TEACHER);
            teacher.setRoleName("Teacher");
            userRoleRepository.save(teacher);
        }
        //create a super admin for every database clean up
        if (userService.getAllUsers().isEmpty()){
            UserRequest userRequest = getUserRequest();
            userService.saveUser(userRequest,"Admin");
        }
        
    }

    private static UserRequest getUserRequest() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("UserAdmin");
        userRequest.setEmail("admin@gmail.com");
        userRequest.setSsn("111-11-1111");
        userRequest.setPassword("Ankara06*");
        userRequest.setName("adminName");
        userRequest.setSurname("adminSurname");
        userRequest.setPhoneNumber("111-111-1111");
        userRequest.setGender(Gender.FEMALE);
        userRequest.setBirthDay(LocalDate.of(1980,2,2));
        userRequest.setBirthPlace("Istanbul");
        return userRequest;
    }
}









