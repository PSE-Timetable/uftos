<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import { createTeacher, updateTeacher, type TeacherRequestDto } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';
  import { _schema as schema } from './+page';

  export let data;
  let descriptions = ['Vorname:', 'Nachname:', 'Akronym:'];

  async function update(formData: Record<string, string | number | string[]>, subjectIds?: string[]) {
    let teacherRequestDto: TeacherRequestDto = {
      firstName: String(formData.firstName),
      lastName: String(formData.lastName),
      acronym: String(formData.acronym),
      subjectIds: subjectIds || [],
      tagIds: formData.tags as string[],
    };
    try {
      const _ =
        formData.id === 'new'
          ? await createTeacher(teacherRequestDto)
          : await updateTeacher(String(formData.id), teacherRequestDto);
    } catch {
      error(400, { message: 'Could not update teacher' });
    }
  }
</script>

<div class="flex flex-row justify-between">
  <AddResource
    {descriptions}
    data={data.form}
    {schema}
    {update}
    tags={data.tags}
    subjects={data.subjects}
    entitySubjectsIds={new Set(data.teacherSubjects)}
  />
</div>
