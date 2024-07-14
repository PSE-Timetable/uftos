<script lang="ts">
  import DataTable, { type DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteStudent, getStudents, type Pageable, type PageStudent } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';
  import { onMount } from 'svelte';
  import { writable, type Writable } from 'svelte/store';

  let columnNames = ['Vorname', 'Nachname', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'tags'];
  let tableData: Writable<DataItem[]> = writable();
  let totalElements: Writable<number> = writable(0);

  onMount(async () => loadPage(0, '', ''));

  async function loadPage(index: number, sortString: string, filter: string) {
    let pageable: Pageable = { page: index, size: 10, sort: [sortString] };
    try {
      const result: PageStudent = await getStudents(pageable, {
        firstName: filter,
        lastName: filter,
      });
      totalElements.set(Number(result.totalElements));
      let dataItems: DataItem[] = result.content
        ? result.content.map(
            (student): DataItem => ({
              id: student.id,
              firstName: student.firstName,
              lastName: student.lastName,
              tags: student.tags.map((tag) => tag.name),
            }),
          )
        : [];
      tableData.set(dataItems);
    } catch {
      error(404, { message: 'Could not fetch page' });
    }
  }

  async function deleteEntry(id: string) {
    try {
      await deleteStudent(id);
    } catch {
      error(400, { message: `could not delete student with id ${id}` });
    }
  }
</script>

<div class="p-10 w-full">
  {#if $tableData}
    <DataTable {tableData} {columnNames} {keys} {totalElements} {loadPage} {deleteEntry} />
  {/if}
</div>
