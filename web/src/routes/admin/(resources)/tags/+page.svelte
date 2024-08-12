<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteTagEntry, loadTags } from '$lib/utils/resources';

  export let data;
  let columnNames = ['Name'];
  let keys = ['id', 'name'];
</script>

<div class="p-10 w-full">
  <DataTable initialData={data.initialData} {columnNames} {keys} loadPage={loadTags} deleteEntry={deleteTagEntry} />
</div>
