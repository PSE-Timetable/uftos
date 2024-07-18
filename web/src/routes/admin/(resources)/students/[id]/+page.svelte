<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import {
    createStudent,
    updateStudent,
    type Student,
    type StudentRequestDto,
    type Tag,
  } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';

  export let data;
  let student: Student = data.student;
  let values: string[] = [student.firstName, student.lastName];
  let descriptions: string[] = ['Vorname:', 'Nachname:', 'Tags:'];

  async function create(values: string[], tagIds: string[]) {
    let studentRequestDto: StudentRequestDto = {
      firstName: values[0],
      lastName: values[1],
      tagIds,
    };
    try {
      await createStudent(studentRequestDto);
    } catch {
      error(400, { message: 'Could not create student' });
    }
  }

  async function update(values: string[], tagIds: string[]) {
    let studentRequestDto: StudentRequestDto = {
      firstName: values[0],
      lastName: values[1],
      tagIds,
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

<AddResource {descriptions} {values} {create} {update} createEntity={data.create} {tags} entityTags={student.tags} />
