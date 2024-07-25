<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import { createGrade, updateGrade, type GradeRequestDto } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';

  export let data;
  let grade = data.grade;
  let values: string[] = [grade.name];
  let descriptions: string[] = ['Name:'];

  async function create(values: string[], tagIds: string[]) {
    let gradeRequestDto: GradeRequestDto = {
      name: values[0],
      studentGroupIds: [],
      tagIds,
    };
    try {
      await createGrade(gradeRequestDto);
    } catch {
      error(400, { message: 'Could not create grade' });
    }
  }

  async function update(values: string[], tagIds: string[]) {
    let gradeRequestDto: GradeRequestDto = {
      name: values[0],
      studentGroupIds: [],
      tagIds,
    };
    try {
      await updateGrade(grade.id, gradeRequestDto);
    } catch {
      error(400, { message: 'Could not update grade' });
    }
  }
</script>

<AddResource
  {descriptions}
  {values}
  {create}
  {update}
  createEntity={data.create}
  tags={data.tags}
  entityTags={grade.tags}
/>
