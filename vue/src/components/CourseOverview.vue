<template>

  <div id="courseOVtable">
    <div class="loading" v-if="isLoading">
        <img src="../../assets/loading.gif" />
    </div>
    <div class="main" v-show="!isLoading">
      <table id="course-table">
        <thead>
          <tr>
            <th>Course ID</th>
            <th>Course Name</th>
            <th>Manage Course</th>
            <!-- <th>Assigned Date</th>
                    <th>Due Date</th>
                    <th>Completion status</th> -->
          </tr>
        </thead>
        <tbody>
          <tr v-for="course in courses" v-bind:key="course.courseId">
            <td>{{ course.courseId }}</td>
            <td>
              
              <router-link v-bind:to="{name: 'AdminCourse', params: {courseId: course.courseId}}" id="routeLink"><h3>{{ course.courseName }} </h3>
                </router-link></td>
                
            <td class="manage-course">
              <button @click="enrollAllUsers(course.courseId)">Add All Users</button>
              <button @click="deleteCourse(course.courseId)" v-if="$store.state.isAdmin == true">
                Delete Course
              </button>
            </td>
            <!-- <td>{{course.assignDate}}</td>
                  <td>{{course.dueDate}}</td>
                  <td>{{calculateCompletion}}</td> -->
          </tr>
        </tbody>
      </table>
      <div id="add-course" v-if="$store.state.isAdmin == true">
        <button id="add-course-button" v-on:click="showForm = !showForm">
          Add New Course
        </button>
        <div id="form-div">
          <form
            id="frmAddNewCourse"
            v-show="showForm"
            v-on:submit.prevent="saveCourse"
          >
            <div class="field">
              <label for="courseName">Course Name:</label>
              <input
                type="text"
                name="courseName"
                v-model="newCourse.courseName"
                required
              />
            </div>
            <div class="field">
              <label for="description">Description:</label>
              <textarea
                v-model="newCourse.courseDescription"
                required
              />
            </div>
            <div class="field">
              <label for="difficulty">Difficulty:</label>
              <select name="difficulty" v-model="newCourse.difficulty" required>
                <option value="Beginner">Beginner</option>
                <option value="Intermediate">Intermediate</option>
                <option value="Advanced">Advanced</option>
              </select>
            </div>
            <div class="field">
            </div>
            <button
              type="submit"
              class="btn-save"
              value="save"
            >
              Save Course
            </button>
          </form>
        </div>
      </div>

      <!-- <admin-course-detail v-for="course in courses" v-bind:key="course.courseId" v-bind:course="course"/> -->
      </div>
    </div>
</template>

<script>
// import AdminCourseDetail from '../components/AdminCourseDetail.vue'
import CourseService from "../services/CourseService.js";
import UserService from "../services/UserService.js"
export default {
  data() {
    return {
      isLoading: false,
      courses: [],
      showForm: false,
      newCourse: {
        courseId: null,
        courseName: '',
        courseDescription: '',
        difficulty: '',
        cost: ''
      },
      
    };
  },
  // components: {
  //     AdminCourseDetail
  // },
  computed: {
    // calculateCompletion() {
    // *usersCompleted / usersAssigned
    // }
  },
  methods: {
    deleteCourse(courseId) {
      const deleteConfirmation = confirm(
        "Are you sure you want to delete this course?"
      );
      if (deleteConfirmation) {
        CourseService.deleteCourse(courseId)
          .then((response) => {
            if (response.status === 200 || response.status === 204) {
              alert("Course successfully deleted");
              this.displayList();
            }
          })
          .catch((error) => {
            console.log(error.response);
            alert(`Error ${error.status}: Could not delete course`);
          });
      }
    },
    editCourse() {},
    displayList() {
      CourseService.listCourses().then((response) => {
        if (response.status === 200) {
          this.isLoading = true;
          setTimeout(() => {
           this.isLoading = false;
        }, 1000)
          this.courses = response.data;
        } else {
          console.log("error");
        }
      });
    },
    resetForm() {
      this.newCourse = {};
      this.showForm = false;
    },
    saveCourse() {
      CourseService.addCourse(this.newCourse)
        .then((response) => {
          if (response.status === 201) {

            this.enrollAllUsers(response.data.courseId)
            this.displayList();
          }
      });
      this.resetForm();
    },
    enrollAllUsers(courseId) { 
      const confirmation = confirm('Would you like to enroll all users in this course?')
      if (confirmation) {
        let allUsers = this.$store.state.managerArray.concat(this.$store.state.userArray);
        allUsers.forEach(user => {
        let idToPass = user.id
        CourseService.addUserToCourse(idToPass, courseId)
          .then(response => {
            console.log(response.status)
          })
          .catch(error => {
            console.log(error.response.data)
          })
      });
      }
    },

  },
  created() {
    this.displayList();
    let userArray = [];
    let managerArray = [];
    let adminArray = [];
    UserService.findAll().then(response => {
      if (response.status === 200) {
        response.data.forEach(user => {
          user.overallProgress = 0;
          CourseService.updateUserCourseProgress(user.id)


          user.authorities.forEach(role => {
            if (role.name == 'ROLE_ADMIN') {
              adminArray.push(user);
            }
            else if (role.name == 'ROLE_MANAGER') {
              managerArray.push(user);
            }
            else {
              userArray.push(user);
            }
          })
        })
      }
    });
    this.$store.commit('POPULATE_ADMIN_ARRAY', adminArray);
    this.$store.commit('POPULATE_MANAGER_ARRAY', managerArray);
    this.$store.commit('POPULATE_USER_ARRAY', userArray);
  },
};
</script>

<style>


* {
  font-family: sans-serif;
}

div.main {
  margin-top: 7rem;
  display: flex;
  width: 90%;
  height: 100vh;
  flex-wrap: wrap;
  align-content: center;
  flex-direction: column;
}

#add-course {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  justify-content: left;
  width: 16rem;
  font-weight: bold;
  align-items: center;
  background-color: white;
  opacity: 80%;
  border: 1px solid black; 
}

#routeLink {
 display: flex;
 flex-direction: column;
 justify-content: center;
 height: 100%;
 border: none; 
 color: black;
}

#frmAddNewCourse > div {
  margin: 15px;
  margin-bottom: 15px;
 
}

#frmAddNewCourse > div > input[type=text], 
#frmAddNewCourse > div:nth-child(3) > select,
textarea
 {
   margin-left: 5px;
}

#frmAddNewCourse > div > label {
  margin: 5px;
}

/* description textarea */
#frmAddNewCourse > div:nth-child(2) > textarea {
  width: 11.7rem;
}

/* difficulty */
#frmAddNewCourse > div:nth-child(3) > select {
  width: 6.8rem;
}

 hr {
    border: 1px solid #4cb9bd;
  }

 #links > li {
 background: rgb(31, 143, 143);
 /* padding:0.35em 1.2em; */
 border: 2px solid rgb(31, 143, 143);
 margin: 0 0.3em 0.3em 0;
 border-radius:0.5em;
 box-sizing: border-box;
 text-decoration:none;
 font-family:'Roboto',sans-serif;
 font-weight: bold;
 color:rgb(31, 143, 143);
 text-align:center;
 transition: all 0.2s;
}

#links > li > a {
  color: rgb(255, 255, 255);
  font-weight: normal;
  text-decoration: none;
  text-transform: uppercase;
  letter-spacing: 2px;
}

#links > li:hover {
  color: rgb(31, 143, 143);
  background-color:#FFFFFF;
  cursor: pointer;
  background-position: right top;
}

#links > li > a:hover {
  color: rgb(31, 143, 143);
}


.btn-save {
  margin-top: 7px;
  margin-bottom: 10px;
  margin-left: 3.5rem;
}

button {
 background: rgba(255, 255, 255, 0.4);
 padding:0.35em 1.2em;
 border: 2px solid rgb(31, 143, 143);
 margin: 0 0.3em 0.3em 0;
 border-radius:0.5em;
 box-sizing: border-box;
 text-decoration:none;
 font-family:'Roboto',sans-serif;
 font-weight: bold;
 color:rgb(31, 143, 143);
 text-align:center;
 transition: all 0.2s;
}

button:hover{
 color:rgb(31, 143, 143);
 background-color:#FFFFFF;
 cursor: pointer;
}

#add-course-button {
  background: none;
  border: none;
  white-space: nowrap;
  height: 30%;
  height: 2.5rem;
  width: 8.7rem;
}

/* button {
  background: linear-gradient(to bottom, #26abaf 50%, transparent 100%);
  background-size: 100% 200%;
  background-position: left bottom;
  color: rgb(0, 0, 0);
  font-weight: bold;
  border-radius: 6px;
  border: none;
  width: 50%;
  height: 30%;
  background-color: #7BCED1;
  letter-spacing: .8px;
}

button:hover {
  background-size: 100% 200%;
  background-position: left bottom;
  cursor: pointer;
  background: #7BCED1;
} */


#course-table {
  margin-top: 100px;
  width: 66%;
  border-collapse: collapse;
  background-color: white;
  opacity: 80%;
}

#course-table td {
  text-align: center;
  border: 1px solid black;
 
}

#course-table td button {
  padding: 5px;
  margin: 5px;
}

#course-table th {
  border: 1px solid black;
  padding: 20px;
}


#add-course-button {
  margin-top: 10px;
  margin-bottom: 10px;
}

#frmAddNewCourse {
  border: 1px solid black;
  padding: 3px;
}

.field {
  margin-top: 5px;
  margin-bottom: 5px;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

#AHome {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-grow: 1;
}



</style>