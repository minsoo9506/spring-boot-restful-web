package rest.restfulwebservices.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import rest.restfulwebservices.user.Post;
import rest.restfulwebservices.user.User;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
