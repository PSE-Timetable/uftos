<script lang="ts">
  import Check from 'lucide-svelte/icons/check';
  import ChevronsUpDown from 'lucide-svelte/icons/chevrons-up-down';
  import { tick } from 'svelte';
  import * as Command from '$lib/elements/ui/command/index.js';
  import * as Popover from '$lib/elements/ui/popover/index.js';
  import { Button } from '$lib/elements/ui/button/index.js';
  import { cn } from '$lib/utils.js';
  import type { ComboBoxItem } from './combo-box';

  export let data: ComboBoxItem[] = [];
  export let selectedId: string;
  export let shadow: boolean = false;
  export let placeholder: string = 'Ressource auswählen';
  export let searchPlaceholder: string = 'Suche Ressource';
  export let notFoundPlaceholder: string = 'Keine Ressource gefunden';

  let open: boolean = false;
  let searchedValue: string;
  let selectedValue = data.find((f) => f.value === selectedId)?.label ?? placeholder;
  let lastSearchedValued: string;

  export let onSearch: (value: string) => void;
  export let onSelectChange: (value: string, label: string) => void = () => {};

  $: searchedValue,
    data,
    (() => {
      if (searchedValue !== lastSearchedValued) {
        onSearch(searchedValue);
        lastSearchedValued = searchedValue;
      }
    })();

  const handleSelect = async (value: string, ids: { content: string; trigger: string }) => {
    let selected = data.find((f) => f.value === value);
    selectedValue = selected?.label ?? placeholder;
    selectedId = selected?.value ?? '';
    onSelectChange(selectedId, selectedValue);
    await closeAndFocusTrigger(ids.trigger);
  };

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
      class="w-[200px] justify-between bg-white text-primary hover:bg-accent hover:text-white {shadow
        ? 'shadow-custom'
        : ''}"
      role="combobox"
    >
      <p class="truncate">{selectedValue}</p>
      <ChevronsUpDown class="ml-2 h-4 w-4 shrink-0 opacity-50" />
    </Button>
  </Popover.Trigger>
  <Popover.Content class="overflow-x-auto w-[200px] p-0">
    <Command.Root shouldFilter={false}>
      <Command.Input bind:value={searchedValue} placeholder={searchPlaceholder} />
      <Command.Empty>{notFoundPlaceholder}</Command.Empty>
      {#each data as resource (resource.value)}
        <Command.Item value={resource.value} onSelect={(value) => handleSelect(value, ids)}>
          <Check class={cn('mr-2 h-4 w-4', selectedId !== resource.value && 'text-transparent')} />
          {resource.label}
        </Command.Item>
      {/each}
    </Command.Root>
  </Popover.Content>
</Popover.Root>
