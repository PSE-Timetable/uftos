<script lang="ts">
  import type { DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteSubject, getSubjects, type Pageable, type PageSubject } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';
  import { onMount } from 'svelte';
  import { writable, type Writable } from 'svelte/store';

  let columnNames = ['Name', 'Tags'];
  let keys = ['id', 'name', 'tags'];
  let tableData: Writable<DataItem[]> = writable([]);
  let totalElements: Writable<number> = writable(0);

  onMount(async () => await loadPage(0, '', ''));

  async function loadPage(index: number, sortString: string, filter: string) {
    let pageable: Pageable = { page: index, size: 10, sort: [sortString] };
    try {
      const result: PageSubject = await getSubjects(pageable, {
        name: filter,
      });
      totalElements.set(result.totalElements as number);
      tableData.set(result.content as unknown as DataItem[]);
    } catch {
      error(404, { message: 'Could not fetch page' });
    }
  }

  async function deleteEntry(id: string) {
    try {
      await deleteSubject(id);
    } catch {
      error(404, { message: `subject with id ${id} could not be found` });
    }
  }
</script>

<div class="p-10 w-full">
  {#if $tableData}
    <DataTable {tableData} {columnNames} {keys} {totalElements} {loadPage} {deleteEntry} />
  {/if}
</div>
