<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteGradeEntry, loadGrades } from '$lib/utils/resources';

  export let data;
  let columnNames = ['Name', 'Tags'];
  let keys = ['id', 'name', 'tags'];
</script>

<div class="mx-auto p-10 w-full">
  <DataTable initialData={data.initialData} {columnNames} {keys} loadPage={loadGrades} deleteEntry={deleteGradeEntry} />
</div>
