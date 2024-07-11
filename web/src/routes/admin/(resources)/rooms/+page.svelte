<script lang="ts">
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { writable } from 'svelte/store';
  let columnNames = ['Name', 'Gebäude', 'Kapazität', 'test'];
  let keys = ['id', 'amount', 'status', 'email'];

  //demo data from shadcn
  const data = [
    {
      id: '728ed52f',
      amount: 100,
      status: 'pending',
      email: 'm@example.com',
    },
    {
      id: '489e1d42',
      amount: 125,
      status: 'processing',
      email: ['test1', 'test2'],
    },
  ];
</script>

<!--not yet formatted-->
<div class="mx-auto p-10 w-full">
  <DataTable tableData={writable(data)} {columnNames} {keys} totalElements={writable(2)} />
</div>
