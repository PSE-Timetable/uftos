<script lang="ts">
  import DataTable, { type DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import { deleteStudent, getStudents, type Pageable, type PageStudent } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';

  let columnNames = ['Vorname', 'Nachname', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'tags'];

  async function loadPage(index: number, sortString: string, filter: string) {
    let pageable: Pageable = { page: index, size: 10, sort: [sortString] };
    try {
      const result: PageStudent = await getStudents(pageable, {
        firstName: filter,
        lastName: filter,
      });
      let dataItems: DataItem[] = result.content
        ? result.content.map(
            (student): DataItem => ({
              id: student.id,
              firstName: student.firstName,
              lastName: student.lastName,
              tags: student.tags.map((tag) => tag.name),
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
      await deleteStudent(id);
    } catch {
      error(400, { message: `could not delete student with id ${id}` });
    }
  }
</script>

<div class="p-10 w-full">
  <!--Avoids warning that fetch calls should be in onMount or load function, there must be a better solution-->
  <DataTable {columnNames} {keys} {loadPage} {deleteEntry} />
</div>
