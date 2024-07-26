<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteRoomEntry, loadRoomPage } from '$lib/utils/resources';
  import { onMount } from 'svelte';

  let columnNames = ['Name', 'Gebäude', 'Kapazität', 'Tags'];
  let keys = ['id', 'name', 'buildingName', 'capacity', 'tags'];
  let pageLoaded = false;

  onMount(() => (pageLoaded = true));
</script>

<div class="mx-auto p-10 w-full">
  {#if pageLoaded}
    <DataTable {columnNames} {keys} loadPage={loadRoomPage} deleteEntry={deleteRoomEntry} />
  {/if}
</div>
