<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteGradeEntry, loadGrades } from '$lib/utils/resources';
  import { onMount } from 'svelte';

  let columnNames = ['Name', 'Tags'];
  let keys = ['id', 'name', 'tags'];
  let pageLoaded = false;

  onMount(() => (pageLoaded = true));
</script>

<div class="mx-auto p-10 w-full">
  {#if pageLoaded}
    <DataTable {columnNames} {keys} loadPage={loadGrades} deleteEntry={deleteGradeEntry} />
  {/if}
</div>
