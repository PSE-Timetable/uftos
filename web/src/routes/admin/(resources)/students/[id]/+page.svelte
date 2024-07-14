<script lang="ts">
  import { Input } from '$lib/elements/ui/input';
  import { Button } from '$lib/elements/ui/button';
  import {
    createStudent,
    updateStudent,
    type Student,
    type StudentRequestDto,
    type Tag,
  } from '$lib/sdk/fetch-client.js';
  import TagsMultipleSelect from '$lib/components/ui/tags-multiple-select/tags-multiple-select.svelte';
  import { error } from '@sveltejs/kit';

  export let data;
  let student: Student = data.student;
  let selectedTagIds: string[] = student.tags.map((tag) => tag.id);

  async function create() {
    let studentRequestDto: StudentRequestDto = {
      firstName: student.firstName,
      lastName: student.lastName,
      tagIds: selectedTagIds,
    };
    try {
      await createStudent(studentRequestDto);
    } catch {
      error(400, { message: 'Could not create student' });
    }
  }

  async function update() {
    let studentRequestDto: StudentRequestDto = {
      firstName: student.firstName,
      lastName: student.lastName,
      tagIds: selectedTagIds,
    };
    try {
      await updateStudent(student.id, studentRequestDto);
    } catch {
      error(400, { message: 'Could not update student' });
    }
  }

  let tags: Tag[] = [
    { id: 'tag1', name: 'name1' },
    { id: 'tag2', name: 'name2' },
    { id: 'tag3', name: 'name3' },
    { id: 'tag4', name: 'name4' },
    { id: 'tag5', name: 'name5' },
    { id: 'tag6', name: 'name6' },
    { id: 'tag7', name: 'name7' },
  ]; //TODO get tags for the resource.
</script>

<div class="flex flex-row">
  <div class="m-7 text-xl flex flex-col font-bold">
    <div class="my-5 flex">Vorname:</div>
    <div class="my-5 flex">Nachname:</div>
    <div class="my-5 flex">Tags:</div>
  </div>
  <div class="m-7 flex flex-col w-80">
    <Input
      bind:value={student.firstName}
      class="rounded-none border-0 border-b-4 border-foreground focus-visible:ring-0 focus-visible:border-b-4  text-lg font-normal mt-4"
    />
    <Input
      bind:value={student.lastName}
      class="rounded-none border-0 border-b-4 border-foreground focus-visible:ring-0 focus-visible:border-b-4 text-lg font-normal mt-7"
    />
    <div class="max-w-sm mt-7">
      <TagsMultipleSelect {tags} bind:selectedTagIds />
    </div>
    <div class="max-w-sm mt-5"></div>
    <Button
      on:click={async () => {
        data.create ? await create() : await update();
      }}
      class="max-w-52 px-10 mt-5 bg-accent text-white"
      variant="secondary">Speichern</Button
    >
  </div>
</div>
