<script lang="ts">
  import ConstraintSignatureComp from '$lib/components/ui/constraintSignature/constraint-signature.svelte';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import Navbar from '$lib/elements/ui/navbar/navbar.svelte';
  import {
    type ConstraintArgumentRequestDto,
    type ConstraintInstanceRequestDto,
    type ConstraintSignature,
    createConstraintInstance,
    getConstraintSignatures,
  } from '$lib/sdk/fetch-client';
  import { deleteInstances, getInstancesPage } from '$lib/utils/resources';

  let reloadTable = false;

  const getConstraints = async () => {
    const { totalElements: size } = await getConstraintSignatures({ page: 0, size: 1 });
    return {
      constraints: await getConstraintSignatures({ page: 0, size }).then(({ content }) => content),
    };
  };

  function createKeys(constraint: ConstraintSignature) {
    let parameterNames = Array.from({ length: constraint.parameters.length }, (_, i) => `name${i}`);
    return ['id'].concat(parameterNames);
  }

  function createColumnNames(constraint: ConstraintSignature) {
    return constraint.parameters.map((parameter) => parameter.parameterName);
  }

  async function addInstance(constraintSignature: ConstraintSignature, selectedIds: Record<string, string>) {
    let argumentRequestDtos: ConstraintArgumentRequestDto[] = [];
    for (let parameter of constraintSignature.parameters) {
      argumentRequestDtos.push({
        argumentId: selectedIds[parameter.parameterName] ?? '',
        parameterName: parameter.parameterName,
      });
    }
    let requestDto: ConstraintInstanceRequestDto = {
      arguments: argumentRequestDtos,
      type: constraintSignature.defaultType,
    };
    await createConstraintInstance(constraintSignature.name, requestDto);
    reloadTable = !reloadTable;
  }
</script>

<Navbar />

<div class="flex flex-col gap-8 p-8">
  {#await getConstraints() then { constraints }}
    {#each constraints || [] as constraint}
      <div class="flex flex-row w-fit min-w-full gap-8 items-top">
        <div class="w-1/4 min-w-min">
          <ConstraintSignatureComp constraintSignature={constraint} {addInstance} />
        </div>
        {#key reloadTable}
          <div class="w-3/4 min-w-min">
            {#await getInstancesPage('', '', 0, 5, constraint.name) then initialData}
              <DataTable
                {initialData}
                columnNames={createColumnNames(constraint)}
                keys={createKeys(constraint)}
                loadPage={getInstancesPage}
                deleteEntries={deleteInstances}
                additionalId={constraint.name}
                sortable={false}
                addButton={false}
                editAvailable={false}
                pageSize={5}
              />
            {/await}
          </div>
        {/key}
      </div>
    {:else}
      <div class="text-3xl font-semibold flex justify-center mt-14">Keine Constraint Signaturen vorhanden.</div>
    {/each}
  {/await}
</div>
