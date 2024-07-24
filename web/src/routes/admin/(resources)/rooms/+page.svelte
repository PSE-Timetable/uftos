<script lang="ts">
  import DataTable, { type DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteRoom, getRooms, type Pageable, type PageRoom } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';
  import { onMount } from 'svelte';

  let columnNames = ['Name', 'Gebäude', 'Kapazität', 'Tags'];
  let keys = ['id', 'name', 'buildingName', 'capacity', 'tags'];
  let pageLoaded = false;

  onMount(() => (pageLoaded = true));

  async function loadPage(index: number, sortString: string, filter: string) {
    let pageable: Pageable = { page: index, size: 10, sort: [sortString] };
    let result: PageRoom;
    try {
      result = await getRooms(pageable, {
        name: filter,
        buildingName: filter,
        capacity: /^\d+$/.test(filter) ? (filter as unknown as number) : undefined,
      });
      let dataItems: DataItem[] = result.content
        ? result.content.map(
            (room): DataItem => ({
              id: room.id,
              name: room.name,
              buildingName: room.buildingName,
              capacity: room.capacity,
              tags: room.tags.map((tag) => tag.name),
            }),
          )
        : [];
      return {
        data: dataItems,
        totalElements: Number(result.totalElements),
      };
    } catch {
      error(400, { message: 'Could not fetch page' });
    }
  }

  async function deleteEntry(id: string) {
    try {
      await deleteRoom(id);
    } catch {
      error(400, { message: `could not delete room with id ${id}` });
    }
  }
</script>

<div class="mx-auto p-10 w-full">
  {#if pageLoaded}
    <DataTable {columnNames} {keys} {loadPage} {deleteEntry} />
  {/if}
</div>
