<script lang="ts">
  import type { DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteSubject, getSubjects, type Pageable, type PageSubject } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';

  let columnNames = ['Name', 'Tags'];
  let keys = ['id', 'name', 'tags'];

  async function loadPage(index: number, sortString: string, filter: string) {
    let pageable: Pageable = { page: index, size: 10, sort: [sortString] };
    try {
      const result: PageSubject = await getSubjects(pageable, {
        name: filter,
      });
      let dataItems: DataItem[] = result.content
        ? result.content.map(
            (subject): DataItem => ({
              id: subject.id,
              name: subject.name,
              tags: subject.tags.map((tag) => tag.name),
            }),
          )
        : [];
      return {
        data: dataItems,
        totalElements: Number(result.totalElements),
      };
    } catch {
      error(400, { message: 'Could not fetch page' });
    }
  }

  async function deleteEntry(id: string) {
    try {
      await deleteSubject(id);
    } catch {
      error(400, { message: `could not delete subject with id ${id}` });
    }
  }
</script>

<div class="p-10 w-full">
  <DataTable {columnNames} {keys} {loadPage} {deleteEntry} />
</div>
