<script lang="ts">
  import ConstraintSignatureComp from '$lib/components/ui/constraintSignature/constraint-signature.svelte';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import {
    type ConstraintParameter,
    type ConstraintSignature,
    DefaultType,
    getConstraintSignatures,
    getTeachers,
    type GradeResponseDto,
    ParameterType,
    type Subject,
    type Tag,
    type Teacher,
  } from '$lib/sdk/fetch-client';

  const constraintParameters: ConstraintParameter[] = [
    {
      id: '123',
      parameterName: 'Klasse X',
      parameterType: ParameterType.Grade,
    },
    {
      id: '456',
      parameterName: 'Lehrer Y',
      parameterType: ParameterType.Teacher,
    },
    {
      id: '789',
      parameterName: 'Fach Y',
      parameterType: ParameterType.Subject,
    },
  ];

  const constraintSignature: ConstraintSignature = {
    parameters: constraintParameters,
    description: 'Klasse X hat Lehrer Y im Fach Z',
    name: 'test_constraint',
    defaultType: DefaultType.HardPenalize,
  };

  const tag: Tag = {
    id: '123',
    name: 'qwerty',
  };

  const subjects: Subject[] = [
    {
      id: '123',
      name: 'Deutsch',
      tags: [tag],
    },
    {
      id: '456',
      name: 'English',
      tags: [tag],
    },
  ];

  const grades: GradeResponseDto[] = [
    {
      id: '123',
      name: '5.',
      studentIds: [],
      studentGroupIds: [],
      tags: [],
    },
    {
      id: '456',
      name: '6.',
      studentIds: [],
      studentGroupIds: [],
      tags: [],
    },
  ];

  let teachers: Teacher[] = [];

  const getResources = async () => {
    // const response = await getTeachers({ page: 0, size: 2 });
    // etc
    return {
      teachers: await getTeachers({ page: 0, size: 2 }).then(({ content }) => content),
      grades,
      subjects,
      constraintSignature,
    };
  };

  const getConstraints = async () => {
    // const response = await getTeachers({ page: 0, size: 2 });
    // etc
    return {
      constraints: await getConstraintSignatures({ page: 0, size: 50 }).then(({ content }) => content),
    };
  };
</script>

{#await getConstraints() then { constraints }}
  {#await getResources() then { teachers, grades, subjects, constraintSignature }}
    {#each constraints || [] as constraint}
      {console.log(constraint)}
      <ConstraintSignatureComp {teachers} {grades} {subjects} constraintSignature={constraint} />
    {/each}
  {/await}
{/await}
