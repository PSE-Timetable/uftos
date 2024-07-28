<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import { Button } from '$lib/elements/ui/button';
  import ComboBox from '$lib/elements/ui/combo-box/combo-box.svelte';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import {
    addStudentsToStudentGroup,
    deleteStudentGroup,
    getGrades,
    getStudentGroup,
    getStudentGroups,
    getStudents,
    removeStudentsFromStudentGroup,
    updateStudentGroup,
    type GradeResponseDto,
    type Pageable,
    type Student,
    type StudentGroup,
    type StudentGroupRequestDto,
  } from '$lib/sdk/fetch-client.js';
  import { type DataItem } from '$lib/utils/resources.js';
  import { error } from '@sveltejs/kit';
  import { Pencil, Plus, Trash2 } from 'lucide-svelte';

  export let data;
  let studentGroups = data.studentGroups || [];
  let students: Student[] = data.students || [];
  let selectedStudentId: string;
  let grades: GradeResponseDto[] = data.grades;
  let selectedGradeId: string;
  let reloadTable: boolean = false;

  let columnNames = ['Vorname', 'Nachname', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'tags'];

  async function getStudentsFromGroup(index: number, toSort: string, filter: string, additionalId?: string) {
    if (!additionalId) {
      throw error(400, { message: 'Invalid student group id' });
    }
    try {
      const studentGroup = await getStudentGroup(additionalId);
      let dataItems: DataItem[] = studentGroup.students.map(
        (student): DataItem => ({
          id: student.id,
          firstName: student.firstName,
          lastName: student.lastName,
          tags: student.tags.map((tag) => tag.name),
        }),
      );
      return {
        data: dataItems,
        totalElements: Number(studentGroup.students.length),
      };
    } catch {
      error(400, { message: 'Could not get student from group' });
    }
  }

  async function addStudentToGroup(studentGroup: StudentGroup, studentId: string) {
    studentGroup = await addStudentsToStudentGroup(studentGroup.id, [studentId]);
    reloadTable = !reloadTable;
  }

  async function removeStudentFromGroup(studentId: string, studentGroupId?: string) {
    if (!studentGroupId) {
      throw error(400, { message: 'Invalid student group id' });
    }
    try {
      await removeStudentsFromStudentGroup(studentGroupId, [studentId]);
    } catch {
      error(400, { message: 'Could not remove student from group' });
    }
  }

  async function updateStudents(value: string) {
    const pageable: Pageable = { page: 0, size: 40 };
    try {
      students =
        (await getStudents(pageable, { firstName: value, lastName: value }).then(({ content }) => content)) || [];
    } catch {
      error(400, { message: 'Could not fetch students' });
    }
  }

  async function updateGrades(value: string) {
    try {
      grades = await getGrades({ sort: ['name,asc'] }, { name: value });
    } catch {
      error(400, { message: 'Could not fetch grades' });
    }
  }

  async function saveGrade(gradeId: string, studentGroupId: string) {
    let studentGroup = await getStudentGroup(studentGroupId); //studentGroups field doesn't contain newly added students
    let requestDto: StudentGroupRequestDto = {
      gradeIds: [gradeId],
      name: studentGroup.name,
      studentIds: studentGroup.students.map((student) => student.id),
      tagIds: studentGroup.tags.map((tag) => tag.id),
    };
    await updateStudentGroup(studentGroup.id, requestDto);
  }

  async function deleteGroup(id: string) {
    await deleteStudentGroup(id);
    studentGroups =
      (await getStudentGroups({ page: 0, size: 50, sort: ['name,asc'] }).then(({ content }) => content)) || [];
  }
</script>

<div class="flex justify-end mb-4">
  <Button
    class="mt-5 mx-5 text-md text-primary bg-white shadow-customSmall"
    variant="secondary"
    on:click={() => goto(`${$page.url}/new`)}
    >Hinzufügen
    <Plus class="ml-3" />
  </Button>
</div>

<div class="flex flex-col gap-8 p-4">
  {#each studentGroups as studentGroup}
    <div class="flex flex-row w-full gap-8 items-top my-5">
      <div class="flex flex-col gap-8 bg-primary w-fit h-fit p-6 rounded-md text-white">
        <div class="flex flex-row justify-between">
          <p class="font-bold text-md">{studentGroup.name}</p>
          <div>
            <button type="button" on:click={() => goto(`${$page.url}/${studentGroup.id}`)}><Pencil /> </button>
            <button type="button" on:click={() => deleteGroup(studentGroup.id)}><Trash2 /> </button>
          </div>
        </div>
        <div class="flex flex-row items-center justify-between w-full gap-8">
          <p>Stufe:</p>
          <div class="text-primary">
            <ComboBox
              onSearch={(value) => updateGrades(value)}
              data={grades.map((grade) => ({ value: grade.id, label: grade.name }))}
              bind:selectedId={selectedGradeId}
              onSelectChange={() => saveGrade(selectedGradeId, studentGroup.id)}
            />
          </div>
        </div>
        <div class="flex flex-row items-center justify-between w-full gap-8">
          <p>Schüler:</p>
          <div class="text-primary">
            <ComboBox
              onSearch={(value) => updateStudents(value)}
              data={students.map((student) => ({
                value: student.id,
                label: `${student.firstName} ${student.lastName}`,
              }))}
              bind:selectedId={selectedStudentId}
            />
          </div>
        </div>
        <Button
          variant="outline"
          class="bg-accent border-0 text-md text-white py-6"
          on:click={() => selectedStudentId && addStudentToGroup(studentGroup, selectedStudentId)}
          >Schüler hinzufügen</Button
        >
      </div>
      {#key reloadTable}
        <div class="w-full">
          <DataTable
            {columnNames}
            {keys}
            loadPage={getStudentsFromGroup}
            deleteEntry={removeStudentFromGroup}
            additionalId={studentGroup.id}
            sortable={false}
            addButton={false}
            pageSize={5}
          />
        </div>
      {/key}
    </div>
  {/each}
</div>
