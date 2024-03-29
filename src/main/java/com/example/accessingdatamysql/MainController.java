package com.example.accessingdatamysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller //RestController
@RequestMapping(path="/demo")
@CrossOrigin("*")
public class MainController {

    public final String SUCCESS_SAVE = "Saved!";
    public final String USER_DOES_NOT_EXIST = "The User does not exist";
    public final String TEST_DOES_NOT_EXIST = "The Test does not exist";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRepository testRepository;

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping(path="/addNewUser")
    public @ResponseBody Map.Entry<String, Integer> addNewUser (
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {
        var users = userRepository.findAll();
        for (var user : users) {
            if (user.getEmail().equals(email)) {
                return user.getPerson();
            }
        }
        User newUser = new User();
        newUser.rename(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.createListTests();
        userRepository.save(newUser);
        return newUser.getPerson();
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping(path="/addNewTest")
    public @ResponseBody String addNewTest(
            @RequestParam int userId,
            @RequestParam String testTitle,
            @RequestParam String testContent,
            @RequestParam boolean isAllowedNewAttempt) {
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            return USER_DOES_NOT_EXIST;
        //Test newTest = new Test(user.get().getId(), testTitle, testContent, isAllowedNewAttempt);
        Test newTest = new Test();
        newTest.setCreatorId(userId);
        newTest.updateTitle(testTitle);
        newTest.updateContent(testContent);
        newTest.updateAllowedNewAttempt(isAllowedNewAttempt);
        newTest.createMapPassedUsers();
        testRepository.save(newTest);
        user.get().addNewTest(newTest.getId());
        userRepository.save(user.get());
        return SUCCESS_SAVE;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(path="/signInAccount")
    public @ResponseBody Map.Entry<String, Integer> signInAccount (
            @RequestParam String email,
            @RequestParam String password) {
        var users = userRepository.findAll();
        for (var user : users) {
            if (user.successInput(email, password)) {
                return user.getPerson();
            }
        }
        return null;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/getUserTestsById")
    public @ResponseBody Iterable<Map.Entry<String, Integer>> getUserTests(@RequestParam int userId) {
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            return null;
        var testsId = user.get().getListTests();
        List<Map.Entry<String, Integer>> testList = new ArrayList<>(testsId.size());
        for (var id : testsId) {
            testList.add(testRepository.findById(id).get().getInfo());
        }
        return testList;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/getTestById")
    public @ResponseBody Test getTestById(@RequestParam int testId) {
        return testRepository.findById(testId).orElse(null);
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PutMapping(path="/updateTestById")
    public @ResponseBody String updateTestUser (
            @RequestParam int userId,
            @RequestParam int testId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam boolean isAllowedNewAttempt) {
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            return USER_DOES_NOT_EXIST;
        var test = testRepository.findById(user.get().getTestById(testId));
        if (test.isEmpty())
            return TEST_DOES_NOT_EXIST;
        test.get().updateTitle(title);
        test.get().updateContent(content);
        test.get().updateAllowedNewAttempt(isAllowedNewAttempt);
        testRepository.save(test.get());
        return SUCCESS_SAVE;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PutMapping(path="/updateSecondAttemptTestById")
    public @ResponseBody String updateTestUser (
            @RequestParam int userId,
            @RequestParam int testId,
            @RequestParam boolean isAllowedNewAttempt) {
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            return USER_DOES_NOT_EXIST;
        var test = testRepository.findById(user.get().getTestById(testId));
        if (test.isEmpty())
            return TEST_DOES_NOT_EXIST;
        test.get().updateAllowedNewAttempt(isAllowedNewAttempt);
        testRepository.save(test.get());
        return SUCCESS_SAVE;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PutMapping(path="/passTestByUser")
    public @ResponseBody String updateTestUser (
            @RequestParam int userId,
            @RequestParam int testId,
            @RequestParam int result) {
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            return USER_DOES_NOT_EXIST;
        var test = testRepository.findById(testId);
        if (test.isEmpty())
            return TEST_DOES_NOT_EXIST;
        test.get().savePass(user.get().getId(), result);
        testRepository.save(test.get());
        return SUCCESS_SAVE;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/getStatisticsTestId")
    public @ResponseBody Map<Map.Entry<String, Integer>, Integer> getTestStatistics(@RequestParam int testId) {
        var test = testRepository.findById(testId);
        if (test.isEmpty())
            return null;
        var stats = test.get().getAllStatistics();
        Map<Map.Entry<String, Integer>, Integer> statistics = new HashMap<>();
        for (var userId : stats.keySet()) {
            statistics.put(new AbstractMap.SimpleEntry<>(userRepository.findById(userId).get().getPerson()), stats.get(userId));
        }
        return statistics;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/getShortStatisticsTestId")
    public @ResponseBody ShortStatistic getTestShortStatistics(@RequestParam int testId) {
        var test = testRepository.findById(testId);
        return test.get().getShortStatistics();
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @DeleteMapping("/deleteTestById")
    public @ResponseBody String deleteTest(
            @RequestParam int userId,
            @RequestParam int testId) {
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            return USER_DOES_NOT_EXIST;
        var test = testRepository.findById(testId);
        if (test.isEmpty())
            return TEST_DOES_NOT_EXIST;
        user.get().removeTest(testId);
        testRepository.deleteById(testId);
        return SUCCESS_SAVE;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @DeleteMapping("/clearDB")
    public @ResponseBody String clearDB() {
        userRepository.deleteAll();
        testRepository.deleteAll();
        return SUCCESS_SAVE;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(path="/getAllUsers")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(path="/getAllTests")
    public @ResponseBody Iterable<Test> getAllTests() {
        return testRepository.findAll();
    }
}
