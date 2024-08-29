<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import { Button } from '$lib/elements/ui/button';
  import ComboBox from '$lib/elements/ui/combo-box/combo-box.svelte';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import {
    addStudentsToStudentGroup,
    deleteStudentGroup,
    getStudentGroups,
    getStudents,
    type Pageable,
    type Student,
    type StudentGroupResponseDto,
  } from '$lib/sdk/fetch-client.js';
  import { getStudentsFromGroup, removeStudentFromGroup } from '$lib/utils/resources.js';
  import { error } from '@sveltejs/kit';
  import { Pencil, Plus, Trash2 } from 'lucide-svelte';

  export let data;
  let studentGroups = data.studentGroups || [];
  let students: Student[] = data.students || [];
  let selectedStudentIds: string[] = [];
  let reloadTable: boolean = false;

  let columnNames = ['Vorname', 'Nachname', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'tags'];

  async function addStudentToGroup(studentGroup: StudentGroupResponseDto, studentId: string) {
    studentGroup = await addStudentsToStudentGroup(studentGroup.id, [studentId]);
    reloadTable = !reloadTable;
  }

  async function updateStudents(value: string) {
    const pageable: Pageable = { page: 0, size: 40 };
    try {
      students = (await getStudents(pageable, { search: value }).then(({ content }) => content)) || [];
    } catch {
      error(400, { message: 'Could not fetch students' });
    }
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
  {#each studentGroups as studentGroup, i}
    <div class="flex flex-row w-full gap-8 items-top my-5">
      <div class="flex flex-col gap-8 bg-primary w-fit h-fit p-6 rounded-md text-white">
        <div class="flex flex-row justify-between">
          <p class="font-bold text-md">{studentGroup.name}</p>
          <div class="flex flex-row gap-4">
            <button type="button" on:click={() => goto(`${$page.url}/${studentGroup.id}`)}>
              <Pencil class="hover:stroke-accent" />
            </button>
            <button type="button" on:click={() => deleteGroup(studentGroup.id)}>
              <Trash2 class="hover:stroke-accent" />
            </button>
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
              bind:selectedId={selectedStudentIds[i]}
            />
          </div>
        </div>
        <Button
          variant="outline"
          class="bg-accent border-0 text-md text-white py-6"
          on:click={() => selectedStudentIds[i] && addStudentToGroup(studentGroup, selectedStudentIds[i])}
          >Schüler hinzufügen</Button
        >
      </div>
      {#key reloadTable}
        <div class="w-full">
          {#await getStudentsFromGroup('', '', 0, 5, studentGroup.id) then initialData}
            <DataTable
              {initialData}
              {columnNames}
              {keys}
              loadPage={getStudentsFromGroup}
              deleteEntries={removeStudentFromGroup}
              additionalId={studentGroup.id}
              sortable={false}
              addButton={false}
              editAvailable={false}
              pageSize={5}
            />
          {/await}
        </div>
      {/key}
    </div>
  {:else}
    <div class="text-3xl font-semibold flex justify-center mt-14">Keine Gruppen vorhanden.</div>
  {/each}
</div>
