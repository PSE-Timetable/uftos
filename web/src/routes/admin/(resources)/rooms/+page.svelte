<script lang="ts">
  import DataTable, { type DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteRoom, getRooms, type Pageable, type PageRoom } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';
  import { onMount } from 'svelte';
  import { writable, type Writable } from 'svelte/store';

  let columnNames = ['Name', 'Gebäude', 'Kapazität', 'Tags'];
  let keys = ['id', 'Name', 'buildingName', 'capacity', 'tags'];
  let tableData: Writable<DataItem[]> = writable([]);
  let totalElements: Writable<number> = writable(0);

  onMount(async () => await loadPage(0, '', ''));

  async function loadPage(index: number, sortString: string, filter: string) {
    let pageable: Pageable = { page: index, size: 10, sort: [sortString] };
    let result: PageRoom;
    try {
      /^\d+$/.test(filter)
        ? (result = await getRooms(pageable, {
            name: filter,
            buildingName: filter,
            capacity: filter as unknown as number,
          }))
        : (result = await getRooms(pageable, {
            name: filter,
            buildingName: filter,
          }));
      totalElements.set(result.totalElements as number);
      tableData.set(result.content as unknown as DataItem[]);
    } catch {
      error(404, { message: 'Could not fetch page' });
    }
  }

  async function deleteEntry(id: string) {
    try {
      await deleteRoom(id);
    } catch {
      error(404, { message: `room with id ${id} could not be found` });
    }
  }
</script>

<div class="mx-auto p-10 w-full">
  {#if $tableData}
    <DataTable {tableData} {columnNames} {keys} {totalElements} {loadPage} {deleteEntry} />
  {/if}
</div>
