<script lang="ts">
  import Ellipsis from 'lucide-svelte/icons/ellipsis';
  import * as DropdownMenu from '$lib/elements/ui/dropdown-menu';
  import Button from '$lib/elements/ui/button/button.svelte';
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import { toast } from '$lib/utils/resources';

  export let id: string;
  export let deleteEntries: (id: string[], additionalId?: string) => Promise<void>;
  export let getData: () => Promise<void>;
  export let editAvailable: boolean;
  export let additionalId: string;
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
    {#if editAvailable}
      <DropdownMenu.Item on:click={() => goto(`${$page.url}/${id}`)}>Editieren</DropdownMenu.Item>
    {/if}
    <DropdownMenu.Separator />
    <DropdownMenu.Item
      class="text-red-600"
      on:click={async () => {
        await deleteEntries([id], additionalId);
        await getData();
        toast(true, 'Eintrag erfolgreich gelöscht.');
      }}
    >
      Löschen
    </DropdownMenu.Item>
  </DropdownMenu.Content>
</DropdownMenu.Root>
