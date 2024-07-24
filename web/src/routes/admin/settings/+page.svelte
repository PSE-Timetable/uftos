<script lang="ts">
  import { getTimetableMetadata, setTimetableMetadata, type TimetableMetadata } from '$lib/sdk/fetch-client';
  import { CirclePlus, ChevronLeft, Trash2 } from 'lucide-svelte/icons';
  import { Input } from '$lib/elements/ui/input/index.js';
  import { toast } from 'svelte-sonner';
  import { Button } from '$lib/elements/ui/button';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';

  export let data;
  let changed: boolean = false;

  enum Type {
    TIMESLOT,
    PAUSE,
  }

  let metaData: TimetableMetadata = data.metadata;

  let timeslotList: { from: string; to: string; relativeIndex: number; type: Type }[] = generateTimetable(metaData);

  $: timeslotList = generateTimetable(metaData);

  function addBreakAndUpdate(metadata: TimetableMetadata, afterSlot: number, length: number, long?: boolean) {
    metadata.breaks.push({ afterSlot, length, long });
    metadata.breaks.sort((a, b) => a.afterSlot - b.afterSlot);
    metaData = metadata;
  }

  function removeBreakAndUpdate(metadata: TimetableMetadata, index: number) {
    metadata.breaks.splice(index, 1);
    metadata.breaks.sort((a, b) => a.afterSlot - b.afterSlot);
    metaData = metadata;
  }

  function setPauseLength(metadata: TimetableMetadata, index: number, length: string) {
    metadata.breaks[index].length = parseInt(length, 10);
    metaData = metadata;
  }

  function generateTimetable(metadata: TimetableMetadata) {
    metadata.breaks.sort((a, b) => a.afterSlot - b.afterSlot);
    const { breaks, startTime, timeslotLength } = metadata;

    let timeslotList: { from: string; to: string; relativeIndex: number; type: Type }[] = [];

    let currentStartTime = convertTimeStringToDate(startTime);
    let breakIndex = 0;

    for (let i = 0; i < metadata.timeslotsAmount; i++) {
      // Add the regular timeslot
      const endTime = addMinutesToDate(currentStartTime, timeslotLength);

      timeslotList.push({
        from: formatDateToTimeString(currentStartTime),
        to: formatDateToTimeString(endTime),
        relativeIndex: i,
        type: Type.TIMESLOT,
      });
      currentStartTime = endTime;

      // Add a pause if it is scheduled after the current timeslot
      if (breakIndex < breaks.length && breaks[breakIndex].afterSlot === i) {
        const breakLength = breaks[breakIndex].length;
        const breakEndTime = addMinutesToDate(currentStartTime, breaks[breakIndex].length);

        timeslotList.push({
          from: formatDateToTimeString(currentStartTime),
          to: formatDateToTimeString(breakEndTime),
          relativeIndex: breakIndex,
          type: Type.PAUSE,
        });

        currentStartTime = breakEndTime;
        breakIndex++;
      }
    }

    return timeslotList;
  }

  async function saveMetadata() {
    await setTimetableMetadata(metaData);
    toast.success('Erfolgreich', {
      description: 'Die Einstellungen wurden erfolgreich gespeichert!',
    });
  }

  function convertTimeStringToDate(timeString: string): Date {
    const [hours, minutes] = timeString.split(':').map(Number);
    const date = new Date();
    date.setHours(hours);
    date.setMinutes(minutes);
    return date;
  }

  function addMinutesToDate(date: Date, minutes: number): Date {
    const newDate = new Date(date);
    newDate.setMinutes(newDate.getMinutes() + minutes);
    return newDate;
  }

  function formatDateToTimeString(date: Date): string {
    const hours = date.getHours();
    const minutes = date.getMinutes();

    const formattedHours = hours.toString().padStart(2, '0');
    const formattedMinutes = minutes.toString().padStart(2, '0');

    return `${formattedHours}:${formattedMinutes}`;
  }

  function updateTimeslots(event) {
    const value = parseInt(event.target.value, 10);
    if (value !== metaData.timeslotsAmount) {
      changed = true;
      metaData.timeslotsAmount = value;
      metaData.breaks = metaData.breaks.filter((b) => b.afterSlot < metaData.timeslots);
    }
  }

  function updateStartTime(event) {
    if (event.target.value !== metaData.startTime) {
      changed = true;
      metaData.startTime = event.target.value;
    }
  }

  function updateTimeSlotLength(event) {
    if (event.target.value !== metaData.timeslotLength) {
      metaData.timeslotLength = parseInt(event.target.value);
      changed = true;
    }
  }

  function hasTimeslotBreak(index: number): boolean {
    for (let i = 0; i < metaData.breaks.length; i++) {
      const break1 = metaData.breaks[i];
      if (break1.afterSlot === index) {
        return true;
      }
    }
    return false;
  }
</script>

<div class="flex flex-row justify-start bg-foreground md:p-4 text-white">
  <Button
    on:click={async () => {
      await goto('/admin');
    }}
    variant="secondary"
    size="icon"
    class="rounded-full bg-accent mr-6"
  >
    <ChevronLeft class="h-5 w-5 text-white" />
  </Button>
  <h1 class="font-bold text-xl mt-1">{$page.data['meta']['title']}</h1>
</div>

{#await getTimetableMetadata() then metadata}
  <div class="flex flex-row gap-8 p-4">
    <div class="grid grid-cols-2 auto-rows-auto h-fit gap-4">
      <label for="slot" class="font-bold fit-content">Timeslots pro Tag:</label>
      <Input
        background="true"
        id="slot"
        type="number"
        value={metaData.timeslotsAmount}
        on:input={updateTimeslots}
        min="0"
        step="1"
        placeholder="Anzahl"
      />
      <label for="slot_length" class="font-bold">Timeslots Länge:</label>
      <Input
        background="true"
        id="slot_length"
        type="number"
        value={metaData.timeslotLength}
        on:input={updateTimeSlotLength}
        min="0"
        step="1"
        placeholder="Länge"
      />
      <label for="start_time" class="font-bold">Anfangsuhrzeit:</label>
      <Input
        background="true"
        value={metaData.startTime}
        on:input={updateStartTime}
        id="start_time"
        type="time"
        placeholder="07:45"
      />
      <div></div>
      <Button class="bg-accent text-white py-8 col-span-2" on:click={saveMetadata}>Speichern</Button>
    </div>
    <div class="bg-white rounded-md p-8 gap-6 flex flex-col w-1/2 min-w-[fit-content]">
      <p class="font-bold text-lg">Timeslots</p>
      {#each timeslotList as timeslot, i}
        <div class="flex flex-row gap-2 items-center">
          {#if timeslot.type === Type.TIMESLOT}
            <p>
              {timeslot.relativeIndex + 1}. Stunde von {timeslot.from} bis {timeslot.to}
            </p>
            <button
              disabled={hasTimeslotBreak(timeslot.relativeIndex)}
              type="button"
              on:click={() => addBreakAndUpdate(metaData, timeslot.relativeIndex, 5, false)}
            >
              <div class="stroke-accent">
                <CirclePlus class={hasTimeslotBreak(timeslot.relativeIndex) ? 'stroke-gray-400' : ''} />
              </div>
            </button>
          {:else}
            <p class="invisible">...</p>
            <p>
              Pause von {timeslot.from} bis {timeslot.to}
            </p>
            <Input
              background="true"
              id="pause_length"
              type="number"
              value={metaData.breaks[timeslot.relativeIndex].length}
              on:input={(e) => setPauseLength(metaData, timeslot.relativeIndex, e.target.value)}
              min="0"
              step="1"
              placeholder="Länge"
              class="w-min"
            />
            <button type="button" on:click={() => removeBreakAndUpdate(metaData, timeslot.relativeIndex)}>
              <Trash2 />
            </button>
          {/if}
        </div>
      {/each}
    </div>
  </div>
{/await}
