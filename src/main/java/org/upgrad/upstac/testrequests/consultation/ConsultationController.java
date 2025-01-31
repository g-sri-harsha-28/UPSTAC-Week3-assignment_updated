package org.upgrad.upstac.testrequests.consultation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;


@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    Logger log = LoggerFactory.getLogger(ConsultationController.class);


    @Autowired
    private TestRequestUpdateService testRequestUpdateService;

    @Autowired
    private TestRequestQueryService testRequestQueryService;


    @Autowired
    TestRequestFlowService testRequestFlowService;

    @Autowired
    private UserLoggedInService userLoggedInService;


    @GetMapping("/in-queue")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForConsultations() {

        /* Explanation of the implemented method:
         * Used the findyBy() method of testRequestQueryService class to return list of the test requests having status
         * as 'LAB_TEST_COMPLETED'*/

        return testRequestQueryService.findBy(RequestStatus.LAB_TEST_COMPLETED);

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForDoctor() {

        /* Explanation of the implemented method:
         * Created an object of User class(named doctor) to store the current logged in user
         * Used getLoggedInUser() method of userLoggedInService to store current logged in user into User class instance
         * Used the findByDoctor() method from testRequestQueryService class to return the list of requests assigned
         * to current logged in user (doctor) */
        User doctor = userLoggedInService.getLoggedInUser();
        return testRequestQueryService.findByDoctor(doctor);

    }


    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/assign/{id}")
    public TestRequest assignForConsultation(@PathVariable Long id) {

        /* Explanation of the implemented method:
         * Created an object of User class(named doctor) to store the current logged in user
         * Used getLoggedInUser() method of userLoggedInService to store current logged in user into User class instance
         * Used the assignForConsultation() method from testRequestUpdateService class with a particular id and the
         * logged in user instance (doctor) as parameters to assign a particular request for the doctor.*/

        try {

            User doctor = userLoggedInService.getLoggedInUser();
            return testRequestUpdateService.assignForConsultation(id, doctor);

        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/update/{id}")
    public TestRequest updateConsultation(@PathVariable Long id, @RequestBody CreateConsultationRequest testResult) {

        /* Explanation of the implemented method:
         * Created an object of User class(named doctor) to store the current logged in user
         * Used getLoggedInUser() method of userLoggedInService to store current logged in user into User class instance
         * Used the updateConsultation() method from testRequestUpdateService class with particular id, logged in
         * user instance (doctor) and testResult class object as parameters to update the result of current test request
         * with doctor comments */

        try {

            User doctor = userLoggedInService.getLoggedInUser();
            return testRequestUpdateService.updateConsultation(id, testResult, doctor);

        } catch (ConstraintViolationException e) {
            throw asConstraintViolation(e);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


}
