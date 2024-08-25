<script lang="ts">
  import Card from '$lib/components/ui/card/card.svelte';
  import { Icons } from '$lib/utils';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import {
    deleteGradeEntry,
    deleteRoomEntry,
    deleteStudentEntry,
    deleteTeacherEntry,
    loadGrades,
    loadRoomPage,
    loadStudentPage,
    loadTeacherPage,
  } from '$lib/utils/resources';
  import { Navbar } from '$lib/elements/ui/navbar';

  export let data;

  let teacherColumnNames = ['Vorname', 'Nachname', 'Akronym', 'Fächer', 'Tags'];
  let teacherKeys = ['id', 'firstName', 'lastName', 'acronym', 'subjects', 'tags'];
  let studentColumnNames = ['Vorname', 'Nachname', 'Tags'];
  let studentKeys = ['id', 'firstName', 'lastName', 'tags'];
  let gradeColumnNames = ['Name', 'Tags'];
  let gradeKeys = ['id', 'name', 'tags'];
  let roomColumnNames = ['Name', 'Gebäude', 'Kapazität', 'Tags'];
  let roomKeys = ['id', 'name', 'buildingName', 'capacity', 'tags'];
</script>

<div class="h-[170px] w-[100%] min-w-[fit-content] bg-primary items-center absolute">
  <Navbar />

  <div class="px-4">
    <div class="w-full mb-12 flex flex-row gap-14 justify-between text-white font-medium text-2xl">
      <Card text="Schüler" icon={Icons.STUDENT} number={data.stats.studentCount} url="/admin/students" />
      <Card text="Lehrer" icon={Icons.TEACHER} number={data.stats.teacherCount} url="/admin/teachers" />
      <Card text="Stufen" icon={Icons.GRADE} number={data.stats.gradeCount} url="/admin/grades" />
      <Card text="Räume" icon={Icons.ROOM} number={data.stats.roomCount} url="/admin/rooms" />
      <Card text="Constraints" icon={Icons.CONSTRAINT} number={data.stats.constraintCount} url="/admin/constraints" />
    </div>
    <div class="flex flex-col gap-16 pb-4">
      <div class="flex flex-col gap-4">
        <p class="font-bold text-2xl">Schüler</p>
        <DataTable
          initialData={data.initialStudents}
          columnNames={studentColumnNames}
          keys={studentKeys}
          loadPage={loadStudentPage}
          deleteEntries={deleteStudentEntry}
          pageSize={5}
          addButton={false}
          editAvailable={false}
        />
      </div>
      <div class="flex flex-col gap-4">
        <p class="font-bold text-2xl">Lehrer</p>
        <DataTable
          initialData={data.initialTeachers}
          columnNames={teacherColumnNames}
          keys={teacherKeys}
          loadPage={loadTeacherPage}
          deleteEntries={deleteTeacherEntry}
          pageSize={5}
          addButton={false}
          editAvailable={false}
        />
      </div>
      <div class="flex flex-col gap-4">
        <p class="font-bold text-2xl">Stufen</p>
        <DataTable
          initialData={data.initialGrades}
          columnNames={gradeColumnNames}
          keys={gradeKeys}
          loadPage={loadGrades}
          deleteEntries={deleteGradeEntry}
          pageSize={5}
          addButton={false}
          editAvailable={false}
        />
      </div>
      <div class="flex flex-col gap-4">
        <p class="font-bold text-2xl">Räume</p>
        <DataTable
          initialData={data.initialRooms}
          columnNames={roomColumnNames}
          keys={roomKeys}
          loadPage={loadRoomPage}
          deleteEntries={deleteRoomEntry}
          pageSize={5}
          addButton={false}
          editAvailable={false}
        />
      </div>
    </div>
  </div>
</div>
