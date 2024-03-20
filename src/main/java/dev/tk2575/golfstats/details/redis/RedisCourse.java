package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.course.Course;

class RedisCourse {

  private String name;
  private String location;
  RedisCourse(Course course) {
    this.name = course.getName();
    this.location = course.getLocation();
  }
  
  Course toCourse() {
    return Course.of(this.name, this.location);
  }
}
