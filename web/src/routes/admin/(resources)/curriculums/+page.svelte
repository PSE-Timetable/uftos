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

<div class="grid grid-cols-[max-content,1fr] p-4 gap-8">
  <div class="text-lg font-bold">Stufe:</div>
  <Select.Root bind:selected={selectedGradeId}>
    <Select.Trigger class="shadow-custom w-[15vw]">
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

  {#if selectedGrade}
    {#key selectedGradeId}
      {#await getCurriculumFromGrade() then}
        <div class="w-[10vw] text-lg font-bold">Anzahl an Stunden pro Fach:</div>
        <div class="grid grid-cols-4 w-fit gap-8">
          {#each lessonsCounts as lessonCount}
            <div class="bg-white shadow-custom p-4 w-[15vw] break-words rounded-md flex flex-col gap-2 justify-between">
              <div class="text-center m-1">{lessonCount.subject?.name}</div>
              <div class="flex-row flex justify-center gap-2">
                <button
                  type="button"
                  on:click={() => {
                    if (lessonCount.count) {
                      lessonCount.count--;
                    } else {
                      lessonCount.count = 0;
                    }
                  }}
                  ><CircleMinus />
                </button>
                <div class="mx-3">{lessonCount.count}</div>
                <button
                  type="button"
                  on:click={() => {
                    if (lessonCount.count) {
                      lessonCount.count++;
                    } else {
                      lessonCount.count = 1;
                    }
                  }}
                  ><CirclePlus />
                </button>
              </div>
            </div>
          {/each}
        </div>
      {/await}
    {/key}
  {/if}

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
    class="col-start-2 w-[15vw] p-8 text-lg bg-accent text-white"
    variant="secondary">Speichern</Button
  >
</div>
