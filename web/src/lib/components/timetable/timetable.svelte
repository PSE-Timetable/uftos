<script lang="ts">
  import Timeslot from '$lib/elements/ui/timeslot/timeslot.svelte';
  import { DateTime } from 'luxon';
  import Lesson from '../lesson/lesson.svelte';
  import { getTimetableMetadata, type TimetableMetadata } from '$lib/sdk/fetch-client';
  import { onMount } from 'svelte';
  import type { LessonItem, TimetableItems } from './timetable';

  type TimetableElement = {
    hasLongBreak: boolean;
    lessons: Array<LessonItem>;
    index: string;
    begin: string;
    end: string;
  };

  export let items: TimetableItems;

  let index = 1;
  let timetableMetadata: TimetableMetadata;
  let currentTime: DateTime;

  let timetableElements: TimetableElement[] = [];

  onMount(async () => {
    timetableMetadata = {
      ...(await getTimetableMetadata()),
      breaks: [
        { afterSlot: 1, length: 5, long: false },
        { afterSlot: 2, length: 15, long: true },
        { afterSlot: 3, length: 5, long: false },
        { afterSlot: 4, length: 15, long: true },
      ],
    };
    currentTime = DateTime.fromFormat(timetableMetadata.startTime, 'H:mm');
    timetableElements = items.map((element) => {
      const begin = currentTime.toFormat('H:mm');
      const nextBreak = timetableMetadata.breaks.find((value) => value.afterSlot === index);

      currentTime = currentTime.plus({ minutes: timetableMetadata.timeslotLength + (nextBreak?.length || 0) });

      return {
        hasLongBreak: !!nextBreak?.long,
        begin,
        end: currentTime.minus({ minutes: nextBreak?.length || 0 }).toFormat('H:mm'),
        index: `${index++}.`,
        lessons: element,
      };
    });
  });
</script>

<div class="inline-grid grid-flow-row gap-2 grid-cols-[max-content,repeat(5,1fr)] bg-white p-4 rounded-2xl w-full">
  {#each timetableElements as row}
    <Timeslot index={row.index} begin={row.begin} end={row.end} />
    {#each row.lessons as lesson}
      {#if lesson === null}
        <div />
      {:else}
        <Lesson
          color={lesson.color || 'orange'}
          leftCorner={lesson.bottomLeft}
          length={lesson.length}
          rightCorner={lesson.bottomRight}
          title={lesson.title}
        />
      {/if}
    {/each}
    {#if row.hasLongBreak}
      <div class="col-span-full" />
    {/if}
  {/each}
</div>
