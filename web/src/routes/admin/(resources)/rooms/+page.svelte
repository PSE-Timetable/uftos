<script lang="ts">
  import pageName from '../pageName';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  let columnNames = ['Name', 'Gebäude', 'Kapazität', 'test'];

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

  pageName.set('Raummanagment');
</script>

<!--not yet formatted-->
<div class="container mx-auto py-10 bg-white w-full mt-5">
  <DataTable {data} {columnNames} />
</div>
