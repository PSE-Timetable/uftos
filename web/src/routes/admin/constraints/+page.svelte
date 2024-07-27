<script lang="ts">
  import { goto } from '$app/navigation';
  import ConstraintSignatureComp from '$lib/components/ui/constraintSignature/constraint-signature.svelte';
  import { Button } from '$lib/elements/ui/button';
  import type { ComboBoxItem } from '$lib/elements/ui/combo-box/combo-box.svelte';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import {
    type ConstraintArgumentRequestDto,
    type ConstraintInstanceRequestDto,
    type ConstraintSignature,
    createConstraintInstance,
    deleteConstraintInstance,
    getConstraintInstances,
    getConstraintSignatures,
    type Pageable,
  } from '$lib/sdk/fetch-client';
  import type { DataItem } from '$lib/utils/resources';
  import { error } from '@sveltejs/kit';
  import { ChevronLeft } from 'lucide-svelte';

  let reloadTable = {};

  const getConstraints = async () => {
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
      let result = await getConstraintInstances(constraintSignatureId, pageable, { argument: filter });
      let dataItems: DataItem[] = result.constraintInstances.map((instance) => {
        let item: DataItem = { id: instance.id };
        for (let i = 0; i < instance.arguments.length; i++) {
          item[`name${i}`] = String(
            result.displayNames.find((item) => item.id === instance.arguments[i].value)?.displayName,
          );
        }
        return item;
      });
      return {
        data: dataItems,
        totalElements: Number(dataItems.length),
      };
    } catch {
      error(400, { message: 'Could not fetch page' });
    }
  }

  function createKeys(constraint: ConstraintSignature) {
    let parameterNames = Array.from({ length: constraint.parameters.length }, (_, i) => `name${i}`);
    return ['id'].concat(parameterNames);
  }

  function createColumnNames(constraint: ConstraintSignature) {
    return constraint.parameters.map((parameter) => parameter.parameterName);
  }

  async function deleteInstance(id: string, signatureId?: string) {
    try {
      await deleteConstraintInstance(signatureId || '', id);
    } catch {
      error(400, { message: `could not delete constraint instance with id ${id}` });
    }
  }

  async function addInstance(constraintSignature: ConstraintSignature, data: Record<string, ComboBoxItem[]>) {
    let argumentRequestDtos: ConstraintArgumentRequestDto[] = [];
    for (let parameter of constraintSignature.parameters) {
      argumentRequestDtos.push({
        argumentId: data[parameter.parameterName][0] ? data[parameter.parameterName][0].value : '',
        parameterName: parameter.parameterName,
      });
    }
    let requestDto: ConstraintInstanceRequestDto = {
      arguments: argumentRequestDtos,
      type: constraintSignature.defaultType,
    };
    await createConstraintInstance(constraintSignature.name, requestDto);
    reloadTable = {}; //each {} is unique, thus reloading component
  }
</script>

<div class="flex flex-row justify-start bg-foreground md:p-4 text-white">
  <Button
    on:click={async () => {
      await goto('./');
    }}
    variant="secondary"
    size="icon"
    class="rounded-full bg-accent mr-6"
  >
    <ChevronLeft class="h-5 w-5 text-white" />
  </Button>
  <h1 class="font-bold text-xl mt-1">Constraints ändern</h1>
</div>

<div class="p-4">
  {#await getConstraints() then { constraints }}
    {#each constraints || [] as constraint}
      <div class="flex flex-row w-full gap-8 items-center">
        <ConstraintSignatureComp constraintSignature={constraint} {addInstance} />
        {#key reloadTable}
          <div class="w-full">
            <DataTable
              columnNames={createColumnNames(constraint)}
              keys={createKeys(constraint)}
              loadPage={getInstancesPage}
              deleteEntry={deleteInstance}
              additionalId={constraint.name}
              sortable={false}
              addButton={false}
              pageSize={5}
            />
          </div>
        {/key}
      </div>
    {/each}
  {/await}
</div>
