<script lang="ts">
  import { goto } from '$app/navigation';
  import ConstraintSignatureComp from '$lib/components/ui/constraintSignature/constraint-signature.svelte';
  import LinkBar from '$lib/components/ui/link-bar/link-bar.svelte';
  import { Button } from '$lib/elements/ui/button';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import {
    type ConstraintArgumentRequestDto,
    type ConstraintInstanceRequestDto,
    type ConstraintSignature,
    createConstraintInstance,
    getConstraintSignatures,
  } from '$lib/sdk/fetch-client';
  import { deleteInstances, getInstancesPage } from '$lib/utils/resources';
  import { ChevronLeft } from 'lucide-svelte';

  let reloadTable = false;

  const getConstraints = async () => {
    return {
      constraints: await getConstraintSignatures({ page: 0, size: 50 }).then(({ content }) => content),
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

<div class="flex flex-row justify-start bg-foreground md:p-4 text-white">
  <Button on:click={() => goto('./')} variant="secondary" size="icon" class="rounded-full bg-accent mr-6">
    <ChevronLeft class="h-5 w-5 text-white" />
  </Button>
  <LinkBar />
</div>

<div class="p-4">
  {#await getConstraints() then { constraints }}
    {#each constraints || [] as constraint}
      <div class="flex flex-row w-full gap-8 items-top my-5">
        <ConstraintSignatureComp constraintSignature={constraint} {addInstance} />
        {#key reloadTable}
          <div class="w-full">
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
    {/each}
  {/await}
</div>
