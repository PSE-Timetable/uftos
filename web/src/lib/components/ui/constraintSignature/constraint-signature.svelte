<script lang="ts">
  import {
    type ConstraintSignature,
    getGrades,
    getRooms,
    getStudentGroups,
    getStudents,
    getSubjects,
    getTags,
    getTeachers,
    getTimeslots,
    type GradeResponseDto,
    type Pageable,
    type PageGradeResponseDto,
    type PageRoom,
    type PageStudent,
    type PageStudentGroup,
    type PageSubject,
    type PageTag,
    type PageTeacher,
    type PageTimeslot,
    ParameterType,
    type Room,
    type Student,
    type StudentGroup,
    type Subject,
    type Tag,
    type Teacher,
    type Timeslot,
  } from '$lib/sdk/fetch-client';
  import { Button } from '$lib/elements/ui/button';
  import ComboBox from '$lib/elements/ui/combo-box/combo-box.svelte';

  export let constraintSignature: ConstraintSignature;
  export let teachers: Teacher[] = [];
  export let rooms: Room[] = [];
  export let grades: GradeResponseDto[] = [];
  export let tags: Tag[] = [];
  export let timeslots: Timeslot[] = [];
  export let studentGroups: StudentGroup[] = [];
  export let students: Student[] = [];
  export let subjects: Subject[] = [];

  $: {
    if (teachers.length > 0) createTeacherItems();
    if (rooms.length > 0) createRoomItems();
    if (grades.length > 0) createGradeItems();
    if (tags.length > 0) createTagItems();
    if (timeslots.length > 0) createTimeSlotItems();
    if (studentGroups.length > 0) createStundetGroupItems();
    if (students.length > 0) createStudentItems();
    if (subjects.length > 0) createSubjectItems();
  }

  let teacherItems: { value: string; label: string }[] = [];
  let roomItems: { value: string; label: string }[] = [];
  let gradeItems: { value: string; label: string }[] = [];
  let tagItems: { value: string; label: string }[] = [];
  let timeslotItems: { value: string; label: string }[] = [];
  let stundeGroupItems: { value: string; label: string }[] = [];
  let studentItems: { value: string; label: string }[] = [];
  let subjectItems: { value: string; label: string }[] = [];

  function createTeacherItems() {
    teacherItems = teachers.map(teacher => ({ value: `${teacher.firstName} ${teacher.lastName}`, label: `${teacher.firstName} ${teacher.lastName}` }));
    teacherItems = teacherItems;
    console.log("teacher items size" + teacherItems.length);
  }

  function createRoomItems() {
    roomItems = rooms.map(room => ({ value: `${room.buildingName}: ${room.name}`, label: `${room.buildingName}: ${room.name}` }));
  }

  function createGradeItems() {
    gradeItems = grades.map(grade => ({ value: grade.name ?? '', label: grade.name ?? '' }));
  }

  function createTagItems() {
    tagItems = tags.map(tag => ({ value: tag.name, label: tag.name }));
  }

  function createTimeSlotItems() {
    timeslotItems = timeslots.map(timeslot => ({ value: `${timeslot.day}: ${timeslot.slot}`, label: `${timeslot.day}: ${timeslot.slot}` }));
  }

  function createStundetGroupItems() {
    stundeGroupItems = studentGroups.map(studentGroup => ({ value: studentGroup.name, label: studentGroup.name }));
  }

  function createStudentItems() {
    studentItems = students.map(student => ({ value: `${student.firstName} ${student.lastName}`, label: `${student.firstName} ${student.lastName}` }));
  }

  function createSubjectItems() {
    subjectItems = subjects.map(subject => ({ value: subject.name, label: subject.name }));
  }

  async function updateItems(value: string, parameterType?: ParameterType) {
    if (!value) {
      value = '';
    }
    const page: Pageable = { page: 0, size: 2 };
    try {
      switch (parameterType) {
        case ParameterType.Grade: {
          const response: PageGradeResponseDto = await getGrades(page, { name: value });
          grades = response.content || [];
          createGradeItems();
          break;
        }
        case ParameterType.Subject: {
          const response: PageSubject = await getSubjects(page, { name: value });
          subjects = response.content || [];
          createSubjectItems();
          break;
        }
        case ParameterType.Room: {
          const response1: PageRoom = await getRooms(page, { name: value });
          const response2: PageRoom = await getRooms(page, { buildingName: value });
          rooms = response1.content || [];
          createRoomItems();
          break;
        }
        case ParameterType.StudentGroup: {
          const response: PageStudentGroup = await getStudentGroups(page, { name: value });
          studentGroups = response.content || [];
          createStundetGroupItems();
          break;
        }
        case ParameterType.Student: {
          const response1: PageStudent = await getStudents(page, { firstName: value });
          const response2: PageStudent = await getStudents(page, { lastName: value });
          students = response1.content || [];
          createStudentItems();
          break;
        }
        case ParameterType.Tag: {
          const response: PageTag = await getTags(page, { name: value });
          tags = response.content || [];
          createTagItems();
          break;
        }
        case ParameterType.Teacher: {
          console.log("search for " + value + " started");
          const response1: PageTeacher = await getTeachers(page, { firstName: value });
          const response2: PageTeacher = await getTeachers(page, { lastName: value });
          console.log("search done");
          teachers = response1.content || [];
          console.log("response size: " + teachers.length);
          createTeacherItems();
          break;
        }
        case ParameterType.Timeslot: {
          const response: PageTimeslot = await getTimeslots(page);
          timeslots = response.content || [];
          createTimeSlotItems();
          break;
        }
      }
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  }
  
</script>

<div class="flex flex-col gap-8 bg-primary w-fit p-6 rounded-md text-white">
  <p class="font-bold text-md">{constraintSignature.description}</p>

  {#each constraintSignature.parameters as parameter}
    <div class="flex flex-row items-center justify-around w-full gap-8">
      <p>{parameter.parameterName}</p>
      <div class="text-primary">
        {#if parameter.parameterType === ParameterType.Teacher}
          <ComboBox onSearch={(value) => updateItems(value, parameter.parameterType)} data={teacherItems} />
        {:else if parameter.parameterType === ParameterType.Room}
          <ComboBox onSearch={(value) => updateItems(value, parameter.parameterType)} data={roomItems} />
        {:else if parameter.parameterType === ParameterType.Grade}
          <ComboBox onSearch={(value) => updateItems(value, parameter.parameterType)} data={gradeItems} />
        {:else if parameter.parameterType === ParameterType.Tag}
          <ComboBox onSearch={(value) => updateItems(value, parameter.parameterType)} data={tagItems} />
        {:else if parameter.parameterType === ParameterType.Timeslot}
          <ComboBox onSearch={(value) => updateItems(value, parameter.parameterType)} data={timeslotItems} />
        {:else if parameter.parameterType === ParameterType.StudentGroup}
          <ComboBox onSearch={(value) => updateItems(value, parameter.parameterType)} data={stundeGroupItems} />
        {:else if parameter.parameterType === ParameterType.Subject}
          <ComboBox onSearch={(value) => updateItems(value, parameter.parameterType)} data={subjectItems} />
        {/if}
      </div>
    </div>
  {/each}

  <Button variant="outline" class="bg-accent border-0 text-md text-white py-6">Hinzufügen</Button>
</div>
