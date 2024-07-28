<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteStudentEntry, loadStudentPage } from '$lib/utils/resources';
  import { onMount } from 'svelte';

  let columnNames = ['Vorname', 'Nachname', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'tags'];
  let pageLoaded = false;
  onMount(() => (pageLoaded = true));
</script>

<div class="px-10 py-4 w-full">
  <!--Avoids warning that fetch calls should be in onMount or load function, there must be a better solution-->
  {#if pageLoaded}
    <DataTable {columnNames} {keys} loadPage={loadStudentPage} deleteEntry={deleteStudentEntry} />
  {/if}
</div>
