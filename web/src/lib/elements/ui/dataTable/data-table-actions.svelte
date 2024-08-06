<script lang="ts">
  import Ellipsis from 'lucide-svelte/icons/ellipsis';
  import * as DropdownMenu from '$lib/elements/ui/dropdown-menu';
  import Button from '$lib/elements/ui/button/button.svelte';
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import * as AlertDialog from '$lib/elements/ui/alert-dialog';

  export let id: string;
  export let deleteEntry: (id: string, additionalId?: string) => Promise<void>;
  export let getData: () => Promise<void>;
  export let editAvailable: boolean;
  export let additionalId: string;

  let alertOpen: boolean = false;
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
    <DropdownMenu.Item class="text-red-600" on:click={() => (alertOpen = true)}>Löschen</DropdownMenu.Item>
  </DropdownMenu.Content>
</DropdownMenu.Root>

<AlertDialog.Root bind:open={alertOpen}>
  <AlertDialog.Trigger />
  <AlertDialog.Content>
    <AlertDialog.Header>
      <AlertDialog.Title>Eintrag löschen?</AlertDialog.Title>
      <AlertDialog.Description
        >Sind Sie sicher, dass Sie diesen Eintrag löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden.</AlertDialog.Description
      >
    </AlertDialog.Header>
    <AlertDialog.Footer>
      <AlertDialog.Cancel class="shadow-custom">Abbrechen</AlertDialog.Cancel>
      <AlertDialog.Action
        on:click={async () => {
          await deleteEntry(id, additionalId);
          await getData();
        }}
        class="text-red-600 shadow-custom">Löschen</AlertDialog.Action
      >
    </AlertDialog.Footer>
  </AlertDialog.Content>
</AlertDialog.Root>
