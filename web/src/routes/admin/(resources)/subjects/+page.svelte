<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteSubjectEntry, loadSubjects } from '$lib/utils/resources';

  export let data;
  let columnNames = ['Name', 'Tags'];
  let keys = ['id', 'name', 'tags'];
</script>

<div class="p-10 w-full">
  <DataTable
    initialData={data.initialData}
    {columnNames}
    {keys}
    loadPage={loadSubjects}
    deleteEntry={deleteSubjectEntry}
  />
</div>
