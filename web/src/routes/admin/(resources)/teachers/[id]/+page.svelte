<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import WorkingHoursDayContainer from '$lib/components/working-hours/working-hours-day-container.svelte';
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
  let descriptions = ['Vorname:', 'Nachname:', 'Akronym:'];
  let values: string[] = [teacher.firstName, teacher.lastName, teacher.acronym];

  async function create(values: string[], tagIds: string[], subjectIds?: string[]) {
    let teacherRequestDto: TeacherRequestDto = {
      firstName: values[0],
      lastName: values[1],
      acronym: values[2],
      subjectIds: subjectIds || [],
      tagIds,
    };
    try {
      await createTeacher(teacherRequestDto);
    } catch {
      error(400, { message: 'Could not create tag' });
    }
  }

  async function update(values: string[], tagIds: string[], subjectIds?: string[]) {
    let teacherRequestDto: TeacherRequestDto = {
      firstName: values[0],
      lastName: values[1],
      acronym: values[2],
      subjectIds: subjectIds || [],
      tagIds,
    };
    try {
      await updateTeacher(teacher.id, teacherRequestDto);
    } catch {
      error(400, { message: 'Could not update tag' });
    }
  }

  let tags: Tag[] = data.tags;
</script>

<div class="flex flex-row justify-between">
  <AddResource
    {descriptions}
    {values}
    {create}
    {update}
    createEntity={data.create}
    tags={data.tags}
    entityTags={teacher.tags}
    subjects={data.subjects}
    entitySubjectsIds={new Set(teacher.subjects.map((subject) => subject.id))}
  />
</div>
