<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteTeacherEntry, loadTeacherPage } from '$lib/utils/resources';
  import { onMount } from 'svelte';

  let columnNames = ['Vorname', 'Nachname', 'Akronym', 'Fächer', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'acronym', 'subjects', 'tags'];
  let pageLoaded = false;

  onMount(() => (pageLoaded = true));
</script>

<div class="p-10 w-full">
  {#if pageLoaded}
    <DataTable {columnNames} {keys} loadPage={loadTeacherPage} deleteEntry={deleteTeacherEntry} />
  {/if}
</div>
