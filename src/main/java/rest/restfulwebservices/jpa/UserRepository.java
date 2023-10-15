package rest.restfulwebservices.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import rest.restfulwebservices.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
