<script lang="ts">
  import { Button } from '$lib/elements/ui/button';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { createStudent, type StudentRequestDto } from '$lib/sdk/fetch-client';
  import { deleteStudentEntry, loadStudentPage } from '$lib/utils/resources';
  import { onMount } from 'svelte';

  let columnNames = ['Vorname', 'Nachname', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'tags'];
  let pageLoaded = false;
  onMount(() => (pageLoaded = true));

  let mmNumber = 0;
  const create = async () => {
    let student: StudentRequestDto = { firstName: 'Max' + mmNumber, lastName: 'Mustermann', tagIds: [] };
    await createStudent(student);
    mmNumber++;
  };
</script>

<div class="p-10 w-full">
  <!--Avoids warning that fetch calls should be in onMount or load function, there must be a better solution-->
  {#if pageLoaded}
    <DataTable {columnNames} {keys} loadPage={loadStudentPage} deleteEntry={deleteStudentEntry} />
  {/if}
</div>

<Button on:click={create}>Add</Button>
