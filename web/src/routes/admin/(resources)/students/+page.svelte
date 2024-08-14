<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteStudentEntry, loadStudentPage } from '$lib/utils/resources';

  export let data;
  let columnNames = ['Vorname', 'Nachname', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'tags'];
</script>

<div class="px-10 py-4 w-full">
  <DataTable
    initialData={data.initialData}
    {columnNames}
    {keys}
    loadPage={loadStudentPage}
    deleteEntry={deleteStudentEntry}
  />
</div>
