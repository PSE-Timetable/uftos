<script lang="ts">
  import { Button } from '$lib/elements/ui/button';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { createStudent, type Student, type StudentRequestDto } from '$lib/sdk/fetch-client';
  import { writable, type Writable } from 'svelte/store';
  let columnNames = ['Vorname', 'Nachname', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'tags'];
  export let data;
  let tableData: Writable<Student[] | undefined> = writable([]);

  $: $tableData = data.students;
  const create = async () => {
    let student: StudentRequestDto = { firstName: 'Max', lastName: 'Mustermann', tagIds: [] };
    createStudent(student);
  };
</script>

<div class="p-10 bg-white w-full">
  <DataTable {tableData} {columnNames} {keys} />
</div>

<Button on:click={create}>Add</Button>
