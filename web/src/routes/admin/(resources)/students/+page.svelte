<script lang="ts">
  import { Button } from '$lib/elements/ui/button';
  import DataTable, { type DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import {
    createStudent,
    getStudents,
    type Pageable,
    type PageStudent,
    type StudentRequestDto,
  } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';
  import { onMount } from 'svelte';
  import { writable, type Writable } from 'svelte/store';

  let columnNames = ['Vorname', 'Nachname', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'tags'];
  let tableData: Writable<DataItem[]> = writable([]);
  let totalElements: Writable<number> = writable(0);
  let mmNumber = 0;

  const create = async () => {
    let student: StudentRequestDto = { firstName: 'Max' + mmNumber, lastName: 'Mustermann', tagIds: [] };
    await createStudent(student);
    mmNumber++;
  };

  onMount(async () => await loadPage(0, '', ''));

  async function loadPage(index: number, sortString: string, filter: string) {
    let pageable: Pageable;

    sortString ? (pageable = { page: index, size: 10, sort: [sortString] }) : (pageable = { page: index, size: 10 });
    try {
      const result: PageStudent = await getStudents(pageable, {
        firstName: filter,
        lastName: filter,
      });
      totalElements.set(result.totalElements as number);
      tableData.set(result.content as unknown as DataItem[]);
    } catch {
      error(404, { message: 'Could not fetch page' });
    }
  }
</script>

<div class="p-10 w-full">
  <DataTable {tableData} {columnNames} {keys} {totalElements} {loadPage} />
</div>

<Button on:click={create}>Add</Button>
