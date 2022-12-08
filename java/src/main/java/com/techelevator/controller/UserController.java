package com.techelevator.controller;

import com.techelevator.dao.CourseDao;
import com.techelevator.dao.UserDao;
import com.techelevator.model.Course;
import com.techelevator.model.CourseListDto;
import com.techelevator.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin

public class UserController {

    private UserDao userDao;
    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(path = "/{courseId}/users", method = RequestMethod.GET)
    public List<User> getUsersByCourseId(@PathVariable int courseId) {
        return userDao.getUsersByCourseId(courseId);}

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/course/user", method = RequestMethod.POST)
    public CourseListDto addUserToCourse(@RequestBody CourseListDto dto) {
        return userDao.addUserToCourse(dto);

    }


}
