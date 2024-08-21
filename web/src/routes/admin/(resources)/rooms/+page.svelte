<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteRoomEntry, loadRoomPage } from '$lib/utils/resources';

  export let data;
  let columnNames = ['Name', 'Gebäude', 'Kapazität', 'Tags'];
  let keys = ['id', 'name', 'buildingName', 'capacity', 'tags'];
</script>

<div class="mx-auto p-10 w-full">
  <DataTable
    initialData={data.initialData}
    {columnNames}
    {keys}
    loadPage={loadRoomPage}
    deleteEntries={deleteRoomEntry}
  />
</div>
