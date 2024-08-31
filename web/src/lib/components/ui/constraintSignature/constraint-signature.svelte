<script lang="ts">
  import { type ConstraintSignature, type Pageable, ParameterType, type Sort } from '$lib/sdk/fetch-client';
  import { Button } from '$lib/elements/ui/button';
  import ComboBox from '$lib/elements/ui/combo-box/combo-box.svelte';
  import {
    getGradesItems,
    getRoomsItems,
    getStudentGroupsItems,
    getStudentsItems,
    getSubjectsItems,
    getTagsItems,
    getTeachersItems,
    getTimeslotsItems,
  } from '$lib/utils/combobox-items';
  import type { ComboBoxItem } from '$lib/elements/ui/combo-box/combo-box';

  export let constraintSignature: ConstraintSignature;
  export let addInstance: (
    constraintSignature: ConstraintSignature,
    selectedIds: Record<string, string>,
  ) => Promise<void>;

  let data: Record<string, ComboBoxItem[]> = {};
  let selectedIds: Record<string, string> = {};
  let selectedLabels: Record<string, string> = {};

  async function updateItems(value: string, name: string, parameterType: ParameterType) {
    const page: Pageable = { page: 0, size: 40 };
    const sort: Sort = { sort: ['name,asc'] };
    try {
      switch (parameterType) {
        case ParameterType.Grade: {
          data[name] = await getGradesItems(sort, { search: value });
          break;
        }
        case ParameterType.Subject: {
          data[name] = await getSubjectsItems(sort, { search: value });
          break;
        }
        case ParameterType.Room: {
          data[name] = await getRoomsItems(page, { search: value });
          break;
        }
        case ParameterType.StudentGroup: {
          data[name] = await getStudentGroupsItems(page, { search: value });
          break;
        }
        case ParameterType.Student: {
          data[name] = await getStudentsItems(page, { search: value });
          break;
        }
        case ParameterType.Tag: {
          data[name] = await getTagsItems(sort, { search: value });
          break;
        }
        case ParameterType.Teacher: {
          data[name] = await getTeachersItems(page, { search: value });
          break;
        }
        case ParameterType.Timeslot: {
          data[name] = await getTimeslotsItems();
          break;
        }
      }
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  }
  const initData = async () => {
    await Promise.all(
      constraintSignature.parameters.map(({ parameterName, parameterType }) =>
        updateItems('', parameterName, parameterType),
      ),
    );
  };
</script>

<div class="flex flex-col gap-8 bg-primary w-full h-fit p-6 rounded-md text-white">
  <p class="font-bold text-md">
    {constraintSignature.description.replaceAll(
      /{(?<paramName>.*?)}/gm,
      (match, paramName) => selectedLabels[paramName] ?? match,
    )}
  </p>

  {#await initData() then}
    {#each constraintSignature.parameters as parameter (parameter.id)}
      <div class="flex flex-row items-center justify-between w-full gap-8">
        <p>{parameter.parameterName}</p>

        <div class="text-primary">
          <ComboBox
            onSearch={(value) => updateItems(value, parameter.parameterName, parameter.parameterType)}
            data={data[parameter.parameterName]}
            selectedId={selectedIds[parameter.parameterName]}
            onSelectChange={(value, label) => {
              selectedIds[parameter.parameterName] = value;
              selectedLabels[parameter.parameterName] = label;
            }}
          />
        </div>
      </div>
    {/each}
  {/await}

  <Button
    variant="outline"
    class="bg-accent border-0 text-md text-white py-6"
    on:click={() => addInstance(constraintSignature, selectedIds)}>Hinzufügen</Button
  >
</div>
