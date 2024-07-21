<script lang="ts">
  import * as Select from '$lib/elements/ui/select';
  import { Button } from '$lib/elements/ui/button/index';
  import { CirclePlus, CircleMinus } from 'lucide-svelte';
  import type { LessonsCount, Subject } from '$lib/sdk/fetch-client';
  const grades = [
    { value: 'grade1', label: '5' },
    { value: 'grade2', label: '6' },
    { value: 'grade3', label: '7' },
    { value: 'grade4', label: '8' },
    { value: 'grade5', label: '8-Spanisch' },
  ];

  let subjects: Subject[] = [{ id: 'subject1', name: 'Deutsch', tags: [] }];
  let lessonsCount: LessonsCount[] = [
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
    { count: 1, id: 'count1', subject: subjects[0] },
  ];
</script>

<div class="flex flex-col">
  <div class="flex flex-row text-xl font-bold m-7 items-baseline">
    <div class="w-28 mr-7">Stufe:</div>
    <Select.Root>
      <Select.Trigger class="w-[180px]">
        <Select.Value placeholder="Stufe auswählen" />
      </Select.Trigger>
      <Select.Content>
        <Select.Group>
          {#each grades as grade}
            <Select.Item value={grade.value} label={grade.label}>{grade.label}</Select.Item>
          {/each}
        </Select.Group>
      </Select.Content>
    </Select.Root>
  </div>
  <div class="flex flex-row m-7 items-center">
    <div class="w-28 text-xl font-bold mr-6">Anzahl an Stunden pro Fach:</div>
    <div class="flex flex-wrap w-[54rem]">
      {#each lessonsCount as lessonCount}
        <div class="bg-white rounded-md w-36 h-16 flex flex-col justify-items-center m-1">
          <div class="text-center m-1">{lessonCount.subject?.name}</div>
          <div class="flex-row flex justify-center">
            <button on:click={() => (lessonCount.count ? lessonCount.count-- : (lessonCount.count = 0))}
              ><CircleMinus />
            </button>
            <div class="mx-3">{lessonCount.count}</div>
            <button on:click={() => (lessonCount.count ? lessonCount.count++ : (lessonCount.count = 1))}
              ><CirclePlus />
            </button>
          </div>
        </div>
      {/each}
    </div>
  </div>
  <div>
    <Button class="ml-[10.5rem] px-20 py-6 bg-accent text-white" variant="secondary">Speichern</Button>
  </div>
</div>
