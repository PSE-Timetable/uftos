<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import { createGrade, updateGrade, type GradeRequestDto } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';
  import { _schema as schema } from './+page';

  export let data;
  let descriptions: string[] = ['Name:'];

  async function update(formData: Record<string, string | number | string[]>) {
    let gradeRequestDto: GradeRequestDto = {
      name: String(formData.name),
      studentGroupIds: formData.studentGroups as string[],
      tagIds: formData.tags as string[],
    };
    try {
      const _ =
        formData.id === 'new'
          ? await createGrade(gradeRequestDto)
          : await updateGrade(String(formData.id), gradeRequestDto);
    } catch {
      error(400, { message: 'Could not update grade' });
    }
  }
</script>

<AddResource {descriptions} data={data.form} {schema} {update} tags={data.tags} />
