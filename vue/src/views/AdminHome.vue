<template>
<div class="AHomeBg">
  <div >
    <course-overview id="courseTable"/>
    <!-- <div v-if="isLoading"></div> -->
  </div>
</div>
</template>

<script>
import CourseOverview from '../components/CourseOverview.vue'
export default {
    components: {
        CourseOverview
    }

}
</script>

<style >

.AHomeBg {

  display: flex;
  flex-direction: column;
  background-image: url('../../images/partywork.jpg');
  background-attachment: fixed;
  background-size: cover;
  
  

}



</style>