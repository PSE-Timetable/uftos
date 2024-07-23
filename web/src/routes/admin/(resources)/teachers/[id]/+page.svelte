<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import {
    createTeacher,
    updateTeacher,
    type Tag,
    type Teacher,
    type TeacherRequestDto,
  } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';

  export let data;
  let teacher: Teacher = data.teacher;
  let descriptions = ['Vorname:', 'Nachname:', 'Akronym:', 'Fächer:', 'Tags:'];
  let values: string[] = [teacher.firstName, teacher.lastName, teacher.acronym];

  async function create(values: string[], tagIds: string[]) {
    let teacherRequestDto: TeacherRequestDto = {
      firstName: values[0],
      lastName: values[1],
      acronym: values[2],
      subjectIds: teacher.subjects.map((subject) => subject.id),
      tagIds: teacher.tags.map((tag) => tag.id),
    };
    try {
      await createTeacher(teacherRequestDto);
    } catch {
      error(400, { message: 'Could not create tag' });
    }
  }

  async function update() {
    let teacherRequestDto: TeacherRequestDto = {
      firstName: teacher.firstName,
      lastName: teacher.lastName,
      acronym: teacher.acronym,
      subjectIds: teacher.subjects.map((subject) => subject.id),
      tagIds: teacher.tags.map((tag) => tag.id),
    };
    try {
      await updateTeacher(teacher.id, teacherRequestDto);
    } catch {
      error(400, { message: 'Could not update tag' });
    }
  }

  let tags: Tag[] = data.tags;
</script>

<AddResource
  {descriptions}
  {values}
  {create}
  {update}
  createEntity={data.create}
  tags={data.tags}
  entityTags={teacher.tags}
  subjects={data.subjects}
  enitySubjects={teacher.subjects}
/>
