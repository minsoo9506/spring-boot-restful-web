package rest.restfulwebservices.versioning;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersioningPersonController {

    @GetMapping("/v1/person")
    public PersonV1 getFirstVersionOfPerson () {
        return new PersonV1("Kim MinJae");
    }

    @GetMapping("/v2/person")
    public PersonV2 getSecondVersionOfPerson () {
        return new PersonV2(new Name("Kim", "MinJae"));
    }

    @GetMapping(path = "/person", params = "version=1")
    public PersonV1 getFirstVersionOfPersonRequestParameter () {
        return new PersonV1("Kim MinJae");
    }

    @GetMapping(path = "/person/header", produces = "application/vnd.company.app-v1+json")
    public PersonV1 getFirstVersionOfPersonAcceptHeader () {
        return new PersonV1("Kim MinJae");
    }
}
