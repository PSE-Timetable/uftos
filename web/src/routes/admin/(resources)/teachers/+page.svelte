<script lang="ts">
  import DataTable, { type DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteTeacher, getTeachers, type Pageable, type PageTeacher } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';

  let columnNames = ['Vorname', 'Nachname', 'Akronym', 'Fächer', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'acronym', 'subjects', 'tags'];

  async function loadPage(index: number, sortString: string, filter: string) {
    let pageable: Pageable = { page: index, size: 10, sort: [sortString] };
    try {
      const result: PageTeacher = await getTeachers(pageable, {
        firstName: filter,
        lastName: filter,
        acronym: filter,
      });
      let dataItems: DataItem[] = result.content
        ? result.content.map(
            (teacher): DataItem => ({
              id: teacher.id,
              firstName: teacher.firstName,
              lastName: teacher.lastName,
              acronym: teacher.acronym,
              tags: teacher.tags.map((tag) => tag.name),
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
      await deleteTeacher(id);
    } catch {
      error(400, { message: `could not delete teacher with id ${id}` });
    }
  }
</script>

<div class="p-10 w-full">
  <DataTable {columnNames} {keys} {loadPage} {deleteEntry} />
</div>
