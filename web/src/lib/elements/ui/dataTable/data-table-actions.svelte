<script lang="ts">
  import Ellipsis from 'lucide-svelte/icons/ellipsis';
  import * as DropdownMenu from '$lib/elements/ui/dropdown-menu';
  import Button from '$lib/elements/ui/button/button.svelte';
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';

  export let id: string;
  export let deleteEntry: (id: string) => Promise<void>;
  export let getData: () => Promise<void>;
</script>

<DropdownMenu.Root>
  <DropdownMenu.Trigger asChild let:builder>
    <Button variant="ghost" builders={[builder]} size="icon" class="relative h-8 w-8 p-0">
      <span class="sr-only">Open menu</span>
      <Ellipsis class="h-4 w-4" />
    </Button>
  </DropdownMenu.Trigger>
  <DropdownMenu.Content>
    <DropdownMenu.Group>
      <DropdownMenu.Label>Aktionen</DropdownMenu.Label>
      <DropdownMenu.Item on:click={() => navigator.clipboard.writeText(id)}>ID kopieren</DropdownMenu.Item>
    </DropdownMenu.Group>
    <DropdownMenu.Item on:click={() => goto($page.url + '/' + id)}>Editieren</DropdownMenu.Item>
    <DropdownMenu.Separator />
    <DropdownMenu.Item
      on:click={async () => {
        await deleteEntry(id);
        await getData();
      }}
      class="text-red-600">Löschen</DropdownMenu.Item
    >
  </DropdownMenu.Content>
</DropdownMenu.Root>
