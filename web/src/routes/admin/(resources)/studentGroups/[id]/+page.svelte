<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import { createStudentGroup, updateStudentGroup, type StudentGroupRequestDto } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';
  import { _schema as schema } from './+page';

  export let data;
  let descriptions: string[] = ['Name:'];

  async function update(formData: Record<string, string | number | string[]>, subjectIds?: string[]) {
    let studentGroupRequestDto: StudentGroupRequestDto = {
      name: String(formData.name),
      gradeIds: formData.grades as string[],
      studentIds: formData.students as string[],
      tagIds: formData.tags as string[],
      subjectIds: subjectIds || [],
    };
    try {
      const _ =
        formData.id === 'new'
          ? await createStudentGroup(studentGroupRequestDto)
          : await updateStudentGroup(String(formData.id), studentGroupRequestDto);
    } catch {
      error(400, { message: 'Could not update student group' });
    }
  }
</script>

<AddResource
  {descriptions}
  data={data.form}
  {schema}
  {update}
  tags={data.tags}
  subjects={data.subjects}
  entitySubjectsIds={new Set(data.groupSubjects)}
  grades={data.grades}
/>
