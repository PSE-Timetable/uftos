<script lang="ts">
  import * as Collapsible from '$lib/elements/ui/collapsible';
  import { Switch } from '$lib/elements/ui/switch/index.js';
  import { ChevronDown } from 'lucide-svelte';
  import { Button } from '$lib/elements/ui/button/index.js';
  import WorkingHoursItem from './working-hours-item.svelte';
  import { WeekDay, WorkingHours, type TimeInterval } from '$lib/utils';

  export let weekDay: WeekDay = WeekDay.MONDAY;

  export let timeIntervals: TimeInterval[] = [
    { start: WorkingHours[0], end: WorkingHours[4] },
    { start: WorkingHours[12], end: WorkingHours[18] },
  ];

  const handleAddItem = () => {
    timeIntervals.push({ start: WorkingHours[0], end: WorkingHours[4] });
    timeIntervals = timeIntervals;
  };

  const handleDeleteItem = (index: number) => {
    timeIntervals.splice(index, 1);
    timeIntervals = timeIntervals;
  };
</script>

<div class="flex items-center space-x-2"></div>
<Collapsible.Root class="w-[350px] p-1 bg-white rounded-lg">
  <div class="flex items-center justify-between space-x-4 p-1">
    <div class="flex flex-row gap-2 items-center">
      <Switch id="airplane-mode" />
      <h4 class="text-sm font-semibold">{weekDay}</h4>
    </div>
    <Collapsible.Trigger asChild let:builder>
      <Button builders={[builder]} class="w-9 p-0" size="sm" variant="ghost">
        <ChevronDown class="h-6 w-6" />
        <span class="sr-only">Toggle</span>
      </Button>
    </Collapsible.Trigger>
  </div>
  <Collapsible.Content class="space-y-2">
    {#each timeIntervals as timeInterval, i}
      <WorkingHoursItem onItemAdd={handleAddItem} onItemDelete={() => handleDeleteItem(i)} {timeInterval} />
    {:else}
      <div class="flex flex-row text-sm pr-2 justify-end">
        <button type="button" on:click={handleAddItem}>
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
        </button>
      </div>
    {/each}
  </Collapsible.Content>
</Collapsible.Root>
