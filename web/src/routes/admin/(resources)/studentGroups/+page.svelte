<script lang="ts">
  import { Button } from '$lib/elements/ui/button';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import {
    addStudentsToStudentGroup,
    getStudentGroup,
    removeStudentsFromStudentGroup,
    type StudentGroup,
  } from '$lib/sdk/fetch-client.js';
  import { type DataItem } from '$lib/utils/resources.js';
  import { error } from '@sveltejs/kit';

  export let data;
  let studentGroups = data.studentGroups;
  let reloadTable = {};

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
    await addStudentsToStudentGroup(studentGroup.id, [studentId]);
    reloadTable = {};
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
</script>

<div class="p-4">
  {#each studentGroups || [] as studentGroup}
    <div class="flex flex-row w-full gap-8 items-center">
      <div class="flex flex-col gap-8 bg-primary w-fit p-6 rounded-md text-white">
        <p class="font-bold text-md">{studentGroup.name}</p>
        <div class="flex flex-row items-center justify-between w-full gap-8">
          <p>Stufe:</p>
          <div class="text-primary">
            <!-- Wait for merge of constraint component for combobox 
            <ComboBox
            onSearch={(value) => updateItems(value, parameter.parameterName, parameter.parameterType)}
            data={data[parameter.parameterName || '']}
          />
           -->
          </div>
        </div>
        <div class="flex flex-row items-center justify-between w-full gap-8">
          <p>Schüler:</p>
          <div class="text-primary">
            <!-- Wait for merge of constraint component for combobox 
            <ComboBox
            onSearch={(value) => updateItems(value, parameter.parameterName, parameter.parameterType)}
            data={data[parameter.parameterName || '']}
          />
           -->
          </div>
        </div>
        <Button
          variant="outline"
          class="bg-accent border-0 text-md text-white py-6"
          on:click={async () => {
            addStudentToGroup(studentGroup, ''); //TODO add student id from combobox
          }}>Schüler hinzufügen</Button
        >
      </div>
      {#key reloadTable}
        <div class="w-full">
          <!-- Wait for merge of constraint component for data table changes
          <DataTable
            columnNames,
            keys,
            loadPage={getStudentsFromGroup}
            deleteEntry={removeStudentFromGroup}
            additionalId={studentGroup.id}
            sortable={false}
            addButton={false}
            pageSize={5}
          />
          -->
        </div>
      {/key}
    </div>
  {/each}
</div>
