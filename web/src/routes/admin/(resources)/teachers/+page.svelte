<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteTeacherEntry, loadTeacherPage } from '$lib/utils/resources';

  export let data;
  let columnNames = ['Vorname', 'Nachname', 'Akronym', 'Fächer', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'acronym', 'subjects', 'tags'];
</script>

<div class="p-10 w-full">
  <DataTable
    initialData={data.initialData}
    {columnNames}
    {keys}
    loadPage={loadTeacherPage}
    deleteEntry={deleteTeacherEntry}
  />
</div>
