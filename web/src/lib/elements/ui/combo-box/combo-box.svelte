<script lang="ts" context="module">
  export type ComboBoxItem = { value: string; label: string };
</script>

<script lang="ts">
  import Check from 'lucide-svelte/icons/check';
  import ChevronsUpDown from 'lucide-svelte/icons/chevrons-up-down';
  import { tick } from 'svelte';
  import * as Command from '$lib/elements/ui/command/index.js';
  import * as Popover from '$lib/elements/ui/popover/index.js';
  import { Button } from '$lib/elements/ui/button/index.js';
  import { cn } from '$lib/utils.js';

  export let data: ComboBoxItem[] = [];

  let open = false;
  let value = '';
  let searchedValue: string;
  let selectedValue = '';
  let lastSearchedValued: string;

  export let onSearch = (string: string) => {};

  $: {
    selectedValue = 'Select a framework...';
    if (searchedValue !== lastSearchedValued) {
      onSearch(searchedValue);
      lastSearchedValued = searchedValue;
    }

    if (value) {
      selectedValue = data.find((f) => f.value === value)?.label ?? 'Select a framework...';
    }
  }

  // We want to refocus the trigger button when the user selects
  // an item from the list so users can continue navigating the
  // rest of the form with the keyboard.
  function closeAndFocusTrigger(triggerId: string) {
    open = false;
    tick().then(() => {
      document.getElementById(triggerId)?.focus();
    });
  }
</script>

<Popover.Root bind:open let:ids>
  <Popover.Trigger asChild let:builder>
    <Button
      aria-expanded={open}
      builders={[builder]}
      class="w-[200px] justify-between"
      role="combobox"
      variant="outline"
    >
      {selectedValue}
      <ChevronsUpDown class="ml-2 h-4 w-4 shrink-0 opacity-50" />
    </Button>
  </Popover.Trigger>
  <Popover.Content class="w-[200px] p-0">
    <Command.Root>
      <Command.Input bind:value={searchedValue} placeholder="Search framework..." />
      <Command.Empty>No framework found.</Command.Empty>
      <Command.Group>
        {#each data as framework}
          <Command.Item
            value={framework.value}
            onSelect={(currentValue) => {
              value = currentValue;
              closeAndFocusTrigger(ids.trigger);
            }}
          >
            <Check class={cn('mr-2 h-4 w-4', value !== framework.value && 'text-transparent')} />
            {framework.label}
          </Command.Item>
        {/each}
      </Command.Group>
    </Command.Root>
  </Popover.Content>
</Popover.Root>
