<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import {
    createStudentGroup,
    updateStudentGroup,
    type StudentGroup,
    type StudentGroupRequestDto,
    type Tag,
  } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';

  export let data;
  let studentGroup: StudentGroup = data.studentGroup;
  let values: string[] = [studentGroup.name];
  let descriptions: string[] = ['Name:', 'Tags:'];

  async function create(values: string[], tagIds: string[]) {
    let studentGroupRequestDto: StudentGroupRequestDto = {
      name: values[0],
      gradeIds: studentGroup.grades.map((grade) => String(grade.id)),
      studentIds: studentGroup.students.map((student) => student.id),
      tagIds,
    };
    try {
      await createStudentGroup(studentGroupRequestDto);
    } catch {
      error(400, { message: 'Could not create student group' });
    }
  }

  async function update(values: string[], tagIds: string[]) {
    let studentGroupRequestDto: StudentGroupRequestDto = {
      name: values[0],
      gradeIds: studentGroup.grades.map((grade) => String(grade.id)),
      studentIds: studentGroup.students.map((student) => student.id),
      tagIds,
    };
    try {
      await updateStudentGroup(studentGroup.id, studentGroupRequestDto);
    } catch {
      error(400, { message: 'Could not update student group' });
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
  entityTags={studentGroup.tags}
/>
