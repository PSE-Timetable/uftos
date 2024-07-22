<script lang="ts">
  import ConstraintSignatureComp from '$lib/components/ui/constraintSignature/constraint-signature.svelte';
  import DataTable, { type DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import {
    type ConstraintParameter,
    type ConstraintSignature,
    DefaultType,
    getConstraintInstances,
    getConstraintSignatures,
    getTeachers,
    type GradeResponseDto,
    type Pageable,
    ParameterType,
    type Subject,
    type Tag,
    type Teacher,
  } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';

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

  async function getInstancesPage(pageIndex: number, toSort: string, filter: string, constraintSignatureId?: string) {
    let pageable: Pageable = { page: pageIndex, size: 5, sort: [toSort] };
    if (!constraintSignatureId) {
      throw error(400, { message: 'Could not fetch page' });
    }
    try {
      console.log('test');
      let result = await getConstraintInstances(constraintSignatureId, pageable, { argument: filter });
      let dataItems: DataItem[] = result.content
        ? result.content.map((instance) => {
            let item: DataItem = { id: instance.id };
            for (let i = 0; i < instance.arguments.length; i++) {
              item[`name${i}`] = String(instance.arguments[i].value);
            }
            return item;
          })
        : [];
      return {
        data: dataItems,
        totalElements: Number(result.totalElements),
      };
    } catch {
      error(400, { message: 'Could not fetch page' });
    }
  }

  function createKeys(constraint: ConstraintSignature) {
    let parameterNames = Array.from({ length: constraint.parameters.length }, (_, i) => `name${i}`);
    console.log(['id'].concat(parameterNames));
    return ['id'].concat(parameterNames);
  }

  function createColumnNames(constraint: ConstraintSignature) {
    return constraint.parameters.map((parameter) => parameter.parameterName);
  }

  async function deleteInstance(id: string) {
    try {
      await deleteInstance(id);
    } catch {
      error(400, { message: `could not delete room with id ${id}` });
    }
  }
</script>

{#await getConstraints() then { constraints }}
  {#await getResources() then { teachers, grades, subjects, constraintSignature }}
    {#each constraints || [] as constraint}
      {console.log(constraint)}
      <ConstraintSignatureComp {teachers} {grades} {subjects} constraintSignature={constraint} />
      <DataTable
        columnNames={createColumnNames(constraint)}
        keys={createKeys(constraint)}
        loadPage={getInstancesPage}
        deleteEntry={deleteInstance}
        additionalId={constraint.name}
      />
    {/each}
  {/await}
{/await}
