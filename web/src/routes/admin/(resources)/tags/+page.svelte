<script lang="ts">
  import DataTable, { type DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteTag, getTags, type Sort } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';
  import { onMount } from 'svelte';

  let columnNames = ['Name'];
  let keys = ['id', 'name'];
  let pageLoaded = false;

  onMount(() => (pageLoaded = true));

  async function loadPage(index: number, sortString: string, filter: string) {
    let sort: Sort = { sort: [sortString] };
    try {
      const result = await getTags(sort, {
        name: filter,
      });
      let dataItems: DataItem[] = result.map((tag) => ({
        id: tag.id,
        name: tag.name,
      }));

      return {
        data: dataItems,
        totalElements: result.length,
      };
    } catch {
      error(400, { message: 'Could not fetch page' });
    }
  }

  async function deleteEntry(id: string) {
    try {
      await deleteTag(id);
    } catch {
      error(400, { message: `could not delete tag with id ${id}` });
    }
  }
</script>

<div class="p-10 w-full">
  {#if pageLoaded}
    <DataTable {columnNames} {keys} {loadPage} {deleteEntry} />
  {/if}
</div>
