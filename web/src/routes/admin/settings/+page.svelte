<script lang="ts">
  import { getTimetableMetadata } from '$lib/sdk/fetch-client';
  import ChevronLeft from 'lucide-svelte/icons/chevron-left';
  import { Input } from '$lib/elements/ui/input/index.js';
  import { Button } from '$lib/elements/ui/button';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';
  import WorkingHoursDayContainer from '$lib/components/working-hours/working-hours-day-container.svelte';

  let changed: boolean = false;
  let timeslots = 3;
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
        bind:value={timeslots}
        on:input={(event) => {
          changed = true;
        }}
        min="0"
        step="1"
        placeholder="Slot"
        class="w-min"
      />
    </div>
    <div class="flex flex-row gap-4 items-center text-md">
      <label for="time" class="font-bold">Anfangsuhrzeit:</label>
      <Input background="true" value={metadata.startTime} id="time" type="time" placeholder="Slot" class="w-min" />
    </div>
    {#each { length: timeslots } as index (index)}
      <WorkingHoursDayContainer></WorkingHoursDayContainer>
    {/each}
  </div>
{/await}
