<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteTagEntry, loadTags } from '$lib/utils/resources';
  import { onMount } from 'svelte';

  let columnNames = ['Name'];
  let keys = ['id', 'name'];
  let pageLoaded = false;

  onMount(() => (pageLoaded = true));
</script>

<div class="p-10 w-full">
  {#if pageLoaded}
    <DataTable {columnNames} {keys} loadPage={loadTags} deleteEntry={deleteTagEntry} />
  {/if}
</div>
