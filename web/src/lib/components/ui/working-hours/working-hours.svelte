<script lang="ts">
  import * as Collapsible from '$lib/elements/ui/collapsible';
  import { Switch } from '$lib/elements/ui/switch/index.js';
  import { ChevronDown } from 'lucide-svelte';
  import { Button } from '$lib/elements/ui/button/index.js';
  import * as Select from '$lib/elements/ui/select';

  let selected = { value: '8:00', label: '8:00' };

  const timesOfDay = [
    { value: '8:00', label: '8:00' },
    { value: '8:30', label: '8:30' },
    { value: '9:00', label: '9:00' },
    { value: '9:30', label: '9:30' },
    { value: '10:00', label: '10:00' },
  ];
</script>
<div class="flex items-center space-x-2">

</div>
<Collapsible.Root class="w-[350px] p-1 bg-white rounded-lg">
  <div class="flex items-center justify-between space-x-4 p-1">
    <div class="flex flex-row gap-2 items-center">
      <Switch id="airplane-mode" />
      <h4 class="text-sm font-semibold">Montag</h4>
    </div>
    <Collapsible.Trigger asChild let:builder>
      <Button builders={[builder]} class="w-9 p-0" size="sm" variant="ghost">
        <ChevronDown class="h-6 w-6" />
        <span class="sr-only">Toggle</span>
      </Button>
    </Collapsible.Trigger>
  </div>
  <Collapsible.Content class="space-y-2">
    <div class="flex flex-row text-sm items-center gap-2">

      <Select.Root bind:selected portal={null}>
        <Select.Trigger>
          <Select.Value placeholder="Uhrzeit..." />
        </Select.Trigger>
        <Select.Content>
          <Select.Group>
            {#each timesOfDay as classItem}
              <Select.Item value={classItem.value} label={classItem.label}>
                {classItem.label}
              </Select.Item>
            {/each}
          </Select.Group>
        </Select.Content>
        <Select.Input name="chosenResource" />
      </Select.Root>

      bis

      <Select.Root bind:selected portal={null}>
        <Select.Trigger>
          <Select.Value placeholder="Uhrzeit..." />
        </Select.Trigger>
        <Select.Content>
          <Select.Group>
            {#each timesOfDay as classItem}
              <Select.Item value={classItem.value} label={classItem.label}>
                {classItem.label}
              </Select.Item>
            {/each}
          </Select.Group>
        </Select.Content>
        <Select.Input name="chosenResource" />
      </Select.Root>

    </div>
  </Collapsible.Content>
</Collapsible.Root>
