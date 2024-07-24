<script lang="ts">
  import * as Select from '$lib/elements/ui/select';
  import Button from '$lib/elements/ui/button/button.svelte';
  import { CirclePlus, CircleMinus } from 'lucide-svelte';
  import {
    createCurriculum,
    getCurriculum,
    type CurriculumResponseDto,
    type GradeResponseDto,
    type CurriculumRequestDto,
    updateCurriculum,
  } from '$lib/sdk/fetch-client';
  import type { Selected } from 'bits-ui';
  import { error } from '@sveltejs/kit';

  export let data;
  const subjects = data.subjects;
  let selectGrades = data.grades.map((grade) => ({
    value: grade,
    label: grade.name,
  }));
  let selectedGrade: Selected<GradeResponseDto> = selectGrades[0];
  let curriculum: CurriculumResponseDto;
  let changed = false;

  async function getCurriculumFromGrade() {
    changed = false;
    if (selectedGrade && selectedGrade.value.curriculumId !== '') {
      try {
        curriculum = await getCurriculum(selectedGrade.value.curriculumId);
        selectedGrade.value.curriculumId = curriculum.id;
      } catch {
        error(400, { message: 'could not get curriculum' });
      }
    } else {
      let CurriculumRequestDto: CurriculumRequestDto = {
        gradeId: selectedGrade.value.id,
        lessonsCounts: subjects.map((subject) => ({ count: 0, subjectId: subject.id })),
        name: selectedGrade.value.name,
      };
      let result = await createCurriculum(CurriculumRequestDto);
      selectedGrade.value.id = result.id;
      curriculum = result;
    }
  }
</script>

<div class="flex flex-col">
  <div class="flex flex-row text-xl font-bold m-7 items-baseline">
    <div class="w-28 mr-7">Stufe:</div>
    <Select.Root
      bind:selected={selectedGrade}
      onSelectedChange={async () => {
        await getCurriculumFromGrade();
      }}
    >
      <Select.Trigger class="w-[180px]">
        <Select.Value placeholder="Stufe auswählen" />
      </Select.Trigger>
      <Select.Content>
        <Select.Group>
          {#each selectGrades as grade}
            <Select.Item value={grade.value} label={grade.label}>{grade.label}</Select.Item>
          {/each}
        </Select.Group>
      </Select.Content>
    </Select.Root>
  </div>
  {#if selectedGrade}
    {#await getCurriculumFromGrade() then}
      <div class="flex flex-row m-7 items-center">
        <div class="w-28 text-xl font-bold mr-6">Anzahl an Stunden pro Fach:</div>
        <div class="flex flex-wrap w-[54rem]">
          {#each curriculum.lessonsCounts as lessonCount}
            <div class="bg-white rounded-md w-36 h-16 flex flex-col justify-items-center m-1">
              <div class="text-center m-1">{lessonCount.subject?.name}</div>
              <div class="flex-row flex justify-center">
                <button
                  type="button"
                  on:click={() => {
                    lessonCount.count ? lessonCount.count-- : (lessonCount.count = 0);
                  }}
                  ><CircleMinus />
                </button>
                <div class="mx-3">{lessonCount.count}</div>
                <button
                  type="button"
                  on:click={() => {
                    lessonCount.count ? lessonCount.count++ : (lessonCount.count = 1);
                  }}
                  ><CirclePlus />
                </button>
              </div>
            </div>
          {/each}
        </div>
      </div>
      <div>
        <Button
          on:click={() => {
            console.log(selectedGrade.value);
            console.log(curriculum);
            let test = {
              gradeId: selectedGrade.value.id,
              lessonsCounts: curriculum.lessonsCounts.map((lessonsCount) => ({
                count: lessonsCount.count || 0,
                subjectId: lessonsCount.subject ? lessonsCount.subject.id : '',
              })),
              name: selectedGrade.value.name,
            };
            console.log(test);
            updateCurriculum(curriculum.id, test);
          }}
          class="ml-[10.5rem] px-20 py-6 bg-accent text-white"
          variant="secondary">Speichern</Button
        >
      </div>
    {/await}
  {/if}
</div>
