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
  export let selectedId:string;

  let open:boolean = false;
  let value = '';
  let searchedValue: string;
  let selectedValue = 'Resource auswählen';
  let lastSearchedValued: string;

  export let onSearch: (string: string) => void;

  $: searchedValue,
    data,
    (() => {
      if (searchedValue !== lastSearchedValued) {
        onSearch(searchedValue);
        lastSearchedValued = searchedValue;
      }

      if (value) {
        let selected = data.find((f) => f.value === value);
        selectedValue = selected?.label ?? 'Resource auswählen';
        selectedId = selected?.value ?? '';
      }
    })();

  // We want to refocus the trigger button when the user selects
  // an item from the list so users can continue navigating the
  // rest of the form with the keyboard.
  async function closeAndFocusTrigger(triggerId: string) {
    open = false;
    await tick();
    document.getElementById(triggerId)?.focus();
  }
</script>

<Popover.Root bind:open let:ids>
  <Popover.Trigger asChild let:builder>
    <Button
      aria-expanded={open}
      builders={[builder]}
      class="w-[200px] justify-between bg-white"
      role="combobox"
      variant="outline"
    >
      {selectedValue}
      <ChevronsUpDown class="ml-2 h-4 w-4 shrink-0 opacity-50" />
    </Button>
  </Popover.Trigger>
  <Popover.Content class="w-[200px] p-0">
    <Command.Root shouldFilter={false}>
      <Command.Input bind:value={searchedValue} placeholder="Suche Resource" />
      <Command.Empty>Keine Resource gefunden</Command.Empty>
      {#each data as resource (resource.value)}
        <Command.Item
          value={resource.value}
          onSelect={async (currentValue) => {
            value = currentValue;
            await closeAndFocusTrigger(ids.trigger);
          }}
        >
          <Check class={cn('mr-2 h-4 w-4', value !== resource.value && 'text-transparent')} />
          {resource.label}
        </Command.Item>
      {/each}
    </Command.Root>
  </Popover.Content>
</Popover.Root>
