<script lang="ts">
  import * as Select from '$lib/elements/ui/select';
  import Button from '$lib/elements/ui/button/button.svelte';
  import { CirclePlus, CircleMinus } from 'lucide-svelte';
  import {
    getCurriculum,
    type CurriculumResponseDto,
    updateCurriculum,
    type LessonsCount,
    type GradeResponseDto,
  } from '$lib/sdk/fetch-client';
  import type { Selected } from 'bits-ui';
  import { error } from '@sveltejs/kit';

  export let data;
  let selectGrades = data.grades.map((grade) => ({
    value: grade.id,
    label: grade.name,
  }));
  let selectedGradeId: Selected<string> = data.grades[0]
    ? { value: data.grades[0].id, label: data.grades[0].name }
    : { value: '', label: '' };
  let curriculum: CurriculumResponseDto;
  let lessonsCounts: LessonsCount[] = [];
  let selectedGrade: GradeResponseDto | undefined = data.grades.find((grade) => grade.id === selectedGradeId.value);

  async function getCurriculumFromGrade() {
    selectedGrade = data.grades.find((grade) => grade.id === selectedGradeId.value);
    if (selectedGrade) {
      try {
        curriculum = await getCurriculum(selectedGrade.curriculumId);
        lessonsCounts = curriculum.lessonsCounts;
      } catch {
        error(400, { message: 'could not get curriculum' });
      }
    }
  }
</script>

<div class="flex flex-col">
  <div class="flex flex-row text-xl font-bold m-7 items-baseline">
    <div class="w-28 mr-7">Stufe:</div>
    <Select.Root bind:selected={selectedGradeId}>
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
    {#key selectedGradeId}
      {#await getCurriculumFromGrade() then}
        <div class="flex flex-row m-7 items-center">
          <div class="w-28 text-xl font-bold mr-6">Anzahl an Stunden pro Fach:</div>
          <div class="flex flex-wrap w-[54rem]">
            {#each lessonsCounts as lessonCount}
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
            on:click={async () => {
              if (selectedGrade) {
                let test = {
                  gradeId: selectedGrade.id,
                  lessonsCounts: curriculum.lessonsCounts.map((lessonsCount) => ({
                    count: lessonsCount.count || 0,
                    subjectId: lessonsCount.subject ? lessonsCount.subject.id : '',
                  })),
                  name: selectedGrade.name,
                };
                await updateCurriculum(curriculum.id, test);
              }
            }}
            class="ml-[10.5rem] px-20 py-6 bg-accent text-white"
            variant="secondary">Speichern</Button
          >
        </div>
      {/await}
    {/key}
  {/if}
</div>
