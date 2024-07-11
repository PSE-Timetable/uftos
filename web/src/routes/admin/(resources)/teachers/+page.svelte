<script lang="ts">
  import DataTable, { type DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import { getTeachers, type Pageable, type PageTeacher } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';
  import { onMount } from 'svelte';
  import { writable, type Writable } from 'svelte/store';

  let columnNames = ['Vorname', 'Nachname', 'Akronym', 'Fächer', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'acronym', 'subjects', 'tags'];
  let tableData: Writable<DataItem[]> = writable([]);
  let totalElements: Writable<number> = writable(0);

  onMount(async () => await loadPage(0, '', ''));

  async function loadPage(index: number, sortString: string, filter: string) {
    let pageable: Pageable = { page: index, size: 10, sort: [sortString] };
    try {
      const result: PageTeacher = await getTeachers(pageable, {
        firstName: filter,
        lastName: filter,
        acronym: filter,
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
