<script lang="ts">
  import { getTimetableMetadata } from '$lib/sdk/fetch-client';
  import ChevronLeft from 'lucide-svelte/icons/chevron-left';
  import { Input } from '$lib/elements/ui/input/index.js';
  import { Button } from '$lib/elements/ui/button';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';
  import { onMount } from 'svelte';

  let changed: boolean = false;
  let timeslots = 3;
  let startTime = '07:45';
  let pauseLength = '00:45';
  let time: Date = new Date(startTime);

  enum Type {
    TIMESLOT,
    PAUSE,
  }

  let timeslotList: Type[];

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
    if (value !== timeslots) {
      timeslots = value;
      changed = true;
    }
  }

  function updateStartTime(event) {
    if (event.target.value !== startTime) {
      startTime = event.target.value;
      time = convertTimeStringToDate(startTime);
      changed = true;
    }
  }

  function updatePauseLength(event) {
    if (event.target.value !== startTime) {
      pauseLength = event.target.value;
      changed = true;
    }
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
  <div class="flex flex-col gap-4 p-4">
    <div class="flex flex-row gap-4 items-center text-md">
      <label for="slot" class="font-bold">Timeslots pro Tag:</label>
      <Input
        background="true"
        id="slot"
        type="number"
        value={timeslots}
        on:input={updateTimeslots}
        min="0"
        step="1"
        placeholder="Slot"
        class="w-min"
      />
    </div>
    <div class="flex flex-row gap-4 items-center text-md">
      <label for="start_time" class="font-bold">Anfangsuhrzeit:</label>
      <Input
        background="true"
        value={startTime}
        on:input={updateStartTime}
        id="start_time"
        type="time"
        placeholder="Slot"
        class="w-min"
      />
    </div>
    <div class="flex flex-row gap-4 items-center text-md">
      <label for="pause_length" class="font-bold">Pausenlänge:</label>
      <Input
        background="true"
        value={pauseLength}
        on:input={updatePauseLength}
        id="pause_length"
        type="time"
        placeholder="Slot"
        class="w-min"
      />
    </div>
    <div class="bg-white rounded-md p-4 gap-2 flex flex-col">
      {#each Array(timeslots) as _, index}
        <div class="flex flex-row gap-2">
          <p>
            timeslot {index} von {formatDateToTimeString(
              addMinutesToDate(time, 50 * index),
            )} bis {formatDateToTimeString(
              addMinutesToDate(time, 50 * ++index),
            )}
          </p>
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <g clip-path="url(#clip0_150_8642)">
              <circle cx="12" cy="12" r="11" stroke="#2B6E89" stroke-width="2" />
              <rect x="11" y="6" width="2" height="12" rx="1" fill="#2B6E89" />
              <rect x="6" y="13" width="2" height="12" rx="1" transform="rotate(-90 6 13)" fill="#2B6E89" />
            </g>
            <defs>
              <clipPath id="clip0_150_8642">
                <rect width="24" height="24" fill="white" />
              </clipPath>
            </defs>
          </svg>
        </div>
      {/each}
    </div>
  </div>
{/await}
