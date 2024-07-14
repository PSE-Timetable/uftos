<script lang="ts">
  import DataTable, { type DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteTag, getTags, type Pageable, type PageTag } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';
  import { onMount } from 'svelte';

  let columnNames = ['Name'];
  let keys = ['id', 'name'];
  let pageLoaded: boolean = false;

  onMount(() => (pageLoaded = true));

  async function loadPage(index: number, sortString: string, filter: string) {
    let pageable: Pageable = { page: index, size: 10, sort: [sortString] };
    try {
      const result: PageTag = await getTags(pageable, {
        name: filter,
      });
      let dataItems: DataItem[] = result.content
        ? result.content.map(
            (tag): DataItem => ({
              id: tag.id,
              name: tag.name,
            }),
          )
        : [];
      return {
        data: dataItems,
        totalElements: Number(result.totalElements),
      };
    } catch {
      error(404, { message: 'Could not fetch page' });
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
