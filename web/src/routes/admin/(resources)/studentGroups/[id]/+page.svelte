<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import {
    createStudentGroup,
    updateStudentGroup,
    type StudentGroupRequestDto,
    type StudentGroupResponseDto,
  } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';

  export let data;
  let studentGroup: StudentGroupResponseDto = data.studentGroup;
  let values: string[] = [studentGroup.name];
  let descriptions: string[] = ['Name:'];

  async function create(values: string[], tagIds: string[], subjectIds?: string[]) {
    let studentGroupRequestDto: StudentGroupRequestDto = {
      name: values[0],
      gradeIds: studentGroup.grades.map((grade) => String(grade.id)),
      studentIds: studentGroup.students.map((student) => student.id),
      tagIds,
      subjectIds: subjectIds || [],
    };
    try {
      await createStudentGroup(studentGroupRequestDto);
    } catch {
      error(400, { message: 'Could not create student group' });
    }
  }

  async function update(values: string[], tagIds: string[], subjectIds?: string[]) {
    let studentGroupRequestDto: StudentGroupRequestDto = {
      name: values[0],
      gradeIds: studentGroup.grades.map((grade) => String(grade.id)),
      studentIds: studentGroup.students.map((student) => student.id),
      tagIds,
      subjectIds: subjectIds || [],
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
  subjects={data.subjects}
  entitySubjectsIds={new Set(studentGroup.subjects.map((subject) => subject.id))}
  entityTags={studentGroup.tags}
/>
