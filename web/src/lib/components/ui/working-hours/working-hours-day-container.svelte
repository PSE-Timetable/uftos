<script lang="ts">
  import * as Collapsible from '$lib/elements/ui/collapsible';
  import { Switch } from '$lib/elements/ui/switch/index.js';
  import { ChevronDown } from 'lucide-svelte';
  import { Button } from '$lib/elements/ui/button/index.js';
  import WorkingHoursItem from './working-hours-item.svelte';
  import { WorkingHoursEnum, WeekdDay, type TimeInterval } from '$lib/utils';

  export let weekDay: WeekdDay = WeekdDay.monday;

  export let timeIntervals: TimeInterval[] = [
    { start: WorkingHoursEnum['8:00'], end: WorkingHoursEnum['10:00'] },
    { start: WorkingHoursEnum['14:00'], end: WorkingHoursEnum['17:00'] },
  ];
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
    {#each timeIntervals as timeInterval}
      <WorkingHoursItem
        onItemAdd={(interval) => {
          timeIntervals.push(interval);
          // Don't ask, I'm not crazy. It just works. Svelte magic
          timeIntervals = timeIntervals;
        }}
        onItemDelete={(interval) => {
          // I'm not using filter because if there were multiple intervals with the same start and end, it would remove them all
          const indexToRemove = timeIntervals.findIndex((ti) => ti.start === interval.start && ti.end === interval.end);
          if (indexToRemove !== -1) {
            // Remove the item at the found index
            timeIntervals.splice(indexToRemove, 1);
          }
          // Again, svelte magic
          timeIntervals = timeIntervals;
        }}
        start={timeInterval.start}
        end={timeInterval.end}
      />
    {/each}
  </Collapsible.Content>
</Collapsible.Root>
